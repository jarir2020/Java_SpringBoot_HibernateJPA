# Project 4 — Spring Boot + JPA Storefront

Project 4 is a full-stack e-commerce learning application. Spring Boot serves
the browser frontend and the backend API from one executable JAR. The backend
uses JPA entities and an H2 teaching database. It is intentionally focused on
important learning boundaries rather than production-scale commerce.

## 1. Run it in a browser

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --project4
```

Open:

```text
http://localhost:8080/project4/
```

The frontend supports product browsing, search, category filtering, pagination,
adding and removing cart items, authenticated demo checkout, and order history
with payment status.

Use the teaching credentials `shopper` / `shopper-password`. The credentials,
H2 data, and payment result are local fixtures, not production security or
payment processing.

## 2. Architecture

```text
HTML + CSS + JavaScript
          ↓ Basic-auth fetch()
StorefrontController (@RestController)
          ↓
StorefrontService (@Service, @Transactional)
          ↓
EntityManager repositories
          ↓
JPA entities → Hibernate → H2
```

The entity model contains the requested commerce relationships:

```text
StoreUser ── 1:1 ── Cart ── 1:N ── CartItem ── N:1 ── Product ── N:1 ── Category
    │
    └── 1:N ── StoreOrder ── 1:N ── OrderItem
                         └── 1:1 ── Payment
```

## 3. Important backend topics

### JPA entities and relationships

Each entity has a protected no-argument constructor for JPA and an ordinary
constructor for application code. The mappings demonstrate `@ManyToOne`,
`@OneToOne`, `@OneToMany`, cascade, orphan removal, enum persistence, and a
product-name/unit-price snapshot in `OrderItem`.

### Explicit Boot-to-JPA configuration

`StorefrontJpaConfiguration` creates an H2 `DataSource`, scans the storefront
entities, creates a Hibernate `EntityManagerFactory`, and registers a
`JpaTransactionManager`. This is more explicit than adding every Spring Data
abstraction at once, so the connection between Boot, JPA, and Hibernate remains
visible.

### JPQL and pagination

`ProductRepository.search` uses JPQL to join products with categories, apply a
search pattern, count matching rows, and fetch only the requested page. The
controller returns a generic `PageResponse<T>` DTO instead of exposing JPA
entities to the browser.

### Transactional checkout

`StorefrontService.checkout` is one transaction. It loads the authenticated
user's cart, checks and decreases product inventory, creates the order and
order-item snapshots, creates the demo payment row, and clears the cart. If
inventory validation fails, the transaction raises an error before commit.

### Authentication

Spring Security protects cart, checkout, user, and order endpoints with HTTP
Basic authentication. Product and category browsing plus the frontend are
public. The JavaScript includes the demo credentials strictly so the local
learning page can be browsed without a separate login screen.

## 4. API map

| Method | Path | Access | Purpose |
| --- | --- | --- | --- |
| GET | `/api/project4/categories` | Public | List categories. |
| GET | `/api/project4/products` | Public | Search and paginate products. |
| GET | `/api/project4/me` | Authenticated | Read the demo user. |
| GET | `/api/project4/cart` | Authenticated | Read the current cart. |
| POST | `/api/project4/cart/items` | Authenticated | Add a product. |
| DELETE | `/api/project4/cart/items/{productId}` | Authenticated | Remove a product. |
| POST | `/api/project4/checkout` | Authenticated | Create order and payment. |
| GET | `/api/project4/orders` | Authenticated | Read order history. |

## 5. Focused tests

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
  -Dtest=StorefrontWebTest test
```

The web test checks the browser route and static HTML, public catalog JSON,
anonymous rejection, authenticated cart access, transactional checkout, and
order history.

## 6. Deliberate boundaries

This project does not attempt real payment processing, customer registration,
shipping integration, stock reservations, or production-grade security. It
also uses a simple manually configured JPA repository boundary instead of
introducing every Spring Data abstraction at once.

The next project will build an HRM/payroll SaaS with multiple companies,
attendance, leave, roles, reports, notifications, Redis, and background jobs;
it will keep the browser frontend and backend together.
