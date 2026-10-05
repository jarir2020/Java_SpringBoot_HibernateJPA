# Phase 12 — Production / Advanced Spring

Phase 12 is the final roadmap phase. It connects the earlier Java, Spring,
MVC, Boot, persistence, security, and testing lessons to production concerns.
The repository contains a local lab that runs without Redis, RabbitMQ, Kafka,
or cloud credentials. The guide also shows where those infrastructure choices
belong.

## 1. Production boundaries

Production work adds operational boundaries around the application:

```text
HTTP client
    ↓
reverse proxy / TLS / rate limits
    ↓
Spring Boot application
    ├── cache
    ├── async workers
    ├── scheduled jobs
    ├── database transaction
    └── message boundary
    ↓
metrics, logs, traces, health checks, deployment automation
```

The main lesson is that each boundary needs an explicit owner, timeout,
failure policy, and observable result. A production annotation is not a
substitute for those decisions.

## 2. Spring Cache and Redis

[`AdvancedEmployeeCache`](../src/main/java/com/jarirahmed/production/AdvancedEmployeeCache.java)
uses `@Cacheable` and `@CacheEvict`. The explicit configuration enables caching
with a local `ConcurrentMapCacheManager`:

```java
@Cacheable(cacheNames = "advancedEmployees", key = "#employeeId")
public AdvancedEmployee findById(long employeeId) { ... }
```

The first call loads the backing store. A second call with the same key returns
the cached value. Eviction removes that key so the next call loads again. The
Phase 12 test makes this behavior measurable.

For multiple application instances, an in-memory cache is not shared. Replace
the cache manager with Redis when the consistency, latency, serialization, and
failure tradeoffs are understood:

- choose a key namespace and value format;
- define a TTL rather than caching forever;
- invalidate on writes or accept a documented staleness window;
- avoid caching sensitive data by default;
- decide what happens when Redis is unavailable;
- monitor hit rate, evictions, size, and latency.

`compose.yaml` includes Redis behind the optional `infra` profile. It is a
local infrastructure placeholder, not a production Redis security setup.

## 3. Async processing and messaging

[`AsyncNotificationService`](../src/main/java/com/jarirahmed/production/AsyncNotificationService.java)
uses a named `ThreadPoolTaskExecutor` and `@Async`. Naming the executor makes
thread-pool ownership visible. The configuration also declares a
`ThreadPoolTaskScheduler` instead of relying on Spring's default scheduler. A
production executor needs bounded queues, capacity measurements, graceful
shutdown, and a policy for rejected work.

Do not use `@Async` as a durable message queue. If work must survive a process
restart, use a broker and an explicit delivery contract:

- RabbitMQ is a natural fit for routed commands and work queues;
- Kafka is a natural fit for append-only event streams and replay;
- consumers should be idempotent;
- acknowledgements, retries, dead-letter handling, ordering, and schema
  compatibility must be designed;
- distributed work needs a correlation ID and useful metrics.

The local lesson deliberately stops at the executor boundary so the normal
course build does not require a broker. A later project can replace the method
call with a RabbitMQ or Kafka adapter while keeping the application command
and event contracts stable.

## 4. Scheduling and background jobs

[`BackgroundSyncJob`](../src/main/java/com/jarirahmed/production/BackgroundSyncJob.java)
uses:

```java
@Scheduled(fixedDelayString = "${production.schedule.delay-ms:1000}")
```

`fixedDelay` waits after one invocation finishes. Other useful policies are
`fixedRate` and cron expressions. Production jobs should also be:

- idempotent, so retries do not duplicate effects;
- bounded by a timeout;
- protected from overlapping runs when multiple instances exist;
- observable with duration, success, failure, and last-run metrics;
- safe during shutdown and deployment.

For more than one application instance, use a distributed lock or move the
job to a dedicated worker. A scheduler inside every replica can otherwise run
the same job several times.

## 5. Microservices: delay the split

The course first builds a monolithic application because a clear module and a
clear transaction boundary are prerequisites for a safe service boundary.
When splitting a service, define:

- ownership of data and schema;
- a versioned HTTP or event contract;
- timeouts, retries, backoff, and a circuit-breaker policy;
- authentication and authorization between services;
- correlation IDs and distributed traces;
- deployment, rollback, and compatibility strategy.

OpenFeign can provide a declarative HTTP client, an API Gateway can centralize
edge concerns, and service discovery/configuration systems can help at larger
scale. They do not remove network failure. A client must still distinguish a
valid empty response from a timeout, partial failure, or incompatible schema.

## 6. Deployment assets

The repository includes:

- [`Dockerfile`](../Dockerfile), a multi-stage Java 21 image build;
- [`compose.yaml`](../compose.yaml), a local app plus optional Redis service;
- [`deploy/nginx.conf`](../deploy/nginx.conf), a reverse-proxy starting point;
- [`.github/workflows/ci.yml`](../.github/workflows/ci.yml), test and package CI.

Build and run the container locally:

```bash
docker build -t java-spring-learning .
docker run --rm -p 8080:8080 java-spring-learning
```

Or run the application with Compose:

```bash
docker compose up --build
docker compose --profile infra up --build
```

The second command also starts the optional unauthenticated local Redis
container. Never expose that development setup as production infrastructure
without network controls, authentication, TLS, backups, and monitoring.

The Nginx example forwards the original host and scheme headers. A real
deployment must add TLS certificate management, request-size/rate policies,
security headers, upstream health behavior, and a real hostname.

## 7. Configuration and operations

Configuration belongs outside the artifact. Use environment variables or a
secret manager for database URLs, credentials, signing keys, broker settings,
and external service tokens. Do not place secrets in `application.yml`, Docker
files, Compose files, GitHub logs, or source code.

Operational readiness includes:

- structured logs without passwords or tokens;
- health checks that distinguish liveness from readiness;
- metrics for requests, cache, queues, jobs, and database pools;
- traces across HTTP and asynchronous boundaries;
- graceful shutdown and connection draining;
- backups, migrations, rollback, and capacity plans;
- least-privilege service accounts and dependency patching.

The existing Actuator health endpoint is a learning baseline. It is not a
complete production observability design.

## 8. Validate Phase 12

Run the focused final-phase tests first, then the full course:

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
    -Dtest=ProductionAdvancedExamplesTest test
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 test
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 -DskipTests package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The executable lesson proves cache reuse, eviction, named async processing,
and a scheduled run. The final output is `PHASES 1-12 COMPLETE`.

## What remains beyond this course

- production Redis/RabbitMQ/Kafka adapters and broker-operated schema policy;
- OpenFeign, Gateway, discovery, config server, and circuit-breaker libraries;
- a real PostgreSQL deployment with migrations and backups;
- Kubernetes or a managed container platform;
- centralized logs, metrics, traces, alerting, and SLOs;
- load testing, chaos testing, and security threat modeling.

These are project-sized concerns, not abstractions to hide inside the final
beginner tutorial example.
