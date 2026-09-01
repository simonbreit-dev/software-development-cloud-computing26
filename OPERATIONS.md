# Operations Runbook

This runbook covers the small, repeatable operational procedures supported by the repository. The application is not production-ready yet: no deployment-time user bootstrap exists, and Hibernate `ddl-auto=update` is used instead of managed database migrations.

## Release inputs

A release consists of two OCI artifacts published by `backend-ci.yml`:

- Image: `ghcr.io/simonbreit-dev/software-development-cloud-computing26/sdfcc-backend:<tag>`
- Chart: `oci://ghcr.io/simonbreit-dev/software-development-cloud-computing26/charts/sdfcc-backend`

Use immutable `sha-<commit>` image tags for deployments. Keep environment-specific values outside the chart and provide database credentials plus `JWT_PRIVATE_KEY_PEM` and `JWT_PUBLIC_KEY_PEM` through an existing Kubernetes Secret. The JWT private key must be PKCS#8 PEM, the public key must be X.509 PEM, and every replica must receive the same pair. At minimum, override `APP_SECURITY_CORS_ALLOWED_ORIGINS` and configure OTLP exporters only when a collector is reachable in the cluster.

For Docker Compose, generate the persistent local key files once before starting the backend:

```bash
mkdir -p .secrets
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out .secrets/jwt-private.pem
openssl pkey -in .secrets/jwt-private.pem -pubout -out .secrets/jwt-public.pem
chmod 600 .secrets/jwt-private.pem .secrets/jwt-public.pem
```

The `.secrets` directory is ignored by Git. Compose mounts these files read-only and requires them; preserve and back them up if access tokens must remain valid across host rebuilds. Recreating or restarting only the container does not replace the keys.

## GitOps deployment

Before deploying, confirm that the backend, deployment-configuration, and security jobs passed for the exact commit. Back up the database before any version that can change persistence entities because schema changes are currently unmanaged.

Routine deployments are owned by the separate `sdfcc-gitops` repository:

1. Update the environment's `OCIRepository.spec.ref.tag` to the exact published chart version.
2. Update the Helm values to the matching immutable image SHA tag or digest.
3. Review and merge the GitOps pull request.
4. Let Flux reconcile the commit. Do not run `helm upgrade` against a Flux-owned release.

For an explicitly requested immediate reconciliation:

```bash
flux reconcile source git flux-system
flux reconcile kustomization flux-system --with-source
flux get kustomizations --all-namespaces
flux get helmreleases --all-namespaces
```

Verify the rollout and readiness:

```bash
kubectl --namespace sdfcc rollout status deployment/sdfcc-backend --timeout=5m
kubectl --namespace sdfcc port-forward service/sdfcc-backend 8080:8080
curl --fail http://127.0.0.1:8080/actuator/health/readiness
```

## Rollback

Inspect the failed pods and events, then revert the GitOps promotion commit and let Flux reconcile the previous desired state:

```bash
flux get helmreleases --all-namespaces
kubectl --namespace <namespace> get pods
kubectl --namespace <namespace> describe deployment/sdfcc-backend
kubectl --namespace <namespace> logs deployment/sdfcc-backend --all-containers --tail=200
git revert <gitops-promotion-commit>
git push
flux reconcile kustomization flux-system --with-source
```

An application rollback does not reverse database changes made by Hibernate. If persistence entities changed, assess the database separately before rolling back.

Direct `helm rollback` or `helm upgrade` is break-glass only. Suspend the owning HelmRelease first, record the incident action, and restore Git as the source of truth before resuming Flux; otherwise the controller can overwrite the manual change.

## PostgreSQL backup and restore

The platform operating PostgreSQL should own the scheduled backup policy, encryption, retention, and off-site copies. A logical backup can be created with:

```bash
pg_dump --dbname="$DATABASE_URL" --format=custom --file=sdfcc-$(date +%Y%m%d-%H%M%S).dump
pg_restore --list sdfcc-<timestamp>.dump
```

Restore drills must use an isolated database first. Stop application writes, create a fresh target database, restore into it, point a test deployment at that database, and verify readiness plus representative reads before considering the backup usable:

```bash
pg_restore --dbname="$RESTORE_DATABASE_URL" --clean --if-exists sdfcc-<timestamp>.dump
```

Set recovery objectives with the database operator; the repository does not currently enforce an RPO or RTO.

## Common incident checks

1. Check `/actuator/health/liveness` and `/actuator/health/readiness`. Readiness includes PostgreSQL connectivity.
2. Inspect the deployment rollout, pod events, restarts, and previous container logs.
3. Confirm the database and JWT Secret keys plus database network reachability.
4. Confirm the deployed image tag and chart revision match the intended commit.
5. Check Alloy and Grafana only after application and database health are established; telemetry failure should not be mistaken for application failure.

Rotate database, JWT, and Grafana credentials in their owning secret stores, update the deployment, and verify readiness before revoking the old credentials. Rotating the JWT pair immediately invalidates existing access tokens. Application-user credential rotation cannot be documented until deployment-time user bootstrap is implemented.
