# Phase 1: Java Foundation

This phase builds the language foundation needed before Spring. The runnable
examples live under `src/main/java`, and the tests under `src/test/java` check
the same behaviors without relying on manual console inspection.

## 1. Variables, types, operators, and control flow

Every Java variable has a declared type. Primitive types store simple values;
reference types refer to objects.

```java
int employeeId = 42;
double hours = 40.0;
boolean active = true;
char grade = 'A';
String name = "Jarir";
```

Arithmetic and comparison operators can be combined with `if`, `switch`, and
loops. A `switch` expression can return a value:

```java
String label = switch (status) {
    case ACTIVE -> "Currently employed";
    case ON_LEAVE -> "Temporarily away";
    case TERMINATED -> "No longer employed";
};
```

The lesson keeps console input/output at the application boundary. Methods
such as `formatInput` receive text and return a result, which makes the
behavior easy to test and keeps business logic independent of `System.in`.

## 2. Classes and object-oriented design

A class groups state and behavior. A constructor establishes the initial
state, while `private` fields and public methods protect the class invariant.

```java
public final class Employee {
    private final int id;
    private String name;

    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
```

The employee lesson uses an abstract `Employee` base class, full-time and
part-time subclasses, and a `NotificationSender` interface. The payroll
service receives that interface through its constructor. This is composition:
the service depends on a capability rather than constructing a concrete
notification implementation internally.

Prefer composition when a class needs a collaborator. Inheritance is useful
when the subtype really is a specialized form of the base type and can honor
the base type's contract.

## 3. Core APIs

`String` is immutable. Operations such as `toUpperCase` return a new string.
Use `StringBuilder` when repeatedly constructing text in a loop. Wrapper
classes such as `Integer` let primitive values participate in generic
collections, and methods such as `Integer.parseInt` convert text to numbers.

The `java.time` API provides immutable date/time types such as `LocalDate`.
Regular expressions are useful for small format checks, but they are not a
replacement for complete domain validation.

## 4. Collections

- `ArrayList` is a resizable, indexed list.
- `LinkedList` can act as a list or a queue, although `ArrayDeque` is usually
  the better general-purpose queue/deque implementation.
- `HashSet` stores unique values without promising iteration order.
- `TreeSet` stores unique values in sorted order.
- `HashMap` looks up values by key; `TreeMap` also keeps keys sorted.
- `Queue` models first-in, first-out work; `Deque` can model both a queue and
  a stack.

Generics move type mistakes to compile time. `List<? extends Number>` means a
method can read numbers from a list of any `Number` subtype. `List<? super
Integer>` means the method can safely add integers to a list of `Integer` or a
parent type. This is the practical reason behind the PECS rule: producer
extends, consumer super.

`Comparable` defines a type's natural ordering. `Comparator` supplies an
ordering for a particular use case without changing the type itself.

## 5. Exceptions

Checked exceptions are part of a method's declared contract and must be
handled or declared. Unchecked exceptions, such as `IllegalArgumentException`
and `NumberFormatException`, usually represent invalid input or programming
errors that callers may not be able to recover from locally.

Use a custom exception when a domain failure deserves a meaningful name, such
as `EmployeeNotFoundException`. Catch exceptions at the boundary where you can
actually recover, translate, or report them. Do not catch `Exception` merely
to hide a failure.

## 6. Lambdas, streams, and `Optional`

Functional interfaces describe one operation. `Predicate<T>` tests a value,
`Function<T, R>` transforms it, and `Consumer<T>` performs an action. Lambdas
provide implementations inline:

```java
Predicate<Employee> active = Employee::isActive;
```

A stream pipeline normally has a source, intermediate operations such as
`filter` and `map`, and a terminal operation such as `toList` or `reduce`.
Streams do not make a slow algorithm automatically fast; use them for clear
transformations and measure before optimizing.

`Optional<T>` represents a value that may be absent. It is useful for a return
value such as a search result, but it should not be used as every field in a
domain object or as a replacement for all null checks.

## 7. Threads and asynchronous work

An executor owns a pool of worker threads and accepts tasks. A synchronized
method protects shared mutable state by allowing one thread at a time into the
critical section. `CompletableFuture` represents a result that may be
available later and allows asynchronous stages to be composed.

The sample counter is deliberately small and deterministic. Real applications
must also consider cancellation, timeouts, back-pressure, executor sizing,
and graceful shutdown.

## 8. Maven project structure

The root `pom.xml` describes the project, compiler level, dependencies, and
plugins. Maven uses the conventional layout:

```text
src/main/java/  application code
src/test/java/  test code
target/         generated build output
```

Useful commands:

```bash
mvn test       # compile and run tests
mvn package    # test, then create the runnable jar
mvn clean      # remove generated target/ output
```

Dependencies such as JUnit are downloaded by Maven and are not copied into
the repository. The application itself has no runtime framework dependency in
this phase.

## Run the phase

From the repository root:

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
mvn test
```

The next phase will use these Java foundations to explain Spring's inversion
of control container and dependency injection.
