# Phase 2: Spring Core

Phase 1 showed how to construct objects directly. Phase 2 introduces Spring's
container and makes the object graph explicit: the application declares what
it needs, while the container creates objects, supplies collaborators, and
manages their lifecycle.

This lesson uses Spring Framework 7.0.9 directly. Spring Boot will be added
later; learning the underlying container first makes Boot's auto-configuration
less mysterious.

The official Spring reference describes the IoC container as the part of
Spring that manages beans, dependencies, scopes, annotation configuration, and
Java-based configuration. See the [official IoC container reference](https://docs.spring.io/spring-framework/reference/core/beans.html).

## 1. Inversion of control and the application context

Without a container, an application assembles its dependencies manually:

```java
GreetingFormatter formatter = new GreetingFormatter();
TimeSource clock = new SystemTimeSource();
GreetingService service = new GreetingService(formatter, clock, "Hello");
```

This is valid Java and remains useful in a unit test. In a Spring application,
the composition root registers the configuration and asks an
`ApplicationContext` for the finished service:

```java
try (AnnotationConfigApplicationContext context =
         SpringCoreApplication.createContext(Map.of("app.greeting-prefix", "Welcome"))) {
    GreetingService service = context.getBean(GreetingService.class);
}
```

Inversion of control means the application does not decide every construction
step inside each service. The container controls object creation and injects
the dependencies described by the configuration.

`ApplicationContext` is more than a simple map of objects. It creates beans,
resolves dependencies, applies post-processors, publishes framework events,
and closes managed resources when the context is closed.

## 2. Dependency injection

`GreetingService` uses constructor injection:

```java
public GreetingService(
        GreetingFormatter formatter,
        TimeSource timeSource,
        @Value("${app.greeting-prefix:Hello}") String prefix) {
    this.formatter = formatter;
    this.timeSource = timeSource;
    this.prefix = prefix;
}
```

Constructor injection has useful properties:

- required dependencies are visible in the constructor;
- fields can remain `final`;
- an object cannot be created in an incomplete state;
- a unit test can construct the service without starting Spring.

Spring also supports setter and field injection. They can be appropriate for
optional dependencies or framework integration, but constructor injection is a
strong default for required collaborators. Field injection hides the real
dependency list and makes direct tests less explicit.

The service depends on the `TimeSource` interface rather than a system clock.
The production configuration supplies `SystemTimeSource`; a unit test supplies
`FixedTimeSource`. This is the same dependency-inversion idea used in Phase 1,
now automated by Spring.

## 3. Beans and component scanning

A bean is an object managed by the Spring container. The lesson uses these
stereotypes:

- `@Component` marks a general component for discovery;
- `@Service` communicates that a component belongs to the service layer;
- `@Configuration` marks a class that declares bean definitions.

`@ComponentScan` searches selected packages for annotated classes. The lesson
uses `basePackageClasses` rather than a fragile string-only package name:

```java
@ComponentScan(basePackageClasses = {
    GreetingService.class,
    MethodLoggingAspect.class,
    LifecycleProbe.class
})
```

Scanning is convenient, but it is not magic. A class must be in a scanned
package, use a recognized stereotype, and have resolvable constructor
dependencies. A missing scan boundary or ambiguous implementation becomes a
container startup error.

## 4. Java configuration and `@Bean`

`@Bean` is useful when the object is third-party code, needs custom
construction, or should be selected explicitly:

```java
@Bean
public TimeSource timeSource() {
    return new SystemTimeSource();
}
```

The `SpringCoreConfiguration` class is the composition root for this phase.
It also registers a `PropertySourcesPlaceholderConfigurer` so the `@Value`
placeholder can read `app.greeting-prefix` and fall back to `Hello` when the
property is absent.

Configuration is executable application structure. Keep it small and make
important choices visible. Spring Boot will later provide conventional
configuration and auto-configuration, but the underlying bean definitions
remain the same idea.

## 5. Bean scopes

The default Spring singleton is one instance per bean definition in one
container. It is not the same as a GoF singleton that globally restricts a
class to one instance per class loader.

The lesson compares:

```java
context.getBean(GreetingService.class) ==
        context.getBean(GreetingService.class); // true

context.getBean(PrototypeMarker.class) !=
        context.getBean(PrototypeMarker.class); // true
```

`PrototypeMarker` is declared with `@Scope("prototype")`, so Spring creates a
new object each time the bean is requested. The container does not manage the
complete destruction lifecycle of prototype instances after handing them to
the caller; the caller owns cleanup for resources held by those objects. See
the [official bean scopes reference](https://docs.spring.io/spring-framework/reference/core/beans/factory-scopes.html).

Request, session, application, and WebSocket scopes require a web-aware
`ApplicationContext`. This phase intentionally uses a plain context, so those
web scopes belong with the Spring MVC phase rather than being simulated here.

## 6. Bean lifecycle

`LifecycleProbe` uses Jakarta annotations:

```java
@PostConstruct
public void initialize() {
    initialized = true;
}

@PreDestroy
public void destroy() {
    destroyed = true;
}
```

Spring calls `initialize` after dependencies have been injected and before the
bean is available for normal use. It calls `destroy` when the context closes
for managed singleton beans. A context should be closed with try-with-resources
in a command-line application and in tests.

Do not put long-running work or network calls casually in a constructor or
initialization callback. Startup work affects application availability and
should have an explicit failure and timeout policy.

## 7. Spring AOP

Aspect-oriented programming separates cross-cutting behavior such as logging,
transactions, metrics, and auditing from the core service method.

The lesson's `MethodLoggingAspect` uses three advice types:

```java
@Before("execution(* ...GreetingService.createGreeting(..))")
@Around("execution(* ...GreetingService.createGreeting(..))")
@After("execution(* ...GreetingService.createGreeting(..))")
```

- `@Before` runs before the target method;
- `@Around` can run code before and after `proceed()` and can alter or reject
  the call;
- `@After` runs after the method, including when it exits with an exception.

`@EnableAspectJAutoProxy` tells Spring to create a proxy for matching beans.
The caller receives the proxy, so the service call passes through the advice.
The test checks the recorded order:

```text
around enter → before → target method → after → around exit
```

Spring AOP is proxy-based. A method calling another advised method on `this`
does not pass through the proxy, so self-invocation does not trigger the
advice. Keep that limitation in mind when choosing service boundaries.

## 8. What this phase does not do yet

This is Spring Framework, not Spring Boot. There is no embedded web server,
`@SpringBootApplication`, REST controller, database, or JPA entity yet. The
purpose is to understand the container before those features add more
automation.

The dependency list is intentionally small:

```text
spring-context   ApplicationContext, configuration, component scanning
spring-aop       Spring proxy-based AOP support
aspectjweaver    @Aspect pointcut and advice support
jakarta.annotation-api  @PostConstruct and @PreDestroy
```

## Run the phase

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The next phase will place this container behind Spring MVC request handling:
controllers, HTTP methods, request binding, validation, and consistent error
responses.
