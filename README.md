# NxtGenBackend

Java 21 / Spring Boot 4 REST API.

## Run

From this directory, provide database credentials and a JWT secret through environment variables, then run `mvn spring-boot:run`.

PowerShell example:

```powershell
$env:PROD_DB_USERNAME = "your-database-user"
$env:PROD_DB_PASSWORD = "your-database-password"
$env:PROD_JWT_SECRET = "your-long-random-secret"
mvn spring-boot:run
```

The production profile is the default. Override the TiDB JDBC URL with `PROD_DB_URL` when needed. At startup, `FlywayStartupConfig` prepares `NXTGEN_TABLE_SCHEMA_HISTORY` with a baseline at version `0`, then runs Flyway repair and migration. Add versioned SQL migrations under `src/main/resources/db/migration` (for example, `V1__create_users.sql`).

`GET /api/health` returns the API status; Swagger UI is available at `/swagger-ui.html`.
