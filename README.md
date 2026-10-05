# Java, Spring Boot, Hibernate, and JPA learning track

This repository follows [`plan.md`](plan.md) as a theory-first, incremental
course. Each phase is intended to contain small runnable examples, repeatable
tests, explanatory comments, and a matching theory guide.

The course begins with plain Java because Spring annotations make more sense
when the language, object model, collections, exceptions, and build lifecycle
are already familiar. The eventual relationship is:

```text
Java → Spring Core → Spring MVC → Spring Boot → Hibernate/JPA
```

## Phase 1: Java Foundation

Phase 1 uses a small employee-management domain to demonstrate:

- variables, types, operators, conditions, loops, arrays, input normalization,
  and console output;
- classes, constructors, encapsulation, inheritance, polymorphism,
  abstraction, interfaces, composition, and access modifiers;
- `String`, `StringBuilder`, wrapper classes, `Math`, date/time, regular
  expressions, and enums;
- `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`, `HashMap`, `TreeMap`,
  queues, deques, iterators, `Comparable`, and `Comparator`;
- checked and unchecked exceptions, custom exceptions, generics and
  wildcards;
- lambdas, functional interfaces, method references, streams, `Optional`,
  executors, synchronization, and `CompletableFuture`;
- the Maven project structure and test lifecycle.

Run the lesson application:

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Run the tests:

```bash
mvn test
```

Read the explanation in
[`guides/PHASE1_THEORY_QUICK_GUIDE.md`](guides/PHASE1_THEORY_QUICK_GUIDE.md).

## Phase 2: Spring Core

Phase 2 adds the framework layer directly, without Spring Boot hiding the
container. It demonstrates:

- inversion of control, `ApplicationContext`, beans, component scanning, and
  Java configuration;
- constructor injection, `@Component`, `@Service`, `@Bean`, `@Value`, and
  property-based configuration;
- singleton and prototype scopes;
- `@PostConstruct` and `@PreDestroy` lifecycle callbacks;
- Spring AOP proxies with `@Before`, `@After`, and `@Around` advice.

The existing runnable application now prints both phases. Build and run it
with the same commands:

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Run all tests:

```bash
mvn test
```

Read the explanation in
[`guides/PHASE2_THEORY_QUICK_GUIDE.md`](guides/PHASE2_THEORY_QUICK_GUIDE.md).

## Phase 3: Spring MVC

Phase 3 puts the Spring container behind a small HTTP API. The lesson uses an
in-memory blog/post domain so the MVC request pipeline is visible before a
database or Spring Boot is introduced. It demonstrates:

- `@RestController`, request mappings, and the controller/service/repository
  separation;
- GET, POST, PUT, PATCH, and DELETE requests;
- query parameters, path variables, request headers, cookies, sessions, and
  JSON request/response bodies;
- `ResponseEntity`, location headers, status codes, and DTOs;
- Jackson JSON message conversion;
- Bean Validation with standard constraints and a custom cross-field
  `@ValidPublication` constraint;
- `@RestControllerAdvice` with one predictable JSON error shape for validation,
  not-found, conflict, malformed-request, and unexpected failures.

The executable lesson uses Spring's `MockMvc`. This exercises the MVC
`DispatcherServlet` and request pipeline without starting a network server, so
the example remains a fast command-line tutorial. Spring Boot will add the
embedded-server startup path in a later phase.

Build, run, and test all three phases with the same commands:

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
mvn test
```

Read the explanation in
[`guides/PHASE3_THEORY_QUICK_GUIDE.md`](guides/PHASE3_THEORY_QUICK_GUIDE.md).

The next phase will introduce Spring Boot, auto-configuration, externalized
application configuration, and a real embedded web server.

## Phase 4: Spring Boot

Phase 4 adds the Spring Boot ecosystem on top of the Phase 3 MVC API. It uses
Spring Boot 4.1.1 with the current Spring Framework 7 baseline. It demonstrates:

- `@SpringBootApplication`, component scanning, auto-configuration, and Boot
  starter dependencies;
- an executable archive with embedded Tomcat;
- `application.yml`, environment-variable overrides, typed
  `@ConfigurationProperties`, and the `dev` profile;
- Actuator health and info endpoints;
- the existing CRUD API running under Boot auto-configuration;
- filtering, searching, sorting, and paginated post responses;
- multipart file upload and binary download using temporary in-memory storage.

The normal JAR command runs the earlier lessons and starts Boot briefly on an
ephemeral port to make an HTTP request to `/api/boot/info` and
`/actuator/health`, then shuts the context down. To leave a real local server
running for manual requests, use:

```bash
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar \
  --boot --server.port=8080
```

Then try:

```bash
curl http://127.0.0.1:8080/api/boot/info
curl http://127.0.0.1:8080/actuator/health
curl 'http://127.0.0.1:8080/api/posts?search=container&page=0&size=10&sort=title,asc'
```

Build and test the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE4_THEORY_QUICK_GUIDE.md`](guides/PHASE4_THEORY_QUICK_GUIDE.md).

The next phase will establish the SQL and relational-database foundation
before Hibernate and JPA replace the in-memory repository.

## Phase 5: SQL and Database Foundation

Phase 5 establishes the relational-database concepts that Hibernate and JPA
will build upon. It deliberately uses plain JDBC and SQL first:

- normalized `department`, `employee`, `project`, and junction-table schema;
- primary keys, foreign keys, unique/check/not-null constraints;
- joins, views, aggregates, and a scalar subquery;
- indexes on foreign-key columns;
- prepared statements and JDBC resource handling;
- an explicit commit/rollback transaction example.

The lesson uses an embedded H2 database in PostgreSQL-compatible learning mode
so it runs without a separate server. The database is temporary and resets
when the process ends; it is not a production persistence choice.

Run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE5_THEORY_QUICK_GUIDE.md`](guides/PHASE5_THEORY_QUICK_GUIDE.md).

Phase 6 will introduce Hibernate's ORM concepts using this SQL foundation.

## Phase 6: Hibernate ORM

Phase 6 adds native Hibernate ORM on top of the Phase 5 relational model. It
demonstrates:

- annotated entities and generated identifiers;
- `SessionFactory`, `Session`, and explicit transactions;
- transient, managed, detached, and removed lifecycle concepts;
- dirty checking and the first-level persistence-context cache;
- `@OneToMany`, `@ManyToOne`, cascade, orphan removal, and a composite-key
  association entity for the employee/project many-to-many relationship;
- HQL queries, lazy loading, pagination, and the N+1 problem;
- statement-count evidence comparing lazy navigation with `join fetch`.

This phase uses Hibernate's native `Session` API. It does not introduce
`EntityManager`, Spring transactions, or Spring Data repositories yet. The
database is a disposable H2 `create-drop` database for learning.

Build and run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE6_THEORY_QUICK_GUIDE.md`](guides/PHASE6_THEORY_QUICK_GUIDE.md).

Phase 7 will distinguish the JPA specification from Hibernate and introduce
the standard `EntityManager` API.

## Phase 7: Jakarta Persistence (JPA)

Phase 7 separates the standard JPA API from the Hibernate provider. It uses
the same Phase 6 entity mappings but switches application code to:

- `persistence.xml`, `EntityManagerFactory`, and `EntityManager`;
- persistence-context identity and `persist`/`clear`/`merge` lifecycle;
- JPQL and Criteria API queries;
- resource-local transactions;
- `@Version` optimistic locking;
- standard entity graphs and pagination.

This phase does not add Spring Data JPA yet. The goal is to understand JPA
before introducing repository abstractions such as `JpaRepository`.

Run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE7_THEORY_QUICK_GUIDE.md`](guides/PHASE7_THEORY_QUICK_GUIDE.md).

Phase 8 will connect persistence work to Spring transaction management.

## Phase 8: Spring Transactions

Phase 8 connects the JPA `EntityManager` to Spring's declarative transaction
infrastructure. It demonstrates:

- `@EnableTransactionManagement` and AOP transaction proxies;
- `JpaTransactionManager` and `@PersistenceContext`;
- commit and rollback boundaries;
- default runtime-exception rollback and explicit `rollbackFor`;
- `REQUIRED` and `REQUIRES_NEW` propagation;
- read-only transactions, isolation declarations, and lazy loading within a
  service transaction.

This phase uses explicit Spring configuration rather than Boot or Spring Data
auto-configuration so the transaction manager remains visible.

Run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE8_THEORY_QUICK_GUIDE.md`](guides/PHASE8_THEORY_QUICK_GUIDE.md).

## Phase 9: DTOs and API Architecture

Phase 9 places a professional API boundary in front of the Phase 8 JPA and
transaction layers. The employee API demonstrates:

- request DTOs with Bean Validation constraints;
- response DTOs that prevent JPA entities and lazy relationships from leaking
  into JSON;
- explicit manual mapping before introducing MapStruct;
- controller, service, and repository responsibilities;
- transactional service methods using the existing JPA `EntityManager`;
- a success envelope with pagination metadata;
- stable `400`, `404`, and `409` error responses for validation and domain
  failures.

The request flow is:

```text
HTTP request
    ↓
EmployeeController
    ↓
EmployeeService (@Transactional)
    ↓
EmployeeRepository (EntityManager/JPQL)
    ↓
HibernateEmployee entity
```

The API intentionally uses ordinary Java records and a visible `from(...)`
mapper. MapStruct and Lombok are not added yet; understanding the generated
code they would replace comes first.

Run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE9_THEORY_QUICK_GUIDE.md`](guides/PHASE9_THEORY_QUICK_GUIDE.md).

## Phase 10: Spring Security

Phase 10 places Spring Security in front of the Phase 9 employee API. It
demonstrates:

- username/password authentication through `UserDetailsService`;
- BCrypt password hashing with `PasswordEncoder`;
- the servlet `SecurityFilterChain` and HTTP Basic authentication;
- role-based endpoint authorization for `USER` and `ADMIN`;
- `@EnableMethodSecurity` with `@PreAuthorize`;
- stateless session policy for a JSON API;
- JSON `401 Unauthorized` and `403 Forbidden` responses instead of HTML
  redirects.

The local lesson users are intentionally in-memory teaching fixtures:

```text
reader / reader-password  → ROLE_USER
admin  / admin-password   → ROLE_USER, ROLE_ADMIN
```

They are not production credentials. Database-backed users, login endpoints,
JWT access/refresh tokens, OAuth2, and OpenID Connect remain future topics.

Run the complete course:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

Read the explanation in
[`guides/PHASE10_THEORY_QUICK_GUIDE.md`](guides/PHASE10_THEORY_QUICK_GUIDE.md).

Phase 11 will introduce testing as a first-class backend practice.

## Phase 11: Testing

Phase 11 treats testing as part of backend architecture. It demonstrates:

- JUnit assertions and exception testing;
- Mockito mocks and interaction verification for fast unit tests;
- `@SpringBootTest` and `@AutoConfigureMockMvc` integration tests;
- real controller, validation, transaction, JPA, and disposable H2 behavior;
- API contract checks for status codes, JSON fields, and `Location` headers;
- a disabled Testcontainers PostgreSQL template for Docker-backed integration
  tests.

The testing boundaries are:

```text
unit rule → mocked port
integration test → Spring context + MockMvc + H2
API test → public HTTP contract
```

Run the focused Phase 11 tests:

```bash
mvn -Dtest=EmployeeLabelServiceTest,Phase11SpringBootIntegrationTest test
```

Read the explanation in
[`guides/PHASE11_THEORY_QUICK_GUIDE.md`](guides/PHASE11_THEORY_QUICK_GUIDE.md).

## Phase 12: Production / Advanced Spring

Phase 12 is the final roadmap phase. It connects the course to production
concerns while keeping the default lesson runnable without external brokers or
cloud credentials. It demonstrates:

- Spring Cache with measurable cache hits and explicit eviction;
- named asynchronous work with a bounded task executor;
- scheduled background work and the operational risks of duplicate jobs;
- the architecture boundary between local async work and RabbitMQ/Kafka;
- Redis, microservice, reverse-proxy, Docker, and CI/CD deployment guidance;
- configuration, health, observability, graceful shutdown, and secret hygiene.

The runnable Phase 12 lab is:

```text
cache → async executor → scheduled job → deployable application
```

Run the focused final-phase test:

```bash
mvn -Dtest=ProductionAdvancedExamplesTest test
```

Build the container example when Docker is available:

```bash
docker build -t java-spring-learning .
docker run --rm -p 8080:8080 java-spring-learning
```

Read the explanation in
[`guides/PHASE12_THEORY_QUICK_GUIDE.md`](guides/PHASE12_THEORY_QUICK_GUIDE.md).

# Java_SpringBoot_HibernateJPA
