# Phase 4: Spring Boot

Phase 3 assembled Spring MVC explicitly. Phase 4 introduces Spring Boot as the
application runtime: it chooses compatible dependencies, creates the web
application context, configures MVC, starts an embedded server, loads external
configuration, and provides production-oriented endpoints.

This repository uses Spring Boot 4.1.1. The official system requirements list
Java 17 as the minimum, Spring Framework 7.0.9 or newer, and Servlet 6.1
containers such as Tomcat 11; this project uses Java 21 as its course baseline.
See the [official Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html).

## 1. What Boot adds

Spring Boot is not a replacement for Spring Core or Spring MVC. It is a
convention and runtime layer around them:

```text
Spring Core beans and dependency injection
        +
Spring MVC request handling
        +
Boot starters and dependency management
        +
Auto-configuration and embedded server
        +
Configuration, health, and operational tooling
```

The Phase 4 application class is:

```java
@SpringBootApplication
@EnableConfigurationProperties(CourseProperties.class)
@Import({
        PostController.class,
        PostService.class,
        InMemoryPostRepository.class,
        GlobalExceptionHandler.class
})
public class SpringBootLearningApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootLearningApplication.class, args);
    }
}
```

`@SpringBootApplication` combines three ideas: Boot configuration,
auto-configuration, and component scanning. The official explanation shows
that it is equivalent in purpose to
`@SpringBootConfiguration`, `@EnableAutoConfiguration`, and
`@ComponentScan`; see the [official annotation reference](https://docs.spring.io/spring-boot/reference/using/using-the-springbootapplication-annotation.html).

This lesson imports the Phase 3 controller, service, repository, and advice
explicitly. That is intentional: the old `SpringMvcConfiguration` still
teaches manual MVC setup, while Boot configures the new application without
that hand-built `@EnableWebMvc` configuration.

## 2. Starters and dependency management

The POM uses the Spring Boot parent and focused starters:

```text
spring-boot-starter-webmvc       MVC, JSON, and embedded Tomcat
spring-boot-starter-validation   Jakarta Bean Validation integration
spring-boot-starter-actuator     health and operational endpoints
spring-boot-starter-test         Boot test support and JUnit integration
spring-boot-webmvc-test          Boot 4 MVC test auto-configuration
```

A starter is a dependency descriptor, not a library containing every feature.
It brings a compatible group of dependencies and lets Boot’s curated dependency
management choose their versions. The [official build-systems guide](https://docs.spring.io/spring-boot/reference/using/build-systems.html)
recommends Maven or Gradle and explains why Boot-managed versions should
normally not be repeated in the application POM.

This project retains explicit versions for the earlier direct Spring lessons so
their learning setup remains visible. The Boot starters use the Boot parent’s
compatible versions. The result is one executable archive containing all four
phases.

## 3. Auto-configuration and component boundaries

When the Boot application starts, it examines the classpath and user-defined
beans. With the web MVC starter present, it can configure:

- a servlet web application context;
- an embedded Tomcat web server;
- a `DispatcherServlet`;
- MVC handler mappings and JSON message conversion;
- multipart request handling;
- validation and error infrastructure;
- Actuator endpoint infrastructure.

Auto-configuration is conditional. It backs away when an application provides
its own bean or explicitly excludes a configuration. This is why Boot is
convenient without making the framework unknowable: the conditions report can
show which configuration matched and why.

The application class lives under `com.jarirahmed.springboot`, so Boot scans
the Boot-specific controller, configuration, storage, and error packages. The
Phase 3 components are imported explicitly. This keeps the two startup paths
easy to compare:

```text
SpringMvcApplication       → manual context + @EnableWebMvc + MockServletContext
SpringBootLearningApplication → SpringApplication + auto-configuration + Tomcat
```

## 4. External configuration and profiles

`src/main/resources/application.yml` contains defaults:

```yaml
spring:
  application:
    name: java-spring-learning

server:
  port: ${SERVER_PORT:8080}

course:
  name: ${COURSE_NAME:Spring Boot Course}
  mode: ${COURSE_MODE:boot}
```

The `${NAME:default}` syntax lets an environment variable override a safe
development default. Do not put passwords, API keys, or other credentials in
this file. Use environment variables or a proper secret-management system for
those values.

`CourseProperties` binds the namespace into a typed record:

```java
@ConfigurationProperties(prefix = "course")
public record CourseProperties(String name, String mode) {
}
```

The `dev` profile is in `application-dev.yml` and changes the course mode:

```bash
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar \
  --boot --spring.profiles.active=dev --server.port=8080
```

The `/api/boot/info` endpoint reports the resolved application name, course
properties, and active profiles. This makes configuration behavior observable
instead of leaving it as an invisible startup detail.

## 5. Embedded server and two run modes

The default course entry point is still `com.jarirahmed.javacore.Main`, which
preserves the incremental output from Phases 1–3. It has two modes:

```text
java -jar ...
    run all lessons
    start Boot on port 0
    call /api/boot/info and /actuator/health
    close the context

java -jar ... --boot --server.port=8080
    start the Boot application
    keep Tomcat running for manual HTTP requests
```

The first mode is a deterministic executable tutorial. The second mode is the
normal shape of a deployed Boot process. The official [embedded web server
guide](https://docs.spring.io/spring-boot/how-to/webserver.html) explains that
servlet applications receive an embedded server through the web starter and
can replace Tomcat with another supported container.

The short lesson uses `server.port=0`, which asks the operating system for an
available port. It then uses Java’s `HttpClient` to make actual network calls
and closes the `ConfigurableApplicationContext`, proving both startup and
shutdown behavior.

## 6. REST API improvements

The Phase 3 `/api/posts` CRUD API now also supports:

```text
GET /api/posts?category=Backend
GET /api/posts?published=true
GET /api/posts?search=container
GET /api/posts?page=0&size=10&sort=title,asc
```

The response includes:

```json
{
  "posts": [],
  "page": 0,
  "size": 10,
  "totalElements": 2,
  "totalPages": 1
}
```

The service applies search, sorting, and page slicing after the repository
returns the filtered in-memory data. That is easy to understand for this
phase, but it is not the final database strategy: a real repository should
push filtering, ordering, and pagination into SQL so it does not load an
unbounded table into memory.

The lesson also provides a deliberately small file API:

```text
POST /api/files     multipart field: file
GET  /api/files/{id}
```

`AttachmentStore` keeps bytes in a concurrent map, so uploads disappear when
the application stops. The upload returns `201 Created` and a `Location`
header. The download returns the stored content type, length, and attachment
filename. This demonstrates the HTTP contract only; persistent file storage,
size limits, antivirus scanning, authorization, and object storage belong in a
real application design.

## 7. Actuator health and info

The Actuator starter adds operational endpoints. The configuration exposes only
`health` and `info` under `/actuator`:

```bash
curl http://127.0.0.1:8080/actuator/health
curl http://127.0.0.1:8080/actuator/info
```

Health details are not exposed publicly by default in this lesson. In a real
deployment, protect operational endpoints and choose exposure and health-group
policies deliberately. A health endpoint is useful for orchestration, but
`200 UP` means the configured health indicators are healthy; it is not proof
that every business workflow is correct.

## 8. Testing Boot applications

`SpringBootApplicationTest` uses:

```java
@SpringBootTest(classes = SpringBootLearningApplication.class)
@AutoConfigureMockMvc
class SpringBootApplicationTest {
}
```

This starts a Boot application context and lets the test use MockMvc. It checks
Boot-specific configuration binding, the existing posts API, Actuator, query
pagination, multipart upload, and binary download. It does not require a
network port.

The separate `SpringBootLesson` starts a real embedded server. This gives the
tutorial two complementary boundaries:

```text
MockMvc tests → application context and request pipeline
HttpClient smoke → actual embedded server and TCP request
```

The official [Boot application testing guide](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
describes `@SpringBootTest` and the use of `@AutoConfigureMockMvc` when a full
Boot context should be tested without a live server.

## 9. What this phase does not do yet

Phase 4 deliberately leaves these topics for later:

- posts and uploaded bytes are stored only in memory;
- there is no SQL database or migration system;
- there are no Hibernate entities or JPA repositories;
- file uploads have no authentication, authorization, durable storage, or
  production content scanning;
- Actuator is configured for a learning environment, not exposed as a
  production security policy.

Phase 5 will establish relational SQL concepts. Later Hibernate and JPA phases
will replace the in-memory repository with persistence and move pagination and
filtering into database queries.

## Run the phase

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --boot --server.port=8080
```
