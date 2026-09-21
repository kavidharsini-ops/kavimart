# kaviMart

kaviMart is a multi-seller e-commerce marketplace. Buyers can discover products and place orders, while sellers manage their own listings. The current milestone delivers the secure foundation: database initialization, session-based registration/login, role selection for buyers and sellers, and protected account access.

## Problem statement

Small sellers need a marketplace that is easy to join and simple to operate, while buyers need a trustworthy place to discover products from multiple independent shops. kaviMart provides one shared catalog and account system without coupling the first release to a real payment provider.

## Architecture

Browser requests are handled by thin Servlets. Page Servlets forward to JSP/JSTL views, while `/api/v1/...` Servlets return Gson JSON envelopes for AJAX. Controllers call services; services apply validation and business rules; JDBC DAO implementations own all SQL and use a pooled HikariCP `DataSource`. A single `ServletContextListener` initializes the pool, runs `schema.sql` and `seed.sql`, publishes the services, and closes the pool during shutdown.

```text
Browser
  ├── JSP/JSTL pages  ── Page Servlets
  └── fetch JSON API  ── API Servlets ── Services ── DAO interfaces
                                             └── JDBC DAO ── HikariCP ── H2
```

## Tech stack

| Area | Choice |
| --- | --- |
| Runtime | JDK 17 target, Apache Tomcat 9.0.x (`javax.servlet.*`) |
| Build | Maven WAR |
| Views | JSP + JSTL |
| Browser behavior | Vanilla JavaScript + `fetch()` |
| Database | H2 embedded mode |
| Pool | HikariCP |
| JSON | Gson |
| Passwords | jBCrypt |
| Logging | SLF4J + Logback |
| Tests | JUnit 5 + Mockito |

## Setup

1. Install JDK 17 and Maven 3.8+.
2. Build and test:

   ```bash
   mvn -B clean verify
   ```

3. Deploy `target/kaviMart.war` to Apache Tomcat 9.
4. Open the application context in a browser. The default embedded database is file-based at `./data/kavimart`.

Optional environment variables:

| Variable | Default |
| --- | --- |
| `DB_URL` | `jdbc:h2:./data/kavimart;AUTO_SERVER=TRUE` |
| `DB_USERNAME` | `sa` |
| `DB_PASSWORD` | empty |

For an in-memory database during a short-lived run, use `jdbc:h2:mem:kavimart;DB_CLOSE_DELAY=-1`.

## F1 auth endpoints

All API responses use `{ "success": true|false, "data": ..., "error": null|{ "code": ..., "message": ... } }`.

- `POST /api/v1/auth/register` — creates a BUYER or SELLER account; admin registration is rejected.
- `POST /api/v1/auth/login` — validates bcrypt credentials, regenerates the session ID, and sets a 30-minute timeout.
- `POST /api/v1/auth/logout` — invalidates the current session.
- `GET /api/v1/auth/me` — returns the safe session user DTO; it never contains `passwordHash`.
- `GET /api/v1/health` — returns `{ "status": "UP", "db": "UP" }` when the database is reachable.

Seeded demo account emails are `admin@kavimart.local`, `buyer@kavimart.local`, and `seller@kavimart.local`. Their credentials are represented only by bcrypt hashes in `seed.sql`.

## Repository layout

- `src/main/java/com/kavimart/kavimart/controller` — thin Servlets
- `src/main/java/com/kavimart/kavimart/service` — business rules
- `src/main/java/com/kavimart/kavimart/dao` — interfaces and JDBC implementations
- `src/main/java/com/kavimart/kavimart/model` — entities
- `src/main/java/com/kavimart/kavimart/dto` — API/request/response shapes
- `src/main/java/com/kavimart/kavimart/filter` — encoding and session checks
- `src/main/java/com/kavimart/kavimart/listener` — pool/database lifecycle
- `src/main/resources/schema.sql` and `seed.sql` — checked-in database scripts
- `src/main/webapp/WEB-INF/jsp` — server-rendered views

## Roadmap

F2 adds seller product listing management. Later milestones add buyer search, cart, mock checkout, order history, admin moderation, and reviews restricted to completed orders.