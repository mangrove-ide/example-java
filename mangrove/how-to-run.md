# How to run

- **Prerequisites:** a JDK 17+ (`JAVA_HOME` must point at it; on this Mac Homebrew's `/usr/local/opt/openjdk` works) and Node.js 22.13+.
- **Maven:** not installed globally — `backend/mvnw` (Maven Wrapper, only-script mode) downloads Maven 3.9 into `~/.m2/wrapper` on first run.
- **Env vars:** `PORT` (API port), `DATABASE_PATH` (SQLite file, default `backend/data/crm.sqlite`), `API_PORT` (frontend proxy target). No secrets.
- **Setup:** `npm install --prefix frontend`; seed with `cd backend && ./mvnw -q spring-boot:run -Dspring-boot.run.arguments="--crm.seed=true --spring.main.web-application-type=none"`.
- **Start:** `cd backend && PORT=<api> ./mvnw spring-boot:run` and `API_PORT=<api> npm run dev --prefix frontend -- --port <web> --strictPort`.
- **Problems hit:** `java` on PATH was the macOS stub ("Unable to locate a Java Runtime"); set `JAVA_HOME=/usr/local/opt/openjdk`. First `mvnw` run takes ~1 min to download dependencies.
