# Phase 9 — DTOs and API Architecture

Phase 9 puts a public HTTP contract in front of the JPA model from Phase 8.
The example uses employees because the existing entity relationship makes the
most important boundary visible:

```text
HTTP JSON
    ↓
request DTO → controller → service → repository → entity
    ↑              ↓          ↓          ↓
response DTO ← mapping ← transaction ← EntityManager/JPQL
```

The API lives under `com.jarirahmed.springapi` and is exercised with Spring
MVC `MockMvc`, so the lesson tests the request pipeline without starting a
network server.

## 1. Entity versus DTO

`HibernateEmployee` is a persistence entity. It has database mappings,
relationships, a version field, and a lazy assignments collection. Those are
useful to Hibernate but are not automatically a public API contract.

`EmployeeResponse` is a response DTO containing only the fields this API has
chosen to expose:

```java
public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        BigDecimal salary,
        LocalDate hiredOn,
        String departmentName,
        long version) {
}
```

Returning the entity directly would couple the API to persistence mappings and
could trigger lazy loading or recursive relationship serialization. Mapping to
a DTO inside the transaction makes the boundary explicit and safe.

## 2. Request DTO and validation

`CreateEmployeeRequest` represents input, not a database row. It uses standard
Bean Validation annotations:

- `@NotBlank` and `@Size` for text;
- `@Email` for the email format;
- `@NotNull`, `@DecimalMin`, and `@Digits` for salary;
- `@PastOrPresent` for the hire date;
- `@Valid` on the controller request body to activate validation.

The controller does not manually inspect every field. Spring MVC binds JSON to
the request DTO, invokes validation, and sends `MethodArgumentNotValidException`
to `EmployeeApiExceptionHandler`. The handler converts field errors into the
same stable `ApiError` shape used by the earlier MVC phase.

Validation is an input boundary, not a replacement for domain rules. The
service still checks that the department exists and that the email is unique.

## 3. Controller layer

`EmployeeController` owns HTTP concerns:

- URL mapping under `/api/employees`;
- query parameters for department filtering and pagination;
- JSON request and response media types;
- `201 Created` and the `Location` header after creation;
- the success response envelope.

It does not construct JPA entities, write JPQL, or decide how transactions
work. That separation keeps the controller small and makes the service
reusable from another adapter such as a scheduled job or message consumer.

Example success response:

```json
{
  "message": "Employee created.",
  "data": {
    "id": 4,
    "fullName": "David Ahmed",
    "email": "david@example.com",
    "salary": 7000.00,
    "hiredOn": "2025-01-20",
    "departmentName": "Engineering",
    "version": 0
  }
}
```

The list endpoint returns `data.items` plus `page`, `size`,
`totalElements`, and `totalPages`. Pagination metadata is part of the API
contract rather than an accidental detail of a repository query.

## 4. Service layer

`EmployeeService` is the application layer. It:

1. normalizes values such as email and department name;
2. checks duplicate email and department existence;
3. constructs the entity;
4. coordinates the entity relationship;
5. maps entities to response DTOs;
6. owns the `@Transactional` boundary.

The read methods use `@Transactional(readOnly = true)`. The create method uses
a normal transaction so the new employee and its department relationship are
written atomically. DTO mapping occurs while the transaction is open, which
also makes the joined department name safe to read.

## 5. Repository layer

`EmployeeRepository` is intentionally a small hand-written repository around
`EntityManager`. It contains:

- `findById` using the persistence context;
- a case-insensitive department lookup;
- a case-insensitive duplicate-email check;
- a filtered JPQL page query with a `join fetch` for the department;
- a matching count query.

This phase does not introduce Spring Data JPA. The point is to understand the
responsibility that `JpaRepository` would later automate. Spring Data can be
added after the entity/DTO/service boundary is understood.

## 6. API response and errors

Successful operations use the generic `ApiResponse<T>` envelope:

```java
public record ApiResponse<T>(String message, T data) {
}
```

Expected failures use the existing `ApiError` contract:

- `400 Bad Request` for malformed JSON, invalid fields, and invalid paging;
- `404 Not Found` for an employee or department that does not exist;
- `409 Conflict` for a duplicate employee email.

The API does not expose stack traces, SQL, or internal exception details to the
client. A production application would also add correlation IDs, structured
logging, and a versioning policy, but those are outside this phase.

## 7. Why manual mapping comes first

`EmployeeResponse.from(employee)` is deliberately ordinary Java code. It makes
the direction and field choices visible while learning:

```java
EmployeeResponse.from(employee)
```

MapStruct can later generate this repetitive code at compile time. Lombok can
later reduce boilerplate in classes that need it. Neither tool should hide the
design decision about which entity fields become public API fields.

## 8. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The Phase 9 output shows a successful create, a paginated filtered read, and a
validation failure. The focused tests cover DTO mapping, transaction proxying,
pagination, not-found handling, validation, and conflict handling.

The main lesson is
[`SpringApiLesson.java`](../src/main/java/com/jarirahmed/springapi/demo/SpringApiLesson.java).
The HTTP layer is
[`EmployeeController.java`](../src/main/java/com/jarirahmed/springapi/controller/EmployeeController.java),
the application layer is
[`EmployeeService.java`](../src/main/java/com/jarirahmed/springapi/service/EmployeeService.java),
and the persistence layer is
[`EmployeeRepository.java`](../src/main/java/com/jarirahmed/springapi/repository/EmployeeRepository.java).

## What Phase 9 does not teach yet

- no MapStruct-generated mapper;
- no Lombok-generated constructors/getters;
- no Spring Data `JpaRepository`;
- no authentication or authorization;
- no database-backed production connection pool;
- no API versioning or OpenAPI generation.

Phase 10 will add Spring Security around this already-separated API boundary.
