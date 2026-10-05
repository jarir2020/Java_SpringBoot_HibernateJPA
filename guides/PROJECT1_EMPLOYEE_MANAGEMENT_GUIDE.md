# Project 1 — Employee Management System

Project 1 turns the Phase 1 Java lessons into a small command-line program.
It is intentionally educational rather than production-grade. The goal is to
practice the language boundaries that later Spring services and repositories
will organize.

## 1. What the project practices

```text
console input
    ↓
EmployeeManagementCli
    ↓
EmployeeManagementService
    ↓
CrudRepository<EmployeeRecord, Integer>
    ↓
LinkedHashMap and optional text-file persistence
```

The project covers:

- OOP through `EmployeeRecord`, encapsulation, state-changing methods, and an
  `EmployeeStatus` enum;
- collections through a `LinkedHashMap` repository and sorted result lists;
- custom exceptions for duplicate ids, missing employees, and malformed files;
- Java NIO `Path`, `Files`, UTF-8 text, and explicit save/load behavior;
- generics through `CrudRepository<T, ID>`;
- streams through search filtering, sorting, and payroll grouping by department;
- testable console I/O through injected `Reader` and `PrintWriter` objects.

## 2. Run the CLI

Build the repository first:

```bash
mvn package
```

Start Project 1 instead of the full phase demonstration:

```bash
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --project1
```

Use another save file explicitly when desired:

```bash
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar \
  --project1 /tmp/my-employees.txt
```

The default file is `employees.txt` in the current directory. It is ignored by
Git because it is local learning data.

## 3. Commands

```text
add id|name|department|monthlySalary
list
find id
search text
activate id
deactivate id
payroll
save [path]
load [path]
exit
```

Example session:

```text
add 1|Alice Rahman|Engineering|5000
add 2|Bob Karim|Operations|3000
list
search engineering
deactivate 2
payroll
save
exit
```

The pipe-separated add syntax keeps parsing visible. Names and departments may
not contain `|`, newline, or carriage return in this deliberately simple file
format.

## 4. Important design decisions

### The CLI is not the service

`EmployeeManagementCli` translates text into method calls and formats results.
The service owns duplicate checks, lookup failures, lifecycle changes, and
payroll rules. This separation makes the service testable without `System.in`
and `System.out`.

### The repository is an abstraction

`CrudRepository<T, ID>` demonstrates that application code can depend on an
operation contract rather than a concrete `Map`. The current implementation
is in-memory. A later Spring project can replace it with a database repository
without putting SQL into the CLI.

### Files are persistence, not a database

The file repository writes a header followed by one record per line:

```text
id|name|department|monthlySalary|status
1|Alice Rahman|Engineering|5000|ACTIVE
```

This is sufficient to learn `Path`, `Files`, encoding, parsing, and malformed
input handling. It does not provide concurrent writes, transactions, indexes,
schema migrations, encryption, or reliable multi-process access.

### Streams express reports

`payrollByDepartment()` filters out inactive employees and reduces salaries by
department. The `TreeMap` result gives deterministic department ordering,
which makes both console output and tests easier to understand.

## 5. Test the project

Run only Project 1 tests:

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
  -Dtest=EmployeeManagementServiceTest,EmployeeFileRepositoryTest,EmployeeManagementCliTest test
```

Then run the complete course suite:

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 test
```

The tests cover service rules, stream reporting, file round trips, malformed
files, and scripted console commands. They do not claim to prove production
durability or user authentication.

## 6. Suggested exercises

After understanding the existing code, extend it one small step at a time:

1. Add a `remove id` command and test it.
2. Add a `department` command that lists only one department.
3. Add a `rename id|newName` command.
4. Add a `count` command for active and inactive employees.
5. Add a second file format test with an invalid salary or status.
6. Add a `help <command>` explanation without changing service rules.

Do not add Spring annotations yet. Project 2 will rebuild the idea as a small
Spring Core inventory application so the difference between plain Java
composition and a dependency-injection container is visible.
