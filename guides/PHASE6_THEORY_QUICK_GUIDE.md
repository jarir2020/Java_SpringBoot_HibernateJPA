# Phase 6 — Hibernate ORM

Phase 6 adds Hibernate as the ORM layer over the relational concepts from
Phase 5. It uses Hibernate's native `SessionFactory` and `Session` APIs. It
does not add Spring Data JPA or use `EntityManager` yet; those belong to the
next phase.

The runtime boundary is:

```text
Java entity → Hibernate Session → JDBC → H2 database
```

Hibernate's official quickstart describes the same bootstrap shape: register
annotated classes with `MetadataSources`, build one `SessionFactory`, and open
short-lived `Session` instances for units of work. See the [official Hibernate
quickstart](https://docs.hibernate.org/stable/orm/quickstart/html_single/).

## 1. ORM concepts

Object-relational mapping connects two models:

```text
Java object  ↔ database row
Java class   ↔ database table
Java field   ↔ database column
object link  ↔ foreign key
```

Phase 5 required explicit SQL and `ResultSet` mapping. Hibernate keeps the SQL
work underneath, but maintains an object graph and a persistence context for a
unit of work.

The application has one `SessionFactory` for its database configuration and
opens a `Session` for each transaction. A `Session` is not a global singleton
and should not be shared across unrelated threads.

## 2. Bootstrapping native Hibernate

`HibernateLesson.buildSessionFactory()` makes the startup stages explicit:

```java
StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
        // JDBC URL, credentials, schema-generation mode, and inspector
        .build();

SessionFactory factory = new MetadataSources(registry)
        .addAnnotatedClass(HibernateEmployee.class)
        .buildMetadata()
        .buildSessionFactory();
```

The lesson uses H2 in-memory storage and `hibernate.hbm2ddl.auto=create-drop`.
That is suitable for a disposable tutorial database, not a production schema
strategy. Production applications should use reviewed migrations and a real
connection pool.

## 3. Entity mapping

The entity classes use Jakarta Persistence annotations as mapping metadata,
while the program operates Hibernate's native API:

- `@Entity` marks a persistent class;
- `@Table` chooses the table name;
- `@Id` marks the identifier;
- `@GeneratedValue` delegates identifier generation to the database/Hibernate;
- `@Column` controls column names and constraints;
- `@Transient` marks a Java-only field.

The no-argument constructors are protected because Hibernate needs a way to
materialize an object before populating its fields. Application code uses the
meaningful constructors instead.

`HibernateEmployee.getDisplayLabel()` demonstrates a derived value that is not
stored in the database because its backing field is `@Transient`.

## 4. Relationships

The model contains:

```text
department  1 ──── many  employee
employee    1 ──── many  employee_project  many ──── 1 project
```

`@ManyToOne` is the owning side that holds the foreign-key column. The
`@OneToMany(mappedBy = "department")` collection is the inverse side;
`mappedBy` says that the other field owns the relationship.

The employee/project relationship is technically many-to-many, but it is
represented by `HibernateEmployeeProject` instead of a bare `@ManyToMany`.
That is the correct shape when the relationship has its own attributes:
`role`, `hoursPerWeek`, and `assignedOn`. `@EmbeddedId` and `@MapsId` map the
composite key from the two foreign keys.

The employee and department collections demonstrate `cascade` and
`orphanRemoval`. These are write-propagation rules, not fetch rules. They must
be chosen according to ownership and lifecycle—not added automatically to
every association.

## 5. Entity lifecycle and persistence context

An entity commonly moves through these states:

```text
new object --persist--> managed --commit--> database row
                         |
                    dirty checking
                         |
                    modified UPDATE
```

Important states:

- transient: a new Java object that Hibernate does not manage;
- managed: attached to the current `Session` persistence context;
- detached: once managed, but its session is closed or the entity is evicted;
- removed: marked for deletion in the current unit of work.

The phase demonstrates dirty checking by changing Alice's salary without an
explicit `UPDATE` call. Hibernate compares the managed state at flush/commit
and sends the update. The rollback example changes the same managed object,
rolls back the transaction, and confirms the value remains unchanged in a new
session.

The first-level cache belongs to one `Session`. Loading employee `1` twice in
the same session returns the same Java object identity. This is not a shared
application-wide cache; a new session has a new persistence context.

## 6. Transactions

The lesson keeps the transaction boundary visible:

```java
try (Session session = factory.openSession()) {
    Transaction transaction = session.beginTransaction();
    try {
        // load and change managed entities
        transaction.commit();
    } catch (RuntimeException exception) {
        transaction.rollback();
        throw exception;
    }
}
```

A transaction should cover one coherent unit of work. The later Spring
transaction phase will provide declarative `@Transactional` boundaries, but
the underlying commit/rollback behavior is the same concept.

## 7. HQL

Hibernate Query Language queries use entity and field names rather than table
and column names:

```java
from HibernateEmployee e order by e.id
```

The query returns `HibernateEmployee` objects. Hibernate translates the HQL
into database-specific SQL. HQL is closely related to JPQL, but the Phase 6
code deliberately uses Hibernate's native query API. The next phase will
separate the JPA specification from the Hibernate implementation.

## 8. Lazy loading and the N+1 problem

Associations are mapped `LAZY` in this lesson. A department's employee list is
not loaded until code accesses it while the owning session is open. Accessing
that collection after the session closes raises `LazyInitializationException`;
the fix is to define the needed fetch plan inside the service transaction,
not to keep sessions open indefinitely.

The lesson measures two approaches using `StatementInspector`:

```text
Query employees → access each lazy department → 1 + additional SELECTs
JOIN FETCH      → employees and departments in one SELECT
```

This is the N+1 query problem. The official [Hibernate HQL query
guide](https://docs.hibernate.org/orm/7.3/querylanguage/html_single/) explains
that `join fetch` overrides laziness for the requested association and is a
common way to avoid those extra selects.

The solution is not “make every relationship eager.” Eager mappings can load
too much data and still produce additional queries. Choose a fetch plan for
the use case: HQL join fetch, an entity graph later in the JPA phase, batch
fetching, or a projection.

## 9. Pagination and fetch planning

The example paginates the root employee query with `setFirstResult()` and
`setMaxResults()`. Pagination is straightforward for one root table, but
joining a collection can multiply result rows. A collection fetch join and a
page limit therefore requires care: the database rows are not necessarily the
same thing as the number of root entities.

Practical options include:

1. page root IDs/entities first;
2. fetch related data in a second query;
3. use a carefully designed projection;
4. avoid applying a page directly to a collection fetch join unless its
   semantics are understood and tested.

## 10. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The output includes entity loading, first-level cache identity, lazy loading,
dirty checking, rollback, N+1 and join-fetch statement counts, pagination, and
the closed-session lazy-loading result.

The main implementation is
[`HibernateLesson.java`](../src/main/java/com/jarirahmed/hibernate/HibernateLesson.java).
The mapped classes are in
[`src/main/java/com/jarirahmed/hibernate`](../src/main/java/com/jarirahmed/hibernate).

## What Phase 6 intentionally does not teach yet

- no `EntityManager` or `persistence.xml`;
- no Spring `@Transactional` integration;
- no Spring Data `JpaRepository`;
- no repository query methods or specifications;
- no production migration workflow;
- no second-level cache configuration.

Phase 7 will explain that JPA is a specification/API, Hibernate is an
implementation, and Spring Data JPA builds repository abstractions on top.
