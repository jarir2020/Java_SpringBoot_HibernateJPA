# Phase 8 — Spring Transactions

Phase 8 connects the JPA persistence unit to Spring's transaction
infrastructure. The code still uses the standard JPA `EntityManager` inside
the service, but Spring now starts, commits, and rolls back transactions around
public service methods.

The boundary becomes:

```text
caller → Spring proxy → @Transactional service → EntityManager/JPA → Hibernate → database
```

Spring's official explanation describes this as annotation metadata applied by
an AOP proxy and a `TransactionInterceptor` using a
`PlatformTransactionManager`. See [Understanding Spring's declarative
transaction implementation](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-decl-explained.html).

## 1. Configuration pieces

`SpringTransactionConfiguration` explicitly registers:

- `EntityManagerFactory`, reused from the JPA persistence unit;
- `JpaTransactionManager`, which coordinates JPA transactions;
- `@EnableTransactionManagement`, which enables annotation-driven advice;
- `PersistenceAnnotationBeanPostProcessor`, which injects the shared
  `@PersistenceContext` EntityManager proxy.

The lesson uses an ordinary `AnnotationConfigApplicationContext`, not Spring
Boot auto-configuration. This keeps the transaction manager and proxy setup
visible before a later Boot/Data JPA phase simplifies it.

Because this project uses Hibernate as the JPA provider, the configuration
selects `HibernateJpaDialect`. The JPA standard does not define how a provider
must apply a custom JDBC isolation level; the provider-specific dialect makes
the `Isolation.READ_COMMITTED` demonstration valid here. The JPA API remains
the service-facing API.

## 2. The transactional proxy

The service bean is not called directly. Spring creates a proxy around it:

```text
SpringTransactionService proxy
        ↓
start or join transaction
        ↓
target service method
        ↓
commit or rollback
```

The test checks `AopUtils.isAopProxy(service)`, and the service checks that its
EntityManager is joined to the current transaction. The official [Spring
`@Transactional` reference](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html)
also documents the important proxy rule: an external call through the proxy
is intercepted, while a same-class self-invocation does not pass through the
proxy in the default proxy mode.

That is why the `REQUIRES_NEW` example delegates to a separate
`NewTransactionService` bean rather than calling another annotated method on
the same object.

## 3. Default transaction behavior

The default `@Transactional` settings are conceptually:

```text
propagation = REQUIRED
isolation   = DEFAULT
readOnly    = false
```

`REQUIRED` joins an existing transaction or starts one when none exists. The
`createEvent()` method therefore commits its insert when it returns normally.

The Spring reference documents the default rollback rule: an uncaught
`RuntimeException` or `Error` marks the transaction for rollback, while a
checked exception does not automatically do so. The lesson makes the checked
case rollback explicitly:

```java
@Transactional(rollbackFor = CheckedTransactionFailure.class)
public void createEventThenThrowChecked(...) throws CheckedTransactionFailure {
    // write data
    throw new CheckedTransactionFailure(...);
}
```

## 4. Commit and rollback

`createEventThenThrowRuntime()` persists an event and throws an unchecked
exception. Spring marks the transaction rollback-only, so the event disappears.

`createEventThenThrowChecked()` persists an event and throws a checked
exception, but its `rollbackFor` rule gives that exception rollback semantics.
The test queries the database after each method, making the result observable
outside the completed transaction.

The recommended rollback signal is to let the exception escape the
transactional method. Catching an exception inside the method and returning
normally can result in a commit unless the code explicitly marks the
transaction rollback-only.

## 5. Propagation

Propagation describes how a method participates in an existing transaction.
The lesson demonstrates:

```text
outer REQUIRED transaction
  ├─ outer-before
  ├─ inner REQUIRES_NEW transaction → fails and rolls back
  └─ outer-after
outer commits
```

`REQUIRES_NEW` suspends the outer transaction and starts an independent one.
The inner event is rolled back, while both outer events commit. This is useful
for genuinely independent work such as an audit record, but it requires another
database connection and should not be used casually.

The [Spring transaction propagation reference](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html)
explains the difference between logical propagation and the underlying
physical transactions.

## 6. Isolation

Isolation controls how a transaction observes concurrent work. The service
declares:

```java
@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
```

This tutorial uses Hibernate's dialect and connection-handling mode so Spring
can apply that setting to the JDBC connection. Exact isolation behavior still
depends on the database; H2 is only the repeatable local teaching database.
The default `Isolation.DEFAULT` is usually the best starting point unless a
use case has a measured concurrency requirement.

## 7. Read-only transactions

`readOnly = true` communicates that a method is intended for reads and allows
the transaction manager/provider to optimize or validate that workload. It is
not a security permission that makes writes impossible. The lesson uses it for
event reads and lazy relationship navigation.

## 8. Lazy loading and transaction boundaries

`engineeringEmployeeCount()` loads a department and accesses its lazy employee
collection before the transactional proxy closes the EntityManager. This is
the correct service-layer boundary for that read.

Returning a lazy entity graph and accessing it later, after the transaction
has closed, can produce `LazyInitializationException`. The solution is to
fetch/map the required data inside the transaction or define an appropriate
fetch plan—not to keep a transaction open across a web request.

## 9. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The output shows the proxy, active transaction, committed work, runtime and
checked-exception rollback, `REQUIRES_NEW` behavior, and lazy loading inside a
transaction.

The main lesson is
[`SpringTransactionLesson.java`](../src/main/java/com/jarirahmed/springtransactions/SpringTransactionLesson.java).
The configuration is
[`SpringTransactionConfiguration.java`](../src/main/java/com/jarirahmed/springtransactions/SpringTransactionConfiguration.java).

## What Phase 8 does not teach yet

- no Spring Boot transaction auto-configuration;
- no Spring Data `JpaRepository`;
- no web request transaction policy;
- no distributed/JTA transaction;
- no production database connection pool configuration;
- no retry policy for transient transaction failures.

Phase 9 will introduce DTOs and API architecture, then Spring Data JPA can be
added as a repository abstraction over this transaction-aware service layer.
