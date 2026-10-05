# Phase 7 — Jakarta Persistence (JPA)

Phase 7 separates the persistence specification from the implementation:

```text
Jakarta Persistence API  = standard contracts and annotations
Hibernate ORM             = provider implementing those contracts
```

The application now uses `EntityManagerFactory`, `EntityManager`, JPQL,
Criteria, entity graphs, and standard lock types. Hibernate still runs under
the persistence unit, but Phase 7 code does not call Hibernate's native
`Session` API.

The [Jakarta Persistence specification](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
defines `EntityManager` as the API for interacting with a persistence context,
and defines JPQL, Criteria, entity graphs, and locking as standard persistence
features.

## 1. JPA versus Hibernate

JPA is an API/specification. It defines contracts such as:

- `EntityManagerFactory`;
- `EntityManager`;
- `EntityTransaction`;
- `TypedQuery`;
- JPQL and Criteria APIs;
- `@Entity`, `@Id`, `@Version`, and relationship annotations;
- `LockModeType` and entity-graph hints.

Hibernate is the provider selected in `persistence.xml`:

```xml
<provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
```

Changing providers is not always zero-effort, but standard application code
can depend on the JPA contracts rather than on Hibernate's native `Session`
types. Phase 6 showed the provider-specific API; Phase 7 shows the portable
API layer.

## 2. Persistence unit and EntityManagerFactory

`src/main/resources/META-INF/persistence.xml` defines the `phase7-unit`
persistence unit. `JpaLesson.buildEntityManagerFactory()` overrides only the
database name so each test receives an isolated H2 database.

The lifecycle is:

```text
Persistence.createEntityManagerFactory()
                 ↓
        EntityManagerFactory
                 ↓ createEntityManager()
           EntityManager
                 ↓
          persistence context
```

Create one `EntityManagerFactory` for the application lifetime. Create and
close an `EntityManager` around a unit of work. An application-managed
`EntityManager` must be closed explicitly; the standard API documentation
describes these responsibilities in the [EntityManager
reference](https://jakarta.ee/specifications/persistence/3.2/apidocs/jakarta.persistence/jakarta/persistence/entitymanager).

## 3. EntityManager and persistence context

The `EntityManager` provides standard operations:

```java
entityManager.persist(newEntity);
entityManager.find(HibernateEmployee.class, id);
entityManager.merge(detachedEntity);
entityManager.remove(entity);
entityManager.clear();
```

The persistence context is the set of managed entities associated with one
`EntityManager`. Within one context, finding employee `1` twice returns the
same Java object identity. `clear()` detaches the managed objects. `merge()`
copies the state of a detached object into a managed instance and returns that
managed copy; it does not make the original object managed again.

The entity classes are reused from Phase 6. This is deliberate: the mapping
metadata can remain the same while the API changes from Hibernate `Session` to
JPA `EntityManager`.

## 4. JPQL

JPQL queries entity names and persistent attributes, not physical table and
column names:

```java
select e
from HibernateEmployee e
where e.salary >= :minimum
order by e.salary desc
```

`TypedQuery<HibernateEmployee>` provides a typed result. The provider translates
JPQL into database-specific SQL. JPQL resembles Hibernate HQL, but JPQL is the
standard query language defined by Jakarta Persistence.

## 5. Criteria API

Criteria builds a typed query object graph in Java:

```java
CriteriaBuilder builder = entityManager.getCriteriaBuilder();
CriteriaQuery<HibernateEmployee> query =
        builder.createQuery(HibernateEmployee.class);
Root<HibernateEmployee> employee = query.from(HibernateEmployee.class);
query.select(employee).where(
        builder.equal(employee.get("department").get("name"), "Engineering"));
```

Criteria is useful when filters are assembled dynamically. Its type model can
be made safer with the generated static metamodel, but string-based attribute
names in this first example keep the query structure visible.

## 6. Entity lifecycle

The JPA lifecycle is similar to the Phase 6 lifecycle, but expressed through
the standard API:

```text
new object --persist--> managed --commit--> row
                           |
                        clear()
                           ↓
                       detached
                           |
                        merge()
                           ↓
                  managed copy returned
```

`EntityManager.contains()` lets the lesson observe these state changes. Dirty
checking still happens at flush/commit because Hibernate is the provider, but
the application only depends on `EntityManager`.

## 7. Transactions

This standalone lesson uses a resource-local transaction:

```java
EntityTransaction transaction = entityManager.getTransaction();
transaction.begin();
try {
    // persist, query, or modify managed entities
    transaction.commit();
} catch (RuntimeException exception) {
    transaction.rollback();
    throw exception;
}
```

Writes and explicit lock requests require a transaction. In a Spring
application, Spring will normally manage this boundary with
`@Transactional`; that integration is a later phase.

## 8. Optimistic locking

`HibernateEmployee.version` is mapped with `@Version`. The provider reads the
version and includes it in the update condition. Two entity managers can read
the same version, but after one commits an update, a stale second update is
rejected with an `OptimisticLockException` (often wrapped by a transaction
rollback exception).

Conceptually, the update is similar to:

```sql
UPDATE employee
SET salary = ?, entity_version = ?
WHERE id = ? AND entity_version = ?
```

The [Jakarta Persistence locking section](https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2)
defines version fields and optimistic lock behavior. Optimistic locking helps
prevent lost updates without holding a long database lock for the whole user
interaction.

## 9. Fetch strategies and entity graphs

Phase 6 used Hibernate `join fetch`. Phase 7 adds the standard JPA entity-graph
mechanism:

```java
EntityGraph<HibernateEmployee> graph =
        entityManager.createEntityGraph(HibernateEmployee.class);
graph.addAttributeNodes("department");

query.setHint("jakarta.persistence.fetchgraph", graph);
```

The graph describes which attributes should be fetched for that operation. It
is a fetch plan, not a replacement for careful transaction boundaries. Lazy
collections still require an open persistence context when they are accessed.

## 10. Pagination

JPA uses the same offset/limit idea as JDBC and Hibernate:

```java
query.setFirstResult(offset);
query.setMaxResults(limit);
```

Pagination is safest when the root query returns one row per root entity. A
collection join can multiply SQL rows, so collection fetches and pagination
must be designed and tested together.

## 11. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The output demonstrates JPQL, Criteria, the EntityManager first-level cache,
persist/clear/merge lifecycle, optimistic locking, entity graphs, and JPA
pagination.

The main implementation is
[`JpaLesson.java`](../src/main/java/com/jarirahmed/jpa/JpaLesson.java).
The standard persistence-unit descriptor is
[`persistence.xml`](../src/main/resources/META-INF/persistence.xml).

## What Phase 7 does not teach yet

- no Spring `@Transactional` integration;
- no Spring Data `JpaRepository` or `CrudRepository`;
- no derived query methods or `@Query` repository methods;
- no Specifications or repository projections;
- no production migration workflow.

Phase 8 will connect transaction concepts to Spring. Spring Data JPA can then
be introduced as a repository abstraction over JPA rather than as a substitute
for understanding the persistence context.
