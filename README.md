# CRM (Java)

A basic CRM for adding and removing customers (first name, last name, email). A Java port of
`example-typescript`: same REST API, same SQLite schema, same React UI.

- `backend/` — REST API built with Spring Boot 4 (Spring MVC + `JdbcClient`) storing data in a local SQLite file via `sqlite-jdbc`.
- `frontend/` — React app (Vite) that calls the API through a `/api` dev proxy (identical to the TypeScript project).

Requires Java 17 or newer and Node.js 22.13 or newer (frontend only). Maven is not needed: `backend/mvnw` downloads it.

## Setup

```sh
npm install --prefix frontend
# optional demo customers
(cd backend && ./mvnw -q spring-boot:run -Dspring-boot.run.arguments="--crm.seed=true --spring.main.web-application-type=none")
```

## Run

In two terminals:

```sh
(cd backend && PORT=4000 ./mvnw spring-boot:run)
API_PORT=4000 npm run dev --prefix frontend
```

| Variable        | Used by  | Default                   | Purpose                              |
| --------------- | -------- | ------------------------- | ------------------------------------ |
| `PORT`          | backend  | `4000`                    | Port the API listens on              |
| `DATABASE_PATH` | backend  | `backend/data/crm.sqlite` | SQLite file (ignored by git)         |
| `API_PORT`      | frontend | `4000`                    | Backend port the Vite proxy targets  |

To build a runnable jar: `cd backend && ./mvnw package` then `java -jar target/crm-backend-0.0.1-SNAPSHOT.jar`.

## Test

```sh
(cd backend && ./mvnw test)
npm test --prefix frontend
```

## API

| Method   | Path                  | Body                               | Responses                     |
| -------- | --------------------- | ---------------------------------- | ----------------------------- |
| `GET`    | `/api/customers`      | —                                  | `200` list of customers       |
| `POST`   | `/api/customers`      | `{firstName, lastName, email}`     | `201`, `400` invalid, `409` duplicate email |
| `DELETE` | `/api/customers/:id`  | —                                  | `204`, `404`                  |

Errors are returned as `{"error": "message"}`.
