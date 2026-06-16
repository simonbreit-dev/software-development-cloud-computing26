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

Generated OpenAPI sources are committed under `backend/src/main/java/.../generated`. Do not edit them manually; update `openApiSpec.yaml`, run `make generate`, and commit the regenerated files.

### Required GitHub Settings

No SonarCloud or self-hosted SonarQube configuration is required.

Recommended branch protection for `main`:

- Require pull requests before merging.
- Require status checks: `Build and test backend`, `Local quality checks`, `CodeQL`, `Dependency Review`, and `Trivy image scan`.
- Require branches to be up to date before merging.
- Block force pushes and deletions.

The backend currently permits `/actuator/health` in security configuration, but `spring-boot-starter-actuator` is not present and the container is not self-contained for runtime health checks. Add actuator and a CI-safe runtime configuration before enabling a Docker smoke test against `/actuator/health`.
