# Repository instructions

## Purpose

This repository is a theory-first Java, Spring Boot, Hibernate, and JPA course.
Keep the examples incremental, runnable, beginner-readable, and aligned with
the next phase in [`plan.md`](plan.md).

## Working rules

- Read `plan.md`, the relevant phase guide, and the affected source/tests before editing.
- Advance one roadmap phase at a time; avoid unrelated redesigns.
- Prefer constructor injection, explicit configuration, ordinary Java classes,
  and comments that explain behavior rather than restating syntax.
- Keep entities, DTOs, controllers, services, repositories, and security
  configuration in their demonstrated layers.
- Do not commit passwords, API keys, database credentials, generated build
  output, IDE state, or agent-workspace metadata.
- Preserve existing tutorial behavior when adding a later phase. Add explicit
  isolation configuration when a new dependency would otherwise change an
  earlier lesson.

## Validation

Run the narrowest relevant test first, then the complete suite:

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 -Dtest=... test
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 test
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 -DskipTests package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The embedded H2 databases are disposable teaching databases. A successful
compile or test is local evidence only; distinguish it from packaged-JAR,
Git, provider, and live deployment evidence.

## Documentation

For each completed phase, update the README and add or update
`guides/PHASE*_THEORY_QUICK_GUIDE.md`. Keep future topics explicitly marked as
out of scope instead of adding hidden abstractions early.

## Git handoff

Before a first commit, inspect the intended scope, run
`git diff --cached --check`, use the requested commit message and branch, then
verify the pushed remote ref and working-tree status separately.
