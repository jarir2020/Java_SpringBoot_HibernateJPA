# Phase 11 — Testing

Testing is part of backend design. A test should make the boundary being
verified obvious: one class, one Spring application slice, or the complete
HTTP-and-database path.

## 1. The testing pyramid

This course uses three useful levels:

```text
many fast unit tests
        ↓
fewer Spring integration tests
        ↓
a small number of full API or system tests
```

The levels are complementary. A unit test gives a precise failure and quick
feedback. An integration test catches wiring, serialization, validation,
transactions, and persistence problems. An API test checks the contract that
an external client sees.

## 2. Unit testing with JUnit and Mockito

[`EmployeeLabelService`](../src/main/java/com/jarirahmed/testing/EmployeeLabelService.java)
has one application rule: format an employee label. Its
[`EmployeeDirectoryPort`](../src/main/java/com/jarirahmed/testing/EmployeeDirectoryPort.java)
is a boundary for data access.

[`EmployeeLabelServiceTest`](../src/test/java/com/jarirahmed/testing/EmployeeLabelServiceTest.java)
uses Mockito to replace that boundary:

```java
when(directory.findById(7)).thenReturn(Optional.of(employee));
assertEquals("Nadia Karim [Operations]", service.labelFor(7));
verify(directory).findById(7);
```

The test does not start Spring, open H2, or send HTTP. That makes it fast and
keeps a formatting failure separate from a database or configuration failure.
Mockito is supplied by `spring-boot-starter-test`; the test is not production
code and the mock is not a replacement for an integration test.

Useful JUnit assertions include:

- `assertEquals` for a returned value;
- `assertTrue` and `assertFalse` for a condition;
- `assertThrows` for an expected failure;
- Mockito `verify` for an interaction with a dependency.

## 3. Spring Boot integration testing

[`Phase11SpringBootIntegrationTest`](../src/test/java/com/jarirahmed/testing/Phase11SpringBootIntegrationTest.java)
uses:

- `@SpringBootTest` to start a real Spring application context;
- `@AutoConfigureMockMvc` to send requests without binding a network port;
- the Phase 9 controller, service, repository, transaction manager, and JPA;
- a disposable H2 database seeded through `SpringTransactionService`.

The path under test is:

```text
MockMvc request
    ↓
Spring MVC + validation
    ↓
EmployeeController
    ↓
EmployeeService + transaction proxy
    ↓
EmployeeRepository + EntityManager
    ↓
H2 database
```

This level catches problems that a Mockito unit test cannot see: missing bean
definitions, incorrect request mapping, JSON conversion, validation status,
transaction wiring, JPQL, and persistence behavior.

The test uses a test-only Boot composition root. That keeps the Phase 9 API
configuration explicit and excludes Phase 10 security so the test focuses on
the testing boundary. Security behavior remains covered by the Phase 10
MockMvc tests.

## 4. API testing

`MockMvc` is useful for repeatable automated API tests inside the JVM. A REST
client such as Postman or an HTTP client is also valuable because it behaves
like an external consumer. Test the public contract rather than database
implementation details:

- request method and URL;
- request headers and JSON body;
- status code;
- response headers such as `Location`;
- stable response fields and error shapes.

For a running Boot server, these example requests exercise the Phase 4 API:

```bash
curl http://127.0.0.1:8080/api/boot/info
curl http://127.0.0.1:8080/actuator/health
```

The Phase 9 employee API is intentionally demonstrated through MockMvc in
this phase because it uses an explicit disposable JPA context rather than the
Phase 4 Boot server's in-memory post application.

## 5. Testcontainers

Testcontainers starts disposable Docker containers for integration tests. It
is useful when H2 is not close enough to the production database behavior,
for example when PostgreSQL-specific SQL, indexes, extensions, or transaction
semantics matter.

[`Phase11TestcontainersTemplateTest`](../src/test/java/com/jarirahmed/testing/Phase11TestcontainersTemplateTest.java)
shows the JUnit 5 annotations and PostgreSQL container declaration. It is
disabled by default because it requires Docker and a deliberate test schema;
it does not silently download or start infrastructure during the normal
course build.

## 6. Run the phase

From the repository root:

```bash
mvn -Dtest=EmployeeLabelServiceTest,Phase11SpringBootIntegrationTest test
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The default build skips the disabled Testcontainers template. After Docker,
database schema, and application properties are configured, remove or override
the `@Disabled` annotation and run that test separately.

## What Phase 11 does not teach yet

- contract testing with Pact or Spring Cloud Contract;
- mutation testing and coverage thresholds;
- performance, load, and soak testing;
- CI test reports and parallel test isolation;
- production database migrations and full Testcontainers application wiring.

Those topics build on the boundaries and test levels introduced here.
