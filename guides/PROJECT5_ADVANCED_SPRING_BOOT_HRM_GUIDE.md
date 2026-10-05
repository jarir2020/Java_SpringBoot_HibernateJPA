# Project 5 — Advanced Spring Boot HRM / Payroll SaaS

Project 5 is a full-stack human-resources and payroll learning application.
Spring Boot serves both the browser dashboard and the REST API from one
executable JAR. The backend uses explicit JPA configuration and disposable H2
data so the important boundaries remain visible.

## 1. Run it in a browser

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --project5
```

Open:

```text
http://localhost:8080/project5/
```

The dashboard starts on the `NSTAR` / Northstar Labs tenant. Use the company
selector to switch to the second seeded tenant.

Teaching credentials:

| User | Password | Roles | Purpose |
| --- | --- | --- | --- |
| `admin` | `admin-password` | ADMIN, HR_MANAGER, EMPLOYEE | Full dashboard demo. |
| `hr` | `hr-password` | HR_MANAGER, EMPLOYEE | HR workflow without ADMIN. |
| `employee` | `employee-password` | EMPLOYEE | Employee-level attendance and leave. |

These are local fixtures embedded in the learning application. They are not a
production identity system.

## 2. Architecture

```text
HTML + CSS + JavaScript
          ↓ Basic-auth fetch()
HrmController (@RestController)
          ↓
HrmService (@Service, @Transactional)
          ↓
EntityManager repositories
          ↓
JPA entities → Hibernate → H2
```

The main tenant relationships are:

```text
Company ── 1:N ── Employee ── 1:N ── Attendance
                         │
                         ├── 1:N ── LeaveRequest
                         └── 1:N ── PayrollEntry ── N:1 ── PayrollRun ── N:1 ── Company

Company ── 1:N ── Notification
Company ── 1:N ── AuditLog
```

The service accepts a company code on read/report operations and resolves the
company before querying its employees, leave, payroll, notifications, and
audit rows. This makes the tenant boundary easy to trace in JPQL.

## 3. Backend topics

### Multi-company data

`Company` is the tenant root. Employees point to a company with
`@ManyToOne`, and repository queries join through that relationship rather than
loading all tenants into the controller. The seed data contains Northstar Labs
and Banyan Foods.

### Attendance and leave workflows

Attendance has a unique `(employee_id, work_date)` constraint. The service
implements an upsert: marking the same employee/day changes the existing record
instead of creating a duplicate. Leave requests have explicit PENDING,
APPROVED, and REJECTED state, and the HR approval endpoint is protected with
method security.

### Payroll transaction

`HrmService.runPayroll` creates one `PayrollRun` and one `PayrollEntry` per
active employee in a single transaction. Each entry stores gross, deduction,
and net salary snapshots. The lesson uses a simple 10% deduction formula; it
is not tax or legal payroll advice.

### Roles and permissions

Spring Security uses Basic authentication for the local demo. All Project 5
API routes require authentication. Payroll processing, leave approval, audit
logs, and reminder jobs require `HR_MANAGER` or `ADMIN`; employees can record
attendance and submit leave requests.

### Reports and cache

The payroll report is computed from the run entries and cached with a key like
`hrm:payroll-report:NSTAR:2026-09`. `RedisStyleReportCache` exposes key/value,
TTL, and eviction behavior through a small `ReportCache` interface. It uses a
concurrent map so the tutorial runs without Docker or an external Redis
server. Spring Data Redis can replace this adapter later without changing the
service contract.

### Async notifications and scheduled jobs

Payroll and leave actions create notification queue rows. The
`NotificationDispatcher` waits until the transaction commits and then uses an
`@Async` executor to mark the row SENT. `HrmReminderJob` runs the same leave
reminder use case on a schedule. The dashboard button calls the endpoint
manually so the behavior can be observed immediately while learning.

### Audit logs

Attendance, leave, approval, payroll, and reminder actions create audit rows
with actor, resource, details, and timestamp. The dashboard reads those rows
through a DTO endpoint rather than exposing JPA entities.

## 4. API map

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/project5/companies` | Authenticated | List active tenant companies. |
| GET | `/api/project5/dashboard?company=NSTAR` | Authenticated | Read tenant metrics and latest payroll report. |
| GET | `/api/project5/employees?company=NSTAR` | Authenticated | List active employees for a company. |
| POST | `/api/project5/attendance` | Authenticated | Create or update a daily attendance record. |
| GET | `/api/project5/leave?company=NSTAR` | Authenticated | Read leave workflow rows. |
| POST | `/api/project5/leave` | Authenticated | Submit a leave request. |
| PATCH | `/api/project5/leave/{id}/approve` | HR/Admin | Approve a pending request. |
| POST | `/api/project5/payroll/runs` | HR/Admin | Process one company/month payroll run. |
| GET | `/api/project5/payroll/report` | Authenticated | Read a cached payroll report. |
| GET | `/api/project5/notifications` | Authenticated | Read notification queue history. |
| GET | `/api/project5/audit` | HR/Admin | Read recent audit events. |
| POST | `/api/project5/jobs/leave-reminders` | HR/Admin | Trigger the reminder job manually. |

## 5. Focused tests

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
  -Dtest=HrmWebTest test
```

The web test checks the browser route, dashboard seed data, cache miss/hit
behavior, authentication, role permissions, attendance, payroll, leave
approval, and the reminder job endpoint.

## 6. Deliberate boundaries

This project does not attempt real payroll compliance, tax rules, employee
registration, JWT/SSO, real Redis, email delivery, distributed locks, or
production tenant isolation. The “Redis-shaped” adapter, in-memory security
users, H2 database, simple deduction rule, and browser credentials are all
intentional teaching boundaries. They keep the concepts runnable locally
without hiding the architecture behind infrastructure setup.
