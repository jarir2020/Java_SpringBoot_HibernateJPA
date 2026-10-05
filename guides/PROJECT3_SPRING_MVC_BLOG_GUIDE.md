# Project 3 — Spring MVC Blog Studio

Project 3 is the first full-stack project in the progression. A Spring Boot
application serves a small browser frontend and a REST backend from one local
process. It is intentionally educational rather than production-grade.

## 1. Run it in a browser

```bash
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar --project3
```

Open:

```text
http://localhost:8080/project3/
```

The browser page can:

- list and search seeded posts;
- create authors;
- create, edit, and delete posts;
- publish or save drafts;
- add and remove comments.

The backend is available at `/api/project3/**`. Its in-memory repository is
reset whenever the application restarts.

## 2. Architecture

```text
HTML + CSS + JavaScript
          ↓ fetch()
BlogController (@RestController)
          ↓
BlogService (@Service)
          ↓
BlogRepository (interface)
          ↓
InMemoryBlogRepository (@Repository)
```

The frontend is deliberately plain browser code in
`src/main/resources/static/project3`. That keeps the HTTP boundary visible
before introducing a JavaScript framework or a separate frontend build tool.

The API supports:

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/api/project3/users` | List authors. |
| POST | `/api/project3/users` | Create an author. |
| GET | `/api/project3/posts?search=text` | List or search posts. |
| GET | `/api/project3/posts/{id}` | Read a post with comments. |
| POST | `/api/project3/posts` | Create a post. |
| PUT | `/api/project3/posts/{id}` | Update a post. |
| DELETE | `/api/project3/posts/{id}` | Delete a post and its comments. |
| POST | `/api/project3/posts/{id}/comments` | Add a comment. |
| DELETE | `/api/project3/comments/{id}` | Remove a comment. |

## 3. What to notice in the backend

### Controller

`BlogController` translates HTTP requests into service calls. It owns routes,
request bodies, validation activation, and HTTP status codes. It does not know
how the maps in the repository work.

### Service

`BlogService` owns rules such as:

- an author must exist before a post or comment can reference it;
- user email and post title conflicts return a conflict response;
- deleting a post also deletes its comments;
- post searches match title and body.

### Repository

`BlogRepository` is the storage boundary. The in-memory implementation provides
seed data and can be replaced by a database implementation in Project 4.

### Validation and errors

Jakarta Validation annotations reject invalid request bodies. The
`BlogExceptionHandler` converts validation failures, missing resources,
duplicate resources, malformed JSON, and invalid arguments into JSON errors
that the browser can display.

## 4. Frontend boundary

`app.js` uses `fetch()` against the same origin, so no CORS configuration is
needed. It keeps a small browser state object, renders the post list, and
updates the detail panel after API mutations. `style.css` provides the layout
and responsive behavior without a frontend framework.

The same-origin setup is useful for learning:

```text
browser URL:      /project3/
frontend assets:   /project3/index.html, style.css, app.js
backend resources: /api/project3/**
```

## 5. Focused tests

```bash
mvn -Dmaven.repo.local=/tmp/java-spring-learning-m2 \
  -Dtest=BlogProjectWebTest test
```

The test starts the Project 3 Spring Boot context, verifies that the browser
page is served, reads seeded API data, creates a user, creates a post, adds a
comment, and checks invalid input responses.

## 6. Deliberate boundaries

This project does not add JPA, a database, authentication, authorization,
pagination, file uploads, or a separate frontend toolchain. Those are useful
next topics, but adding them now would hide the MVC and browser/API boundary.

## 7. Exercises

1. Add a category field and a category filter.
2. Add a post publication toggle endpoint and a matching frontend control.
3. Add pagination to the list endpoint and the browser.
4. Replace the in-memory repository with a JPA repository in Project 4.
5. Add login and ownership checks after studying Spring Security.

The next progression project will build an e-commerce backend with Spring Boot
and JPA while keeping a browser frontend available for manual exploration.
