# Phase 10 — Spring Security

Phase 10 adds a security boundary around the Phase 9 API:

```text
HTTP request
    ↓
SecurityFilterChain
    ↓ authenticate and authorize
Spring MVC controller
    ↓
transactional service → repository → database
```

The lesson uses servlet-based Spring Security with Java configuration. Spring
Security's [Java configuration](https://docs.spring.io/spring-security/reference/7.0/servlet/configuration/java.html)
creates the security filter chain that protects servlet requests. The example
uses HTTP Basic only because it makes the first authentication flow visible in
a small local test.

## 1. Authentication versus authorization

Authentication answers:

```text
Who is this caller?
```

Authorization answers:

```text
What is this authenticated caller allowed to do?
```

The Phase 10 configuration has two local users:

```text
reader / reader-password  → ROLE_USER
admin  / admin-password   → ROLE_USER, ROLE_ADMIN
```

The credentials are source-controlled teaching fixtures only. A real
application must load users from a protected store and obtain secrets from
deployment configuration or a secret manager.

`UserDetailsService` supplies the username, encoded password, and authorities
to the username/password authentication provider. Spring Security documents
that `UserDetailsService` can be backed by in-memory, JDBC, or other stores;
this phase chooses in-memory storage to keep the security concepts isolated.
See the [UserDetailsService reference](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/user-details-service.html).

## 2. Password hashing

Passwords must not be stored as plain text. The configuration exposes a
`BCryptPasswordEncoder` bean and encodes each teaching user's password before
placing it in the in-memory manager.

```java
String encoded = passwordEncoder.encode("reader-password");
passwordEncoder.matches("reader-password", encoded);
```

The encoded value is intentionally different each time because BCrypt uses a
salt. Authentication checks the submitted password with `matches`; it does not
decrypt the stored value. Spring's [PasswordEncoder documentation](https://docs.spring.io/spring-security/reference/7.0/servlet/authentication/passwords/password-encoder.html)
explains this one-way storage contract.

Do not replace BCrypt with a fast general-purpose hash such as plain SHA-256
for password storage. Password hashing should be deliberately expensive and
configured for the environment's security/performance requirements.

## 3. The security filter chain

`SpringSecurityConfiguration` declares a `SecurityFilterChain` using
`HttpSecurity`:

- `/api/security/public` is allowed without credentials;
- `/api/security/**` requires an authenticated caller;
- `GET /api/employees...` requires `ROLE_USER` or `ROLE_ADMIN`;
- `POST /api/employees...` requires `ROLE_ADMIN`;
- anything else is denied by default.

HTTP Basic reads the `Authorization` header and creates an authenticated
security context for the current request. The official [HTTP Basic reference](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/basic.html)
describes the `BasicAuthenticationFilter` flow.

The API returns JSON rather than a browser login page. `JsonSecurityErrorHandler`
implements both `AuthenticationEntryPoint` and `AccessDeniedHandler`:

- missing or invalid credentials → `401 Unauthorized`;
- valid credentials without enough authority → `403 Forbidden`.

The handler intentionally sends a stable message and path, not an exception
message, password, stack trace, or internal authentication detail.

## 4. Roles and authorities

Calling `.roles("USER")` creates the authority `ROLE_USER`. Therefore these
rules are equivalent in intent:

```java
hasRole("ADMIN")
hasAuthority("ROLE_ADMIN")
```

`hasRole` adds the conventional `ROLE_` prefix. Authorities are the underlying
strings; roles are a convenient convention for grouping them.

The `/api/security/me` endpoint exposes the authenticated username and
authorities so the distinction can be observed in a response.

## 5. Method security

URL rules protect the HTTP boundary, while method security protects an
application method regardless of which controller or caller reaches it.

`@EnableMethodSecurity` activates the method interceptor, and the lesson uses:

```java
@PreAuthorize("hasRole('ADMIN')")
public ApiResponse<String> adminOnly() {
    ...
}
```

The route itself requires authentication, but the method annotation performs
the final role check. A `reader` is authenticated yet receives `403`; an
`admin` passes. This demonstrates why authentication alone is not
authorization.

## 6. Session policy

The example explicitly uses:

```java
.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
```

That means the API does not create a server-side login session. Each HTTP
Basic request carries its own credentials. This is a deliberate API choice and
also makes the upcoming JWT comparison clear: JWTs are another stateless
request credential.

Session-backed form login is a different design. It would use a session to
retain the security context after a login, while a browser application would
also need CSRF protection. This lesson disables CSRF because it is a stateless
header-authenticated JSON API; that choice must be revisited if the client
uses cookies or server sessions.

## 7. Security and the existing API

Phase 9 remains useful as the application layer:

```text
security filter → EmployeeController → EmployeeService → EmployeeRepository
```

Security does not replace DTO validation, transaction boundaries, or domain
rules. For example, an admin can pass authentication and authorization but
still receive `400` for an invalid request or `409` for a duplicate email.

The API tests attach the real `FilterChainProxy` to `MockMvc`; this is important
because a Spring context can contain a security configuration without a mock
request automatically passing through its filters.

## 8. Run the lesson

From the repository root:

```bash
mvn test
mvn package
java -jar target/java-spring-learning-0.1.0-SNAPSHOT.jar
```

The output demonstrates a public request, an anonymous `401`, a `USER` read,
a `USER` create denial, an `ADMIN` create success, BCrypt matching, and method
security denial.

The security configuration is
[`SpringSecurityConfiguration.java`](../src/main/java/com/jarirahmed/springsecurity/config/SpringSecurityConfiguration.java).
The JSON security handler is
[`JsonSecurityErrorHandler.java`](../src/main/java/com/jarirahmed/springsecurity/error/JsonSecurityErrorHandler.java),
and the executable lesson is
[`SpringSecurityLesson.java`](../src/main/java/com/jarirahmed/springsecurity/demo/SpringSecurityLesson.java).

## What Phase 10 does not teach yet

- no database-backed account repository;
- no registration, login, logout, or password-reset workflow;
- no JWT access or refresh tokens;
- no OAuth2 or OpenID Connect;
- no production secret management;
- no browser session/CSRF application.

Those topics build on the authentication and authorization vocabulary learned
here.
