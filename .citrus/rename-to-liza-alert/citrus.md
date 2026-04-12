# CITRUS Delivery Record

- Change set: `rename-to-liza-alert`
- Goal: align project naming, runtime configuration, and Java application identity with `liza_alert`
- Scope:
  - Maven coordinates and project metadata
  - Spring application name and datasource names
  - Docker Compose database and container naming
  - Java package prefix and application class naming
  - existing tests updated to new package/application names

## Validation

- `./mvnw.cmd clean test`
- `docker compose config`
- `docker compose up -d`
- `curl http://localhost:8080/api/v1/lost-cases`
- `docker compose exec -T postgres psql -U postgres -d liza_alert -c "\dt"`

## Outcome

- Build and tests passed
- API responded with HTTP 200
- Liquibase created tables in the renamed PostgreSQL database `liza_alert`
- No legacy `landingdemo` or `landing-demo` identifiers remain in tracked source/config files
