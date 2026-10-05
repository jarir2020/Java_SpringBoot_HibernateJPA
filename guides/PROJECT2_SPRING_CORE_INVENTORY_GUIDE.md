# Project 2 — Spring Core Inventory Management System

Project 2 is a small, in-memory inventory application built with Spring Core.
It is intentionally educational rather than production-grade. The project
shows how a container creates beans and connects a service to a repository.

## 1. What the project practices

```text
ApplicationContext
       ↓
InventoryService (@Service)
       ↓ constructor injection
InventoryRepository (interface)
       ↓
InMemoryInventoryRepository (@Repository)
```

The configuration object is supplied separately:

```text
@Configuration + @ComponentScan + @Bean
       ↓
InventoryProperties from @Value values
       ↓
InventoryService
```

The project covers:

- IoC: the application context creates and owns the service and repository;
- dependency injection: `InventoryService` receives both dependencies through
  its constructor;
- stereotypes: `@Service` and `@Repository` identify application roles;
- configuration: `@Configuration`, `@ComponentScan`, `@Bean`, and `@Value`;
- service/repository separation: business operations do not know the map
  implementation details;
- ordinary Java testing: the service can be tested without Spring;
- container testing: a focused test verifies scanning and configuration values.

## 2. Run the example

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --project2
```

The demo creates a context with a store name and low-stock threshold, adds
three products, records a sale, and prints all products plus low-stock items.

The full lesson runner remains available:

```bash
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

## 3. Important classes

| Class | Responsibility |
| --- | --- |
| `InventoryProduct` | Owns product data and valid stock changes. |
| `InventoryRepository` | Defines the storage boundary. |
| `InMemoryInventoryRepository` | Stores products in a `LinkedHashMap`. |
| `InventoryService` | Applies duplicate, lookup, sale, and low-stock rules. |
| `InventoryConfiguration` | Registers scanning and the properties bean. |
| `InventoryApplication` | Creates the plain `ApplicationContext`. |
| `InventoryLesson` | Runs a readable command-line demonstration. |

## 4. Why constructor injection is used

The service constructor makes its required dependencies visible:

```java
public InventoryService(
        InventoryRepository repository,
        InventoryProperties properties) {
    this.repository = repository;
    this.properties = properties;
}
```

The service can therefore be created directly in a unit test. Spring only
changes how those dependencies are assembled; it does not hide the class's
requirements.

## 5. What is deliberately out of scope

This project does not add a database, REST controllers, Spring Boot
auto-configuration, authentication, transactions, or distributed messaging.
Those topics belong to the later progression projects and would hide the
Spring Core bean boundary being practiced here.

## 6. Focused tests

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
  -Dtest=InventoryServiceTest,InventorySpringContextTest test
```

The first test creates the service with ordinary Java objects. The second
starts the small Spring context and verifies bean scope and `@Value`
configuration.

## 7. Exercises

1. Add a `removeProduct` operation to the repository and service.
2. Add a category field and a `findByCategory` service method.
3. Add a `Supplier` collaborator as a new constructor dependency.
4. Replace `InventoryProperties` with a dedicated configuration class for
   multiple stores.
5. Write a second repository implementation and choose it with a profile.

The next project will expose a small blog through Spring MVC and HTTP.
