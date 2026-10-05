# Phase 5 — SQL and Database Foundation

Phase 5 adds persistence concepts without introducing Hibernate, JPA, or
Spring Data. The examples use the standard Java `java.sql` API and an embedded
H2 database so the lesson can run in a clean environment without requiring a
separate database server.

The important boundary is:

```text
Java application → JDBC → SQL database
```

The later ORM boundary will be:

```text
Java entity → Hibernate/JPA → JDBC → SQL database
```

## 1. Relational thinking

A relational database stores data in tables. A table has rows and columns, and
each row represents one record of one relation.

The lesson uses four normalized tables:

```text
department  1 ──── many  employee
employee    many ── many  project
                 through employee_project
```

`employee_project` is a junction table. It stores relationship-specific data
such as `assignment_role`, `hours_per_week`, and `assigned_on`. This is better
than putting a comma-separated project list inside `employee`, because each
value remains atomic and independently queryable.

## 2. Keys and constraints

`schema.sql` demonstrates the main integrity tools:

- a primary key uniquely identifies a row;
- a foreign key requires a referenced parent row to exist;
- `UNIQUE` prevents duplicate department names, project names, and emails;
- `NOT NULL` requires a value;
- `CHECK` rejects invalid salary, budget, and weekly-hour values;
- the composite primary key on `employee_project` prevents assigning the same
  employee to the same project twice.

Constraints belong in the database even when application validation also
exists. Application checks improve user feedback; database constraints protect
the data when another program, script, or concurrent request writes to it.

## 3. Joins

The view `v_employee_project` joins four tables:

```sql
SELECT e.full_name, d.name, p.name, ep.assignment_role
FROM employee_project ep
JOIN employee e ON e.id = ep.employee_id
JOIN department d ON d.id = e.department_id
JOIN project p ON p.id = ep.project_id;
```

An inner `JOIN` returns rows where both sides match. A `LEFT JOIN` keeps the
left-side row even when no child row exists. The payroll view uses a left join
so a department with no employees can still be represented, although the
lesson query filters to departments with employees.

## 4. Aggregates and subqueries

`v_department_payroll` demonstrates `COUNT`, `SUM`, `AVG`, `GROUP BY`, and
`COALESCE`. Aggregates reduce many rows into a summary row per department.

The employee query uses a scalar subquery:

```sql
WHERE salary > (SELECT AVG(salary) FROM employee)
```

The inner query calculates one value first. The outer query compares each
employee with it. More advanced subqueries can return a set and work with
`IN`, `EXISTS`, or correlated conditions.

## 5. Indexes

An index is an additional lookup structure. It can make a filter or join much
faster, but it also consumes storage and makes writes slightly more expensive.
The lesson adds indexes for the foreign-key columns most likely to be used in
joins:

```sql
CREATE INDEX idx_employee_department ON employee (department_id);
CREATE INDEX idx_employee_project_project ON employee_project (project_id);
```

Primary keys and unique constraints normally create supporting indexes as well.
Indexes should follow measured query patterns, not be added indiscriminately.

## 6. Transactions

A transaction groups statements into one unit of work. The JDBC lesson turns
off auto-commit, inserts a project assignment, and calls `rollback()`.
Because the transaction is rolled back, the assignment count is unchanged.

The usual transaction shape is:

```java
connection.setAutoCommit(false);
try {
    // related statements
    connection.commit();
} catch (SQLException exception) {
    connection.rollback();
    throw exception;
}
```

Transactions support the ACID goals: atomicity, consistency, isolation, and
durability. Spring's `@Transactional` will make this easier to express later,
but understanding the JDBC boundary first is valuable.

## 7. Views and normalization

A view is a named query. It can provide a stable read model without copying
the underlying rows. The two lesson views keep reporting SQL separate from the
Java code that reads it.

Normalization reduces duplication and update anomalies. Here, department data
is stored once in `department`; employees reference it with `department_id`.
If a department name changes, one row changes instead of many employee rows.

This lesson stays near the practical first three normal forms:

- each column contains one kind of atomic value;
- non-key columns depend on the whole key;
- non-key columns do not repeat facts that belong in another table.

## 8. JDBC and prepared statements

The lesson uses `PreparedStatement` for values instead of concatenating user
input into SQL. That improves type handling and avoids SQL injection in the
usual value-parameter case. It also uses try-with-resources so connections,
statements, and result sets are closed.

The code intentionally shows the lower-level JDBC steps:

1. obtain a `Connection`;
2. prepare SQL;
3. bind parameters;
4. execute the statement;
5. read the `ResultSet`;
6. close resources.

Later Spring JDBC can remove repetitive connection management while keeping
SQL visible. Hibernate/JPA will add object mapping after this phase.

## 9. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The runnable output includes the table list, joined rows, aggregate view,
above-average salary query, index metadata, rollback result, and a rejected
duplicate-email insert. The database is in memory, so it intentionally resets
when the process ends.

The SQL files are:

- [`schema.sql`](../src/main/resources/db/phase5/schema.sql)
- [`data.sql`](../src/main/resources/db/phase5/data.sql)

The JDBC lesson is
[`SqlFoundationLesson.java`](../src/main/java/com/jarirahmed/sqlfoundation/SqlFoundationLesson.java).

## What this phase does not teach yet

- no `@Entity` or `@Table` mapping;
- no Hibernate `Session` or persistence context;
- no JPA `EntityManager`;
- no Spring Data repository;
- no production migration tool such as Flyway or Liquibase;
- no external PostgreSQL/MySQL server configuration.

Those omissions are intentional. Phase 6 will use these relational concepts
to explain how Hibernate maps Java objects to rows, manages a persistence
context, performs dirty checking, and handles entity relationships.
