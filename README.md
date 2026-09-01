# software-development-cloud-computing26

This is a student project for the course "Software Development for Cloud Computing" at the Media University of applied
sciences in Stuttgart,Germany.

### Goals of the course are:


### Goals for the software project are:

- To implement a REST API for a simple online shop application using the OpenAPI specification.
- To use the [OpenAPI Generator](https://openapi-generator.tech/) to generate endpoint interfaces to speed up
  development.
- Gain experience using hexagonal architecture and test-driven development (TDD) principles.
- Work collaboratively in a team on GitHub

## Install openapi-generator

``` bash
docker pull openapitools/openapi-generator-cli:v7.22.0
```

## Backend CI/CD

The backend pipelines live in `.github/workflows/` and cover the project through Phase 3:

- `backend-ci.yml` validates the OpenAPI spec, checks generated sources for drift, runs `./mvnw clean verify`, publishes JUnit results, uploads JaCoCo/build artifacts, and builds the backend Docker image.
- `quality.yml` runs local Maven verification and generates JaCoCo XML coverage at `backend/target/site/jacoco/jacoco.xml`.
- `security.yml` runs CodeQL for Java, Dependency Review on pull requests, and a Trivy scan of the backend Docker image.

OpenAPI generation is intentionally not duplicated in GitHub Actions. CI uses the existing Makefile targets:

```bash
make validate-spec
make check-generated
cd backend && ./mvnw -B clean verify
docker build -f backend/Dockerfile -t sdfcc-backend:local .
```

Local image builds require Docker Buildx so Maven dependency layers can use the
modern BuildKit cache. The production image build skips both test execution and
test compilation because the Maven verification job runs the full test suite
before the image is published. Only `backend/src/main` is copied into the image
build, so changes limited to tests or generator configuration do not invalidate
the application compilation layer.

Generated OpenAPI sources are committed under `backend/src/main/java/.../generated`. Do not edit them manually; update `openApiSpec.yaml`, run `make generate`, and commit the regenerated files.

When the backend is running locally, its runtime-generated OpenAPI JSON is available at `http://localhost:8080/v3/api-docs`.

### Required GitHub Settings

No SonarCloud or self-hosted SonarQube configuration is required.

Recommended branch protection for `main`:

- Require pull requests before merging.
- Require status checks: `Build and test backend`, `Local quality checks`, `CodeQL`, `Dependency Review`, and `Trivy image scan`.
- Require branches to be up to date before merging.
- Block force pushes and deletions.

The backend exposes Actuator liveness and database-aware readiness endpoints. Compose and Helm use those endpoints for health checks, and pull requests run a built-container smoke test.

Deployment, rollback, database backup/restore, and incident checks are documented in [OPERATIONS.md](OPERATIONS.md). Authentication and managed schema migrations remain prerequisites for a production release.
