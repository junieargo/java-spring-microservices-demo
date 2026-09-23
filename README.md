<<<<<<< HEAD
# Microservices Test Project

Java microservices system built with Spring Boot and Spring Cloud, submitted for the Software Engineer technical test. It implements service discovery, centralized configuration, an API gateway, two CRUD services communicating via Feign, JWT based API security, Swagger documentation, and unit tests.

## 1. Tech Stack Overview

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Core framework | Spring Boot 3.2.5 |
| Microservices | Spring Cloud 2023.0.1 (Leyton release train) |
| Service discovery | Spring Cloud Netflix Eureka |
| API gateway | Spring Cloud Gateway |
| Centralized config | Spring Cloud Config Server (native profile) |
| Inter service calls | Spring Cloud OpenFeign |
| Persistence | Spring Data JPA, H2 (in memory) |
| CRUD exposure | Spring Data REST |
| Security | Spring Security, OAuth2 Resource Server, JWT (jjwt) |
| API docs | springdoc openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, AssertJ |
| Build tool | Maven, multi module |

## 2. Why This Stack

Java and Spring Boot were mandated by the test spec, so the real decisions were inside that constraint.

Spring Cloud Netflix Eureka was chosen over a static service list because the whole point of the exercise is demonstrating dynamic discovery: the gateway and order-service resolve product-service by name (lb://PRODUCT-SERVICE), not by a hardcoded host and port. This is also the discovery mechanism the spec explicitly names.

Spring Cloud Config with the native profile (local files instead of a remote Git backend) keeps the "centralized configuration" requirement genuinely centralized (every service pulls jwt.secret and the Eureka URL from one place) without adding a dependency on an external Git host to run the demo. In a real deployment, only the search-locations property in config-service would need to change to point at a private repository; nothing in the client services would change.

H2 in memory was chosen over PostgreSQL or MySQL specifically to remove setup friction for whoever is evaluating this: clone, run, done, no database installation or docker-compose required. The tradeoff is honest: this is not how you would run it in production, and the README says so rather than pretending otherwise.

For the OAuth2 requirement, this project does not stand up a full Keycloak or Spring Authorization Server instance. Instead, a small auth-service issues HMAC signed JWTs, and product-service and order-service validate them locally as OAuth2 resource servers using the shared secret from config-service. This demonstrates the actual mechanism the spec is testing (JWT based resource server security) without the operational overhead of a full identity provider. The honest next step toward production readiness is documented directly in code comments: swap auth-service's controller for Spring Authorization Server or an external IdP, and the resource server side does not need to change at all.

Spring Data REST was used for both CRUD services to satisfy the spec directly. For the one place where auto generated CRUD would be actively wrong (creating an order without validating it against live product data), Data REST's write operations are explicitly disabled on that repository and replaced with a dedicated, validated endpoint. That decision, and the reasoning behind it, is documented as a comment directly on OrderRepository.

## 3. Architecture

```
                        ┌────────────────────┐
                        │   discovery-service │  (Eureka, port 8761)
                        └─────────┬───────────┘
                                  │ registers
      ┌───────────────┬──────────┼───────────────┬────────────────┐
      │               │          │               │                │
┌─────▼─────┐   ┌─────▼─────┐  ┌─▼───────────┐ ┌─▼────────────┐ ┌─▼──────────┐
│  gateway   │   │   auth     │  │product-svc  │ │  order-svc   │ │config-svc  │
│  :8080     │   │  :8090     │  │  :8081      │ │   :8082      │ │  :8888     │
└─────┬──────┘   └────────────┘  └──────┬──────┘ └───────┬──────┘ └────────────┘
      │  routes                          │  Feign call    │
      └─── /api/products/** ─────────────┘◄────────────────┘
      └─── /api/orders/**    ─────────────────────────────►
      └─── /auth/**          ─────► auth-service
```

All services except discovery-service and config-service pull shared settings (Eureka URL, JWT secret) from config-service on startup.

## 4. Module Guide

- discovery-service: Eureka registry. Every other service registers here.
- config-service: Serves shared and per-service configuration from config-repo/ (native, file based, no Git required for this demo).
- gateway-service: Single entry point. Routes /api/products/**, /api/orders/**, and /auth/** to the matching service via Eureka.
- auth-service: POST /auth/login with a username and password from an in memory user list, returns a signed JWT. Demo users: admin/admin123, jonis/jonis123.
- product-service: Data REST CRUD at /api/products (GET is public, writes need a JWT). Also exposes GET /api/products/{id}/details as a plain JSON endpoint for internal Feign calls.
- order-service: Data REST read only browsing at /api/orders (GET). Order creation only happens through POST /api/orders/place, which validates stock and current price against product-service before saving.

## 5. Known Simplifications (stated up front rather than discovered later)

- Placing an order does not decrement the product's stock in product-service. Doing that correctly across two services needs either a distributed transaction or a saga/compensation flow, which is a reasonable follow up discussion point but out of scope for this test.
- auth-service is a minimal token issuer, not a full OAuth2 authorization server (no client registration, no grant types, no refresh tokens).
- The JWT signing secret lives in a plaintext config file for this demo. In a real deployment it belongs in a secrets manager or vault, injected as an environment variable.
- I was not able to run `mvn install` against Maven Central in the environment where this was written, so the build has not been verified by an actual compile. Please build locally first; if you hit an error, it is most likely a version mismatch or a typo, not a fundamental design problem, and I can help fix it from the exact stack trace.

## 6. Setup Instructions

### Prerequisites

- JDK 17 or newer
- Maven 3.9 or newer (or use your IDE's bundled Maven)
- Ports 8080, 8081, 8082, 8090, 8761, 8888 free on localhost

### Build (Linux, macOS, or Windows)

From the project root:

```
mvn clean install
```

This builds all six modules. Windows users can run the exact same command from PowerShell or cmd; there is no OS specific step in this project.

### Run (order matters)

Start each service in its own terminal, in this order, waiting a few seconds between each so registration completes:

```
# 1. Discovery must come up first
cd discovery-service && mvn spring-boot:run

# 2. Config server next, since every other service depends on it
cd config-service && mvn spring-boot:run

# 3. Then the rest, any order among these three
cd auth-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run

# 4. Gateway last
cd gateway-service && mvn spring-boot:run
```

On Windows, the same commands work in PowerShell or Command Prompt.

Confirm everything registered by opening http://localhost:8761 (Eureka dashboard) and checking that DISCOVERY-SERVICE, GATEWAY-SERVICE, AUTH-SERVICE, PRODUCT-SERVICE, and ORDER-SERVICE are all listed as UP.

### Try It

Get a token:

```
curl -X POST http://localhost:8090/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Browse the product catalog (public, no token needed):

```
curl http://localhost:8081/api/products
```

Place an order (needs the token from the login call above):

```
curl -X POST http://localhost:8082/api/orders/place \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <paste token here>" \
  -d '{"productId":1,"quantity":2}'
```

Everything above also works routed through the gateway on port 8080 instead of hitting each service directly, for example http://localhost:8080/api/products.

### API Documentation

Swagger UI is available per service once it is running:

- http://localhost:8081/swagger-ui.html (product-service)
- http://localhost:8082/swagger-ui.html (order-service)

### Running Tests

```
mvn test
```

Tests run against an in memory H2 database and mocked Feign clients, so they do not require any other service to be running.

## 7. Git

```
git init
git add .
git commit -m "Initial commit: microservices test submission"
git remote add origin <your-repository-url>
git push -u origin main
```

Remember to set the repository visibility to public before sharing the link, per the test instructions.
=======
# java-spring-microservices-demo
Java Spring Cloud microservices system featuring Eureka Service Discovery, API Gateway, Config Server, and Spring Data REST with OpenFeign.
>>>>>>> 1d336f37138816ecb39ab215474c0d80b5a383e7
