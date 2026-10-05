# Phase 3: Spring MVC

Phase 2 created and wired Spring beans. Phase 3 connects those beans to HTTP
requests. The lesson is a small in-memory posts API, but the important subject
is the request pipeline: MVC receives an HTTP request, finds a controller
method, converts input, validates it, calls the service, and converts the
result into an HTTP response.

This phase uses Spring Framework 7.0.9 directly. Spring Boot and a real
embedded web server come later. The official Spring MVC documentation describes
annotated controllers as the programming model for mapping requests and
binding request data to method arguments; see the
[annotated controllers reference](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html).

## 1. The MVC request pipeline

The conceptual flow is:

```text
HTTP client
    ↓
DispatcherServlet
    ↓
Handler mapping finds a controller method
    ↓
Argument resolvers bind path/query/header/cookie/body data
    ↓
Bean Validation checks the request DTO
    ↓
Controller delegates to service
    ↓
Service applies business rules and calls repository
    ↓
Message converter serializes the response as JSON
    ↓
HTTP response
```

`DispatcherServlet` is the front controller for Spring MVC. It coordinates
mapping, argument resolution, validation, exception handling, and response
writing. A controller should describe the HTTP boundary, not contain all
business rules.

The lesson keeps the layers small and explicit:

- `PostController` owns HTTP mappings and request/response details.
- `PostService` owns rules such as duplicate titles and not-found behavior.
- `PostRepository` owns storage operations.
- `InMemoryPostRepository` provides deterministic seeded data until JPA is
  introduced.
- `Post`, `CreatePostRequest`, and `PostResponse` keep domain, input, and
  output shapes separate.

That separation means the repository can later be replaced by a JPA
implementation without forcing HTTP code to know about persistence.

## 2. HTTP concepts used by the lesson

HTTP methods communicate intent:

| Method | Lesson endpoint | Meaning | Typical success |
| --- | --- | --- | --- |
| GET | `/api/posts` | list/filter posts | `200 OK` |
| GET | `/api/posts/{id}` | read one post | `200 OK` |
| POST | `/api/posts` | create a post | `201 Created` |
| PUT | `/api/posts/{id}` | replace editable data | `200 OK` |
| PATCH | `/api/posts/{id}/publication` | change one part | `200 OK` |
| DELETE | `/api/posts/{id}` | remove a post | `204 No Content` |

The controller demonstrates several places where request data can live:

```java
@GetMapping
public PostListResponse list(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestHeader(value = "X-Client-Name", defaultValue = "anonymous") String clientName,
        @CookieValue(value = "course-mode", defaultValue = "standard") String courseMode) {
    // ...
}
```

- `@RequestParam` reads query parameters such as `?category=Backend`.
- `@PathVariable` reads a URI segment such as `/api/posts/1`.
- `@RequestHeader` reads metadata such as `X-Client-Name`.
- `@CookieValue` reads a named browser/client cookie.
- `HttpSession` stores server-side state across requests. The `/session`
  endpoint increments a lesson visit counter.
- `@RequestBody` asks an HTTP message converter to deserialize JSON into a Java
  object.

The method and status code are part of the API contract. `ResponseEntity` is
useful when the controller must choose the status, headers, and body together:

```java
return ResponseEntity.created(URI.create("/api/posts/" + saved.id()))
        .header("X-Client-Name", clientName)
        .body(PostResponse.from(saved));
```

The created response includes both `201 Created` and a `Location` header. A
delete response uses `204 No Content` because there is no response body to
serialize.

## 3. Controllers and JSON conversion

`@RestController` combines `@Controller` with response-body behavior. A method
return value is written to the response instead of being interpreted as a view
name. The class-level mapping creates a stable API prefix:

```java
@RestController
@RequestMapping(path = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {
    // @GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping
}
```

Spring MVC delegates JSON serialization and deserialization to HTTP message
converters. This project uses the Jackson 3 databind module with Spring 7's
Jackson 3 converter support; see the
[Spring MVC message-converter reference](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-config/message-converters.html).

The request and response records are DTOs. DTOs make the public HTTP contract
intentional: a client cannot accidentally choose internal fields just because
they exist on a persistence object. `PostResponse.from(Post)` is the explicit
mapping from domain data to API output.

## 4. Validation happens at the boundary

The create request uses Jakarta Bean Validation annotations:

```java
public record CreatePostRequest(
        @NotBlank String title,
        @NotBlank @Size(min = 10, max = 2000) String body,
        @NotBlank @Email String authorEmail,
        @NotBlank String category,
        boolean published) {
}
```

The controller opts into validation with `@Valid`:

```java
public ResponseEntity<PostResponse> create(
        @Valid @RequestBody CreatePostRequest request,
        ...) {
    // ...
}
```

Spring first converts JSON into `CreatePostRequest`, then invokes the Bean
Validation provider. A syntactically valid body with an empty title or invalid
email reaches validation and produces field errors. A malformed JSON document,
or a value that cannot be converted to the Java type, fails earlier during
message conversion and is a different class of error. The official
[`@RequestBody` reference](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/requestbody.html)
describes this conversion and validation sequence.

Some rules involve more than one field. The lesson's `@ValidPublication`
constraint is placed on the whole record: if `published` is true, the body
must contain at least 40 characters. The validator attaches the violation to
`body`, which keeps the client error useful even though the rule is
cross-field.

Validation is not a replacement for service rules. Bean Validation checks the
shape and basic constraints of an input. `PostService` still checks business
conditions such as duplicate titles and missing posts.

## 5. One error contract with controller advice

Without centralized handling, each controller can return a different error
shape. `GlobalExceptionHandler` uses `@RestControllerAdvice` to translate
exceptions from controllers and services into `ApiError`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed.",
  "path": "/api/posts",
  "fieldErrors": {
    "title": "Title is required."
  }
}
```

The lesson maps these cases:

| Situation | Status | Handler result |
| --- | ---: | --- |
| invalid DTO or request value | 400 | validation/read error |
| post does not exist | 404 | `PostNotFoundException` |
| title already exists | 409 | `DuplicatePostTitleException` |
| unexpected server failure | 500 | generic safe message |

The client receives stable messages and does not receive stack traces or raw
exception details. Unexpected exceptions are logged on the server for
diagnosis. The official [controller-advice reference](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html)
explains how advice applies exception handlers across controllers.

## 6. Testing MVC without a running server

`SpringMvcApplication` creates an `AnnotationConfigWebApplicationContext` and
registers the Phase 3 configuration. The example attaches a
`MockServletContext`, then builds `MockMvc` with `webAppContextSetup`:

```java
MockMvc mockMvc = MockMvcBuilders
        .webAppContextSetup(context)
        .build();
```

`MockMvc` drives the Spring MVC request pipeline through a mocked Servlet API.
It tests mappings, argument binding, JSON conversion, validation, controller
advice, status codes, headers, cookies, and sessions without opening a TCP
port. See the [official MockMvc overview](https://docs.spring.io/spring-framework/reference/testing/mockmvc/overview.html).

This is a deliberate phase boundary. The tests prove MVC behavior locally, but
they do not prove that a real embedded server is listening on a port. Phase 4
will add Spring Boot and provide the server startup path.

## 7. Run the phase

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The application prints representative GET, POST, validation, and response
handling examples after the Phase 1 and Phase 2 demonstrations. The test suite
also verifies list filtering, JSON creation, custom validation, update and
delete statuses, global error handling, and session state.

## 8. What this phase does not do yet

This is still a plain Spring Framework application:

- it does not start a real HTTP server;
- posts are stored in memory and disappear when the context closes;
- there is no Spring Boot auto-configuration;
- there is no database, Hibernate mapping, transaction boundary, or JPA
  repository.

Those are intentional next steps. Keeping the MVC pipeline visible now makes
the convenience provided by Spring Boot and the persistence behavior provided
by Hibernate/JPA easier to understand later.
