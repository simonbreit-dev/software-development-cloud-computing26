# Local Kubernetes and Flux GitOps Deployment Report

Date: 2026-09-01

Application repository: `/Users/simonbreit/Projects/software-development-cloud-computing26`

GitOps repository: `/Users/simonbreit/Projects/sdfcc-gitops`

Target: deploy SDFCC to the existing local Colima/K3s cluster through Flux and a separate GitOps repository.

The operator-facing execution sequence is maintained separately in
`LOCAL_K8S_FLUX_MANUAL_CHECKLIST.md`.

## Current verdict

The repository-side preparation is complete enough to publish for review, but deployment remains deliberately blocked by four manual gates:

1. The GitOps repository has no commit or GitHub remote yet.
2. The application changes must be committed and CI must publish a new ARM64-capable image and matching OCI chart.
3. A SOPS/age key and an encrypted database Secret must be created by the operator.
4. The running Flux installation must be re-bootstrapped from `fsdfcc-gitops` to the new repository after it is published.

Both Flux dev Kustomizations and the production scaffold are suspended, so publishing the current draft cannot accidentally deploy placeholder release tags.

No application image was built or pulled, and nothing was deployed to the cluster while implementing these fixes.

## Environment status

| Area | Current state | Readiness |
| --- | --- | --- |
| Kubernetes | Colima/K3s `v1.33.4+k3s1`, ARM64 node | Healthy |
| Flux | Installed and healthy | Ready, but points at the old repository |
| Node resources | 2 CPU, about 2 GiB memory; 87% used during audit | Increase before deployment |
| Storage | Default `local-path` StorageClass | Suitable for local PostgreSQL |
| Ingress | No IngressClass; Traefik disabled | Not needed; use port-forward |
| Current backend artifact | AMD64 only | Not usable as the intended native ARM64 release |
| Helm CLI | Not installed | Not required by Flux |
| SOPS and age CLI | Not installed | Required to prepare the encrypted Secret |

## Problems fixed in the application repository

### Multi-platform image publishing

The backend workflow now:

- Sets up QEMU before Buildx for push builds.
- Publishes `linux/amd64` and `linux/arm64` images on pushes.
- Keeps pull-request builds on `linux/amd64`, allowing the image to be loaded into the runner for the existing smoke test.
- Continues to publish SBOM and provenance metadata on push builds.

This change requires a new CI run. Existing GHCR images remain AMD64-only.

### Helm image digest support

The chart accepts `image.digest`. When populated, the Deployment renders:

```text
repository@sha256:digest
```

When digest is empty, the existing immutable tag behavior remains available. Digest support allows a future GitOps promotion to select the exact reviewed image manifest.

### Safe database Secret validation

The chart now fails rendering when it would generate a database Secret with empty URL, username, or password. It also fails when Secret generation is disabled without an `existingSecretName`.

CI uses a clearly non-production validation values file so linting and template checks still exercise generated-Secret rendering. GitOps uses an external SOPS-managed Secret.

### Configuration-triggered pod rollouts

The Deployment pod template now includes:

- A checksum of the generated ConfigMap.
- A checksum of the generated Secret when the chart owns it.
- `secret.rolloutToken` for explicitly rolling pods after an externally managed Secret changes.

This prevents a successful Flux/Helm reconciliation from leaving pods on stale environment values.

### GitOps-first operations runbook

`OPERATIONS.md` now describes Git commits, Flux reconciliation, and Git reverts as the normal deployment and rollback path. Direct Helm operations are documented only as break-glass actions that require suspending the owning HelmRelease and restoring Git as the source of truth afterward.

## Problems fixed in the GitOps repository

### Real application coordinates

The generic `sample-backend` and `ORG_NAME` resources were replaced with `sdfcc-backend` and the real GHCR coordinates:

```text
chart: ghcr.io/simonbreit-dev/software-development-cloud-computing26/charts/sdfcc-backend
image: ghcr.io/simonbreit-dev/software-development-cloud-computing26/sdfcc-backend
```

The remaining `REPLACE_ME` strings are intentional promotion gates. They must be replaced with the matching chart and image tags produced by the next green application CI run.

### Explicit OCI Helm layer selection

Both environments now select and copy:

```text
application/vnd.cncf.helm.chart.content.v1.tar+gzip
```

This makes the `OCIRepository` artifact contract explicit for `HelmRelease.spec.chartRef`.

### Ordered dev reconciliation

The dev root creates two Flux Kustomizations:

```text
sdfcc-dev-infrastructure
          |
          v
    sdfcc-dev-apps
```

The application depends on infrastructure and cannot reconcile until infrastructure reports Ready. Both begin suspended.

### Local PostgreSQL

The dev infrastructure includes:

- A single-replica PostgreSQL 17.6 StatefulSet.
- A 2 GiB PVC using `local-path`.
- A ClusterIP Service at `postgresql.sdfcc-dev.svc.cluster.local`.
- Startup, readiness, and liveness checks using `pg_isready`.
- CPU and memory requests/limits.
- Non-root execution, dropped Linux capabilities, disabled service-account token mounting, and RuntimeDefault seccomp.
- A PostgreSQL image pinned to multi-platform OCI index digest `sha256:ef257d85f76e48da1c64832459b59fcaba1a4dac97bf5d7450c77753542eee94`.

The pinned index was verified to contain Linux/ARM64. The pod UID/GID is set to
70, matching the `postgres` user created by this Alpine image rather than the
UID used by the Debian variant.

### Secret-management preparation

The GitOps repository now contains:

- Ignore rules for plaintext Secret files and age private keys.
- A database Secret example containing all PostgreSQL and Spring datasource keys.
- An empty secret Kustomization where the encrypted resource will be registered.
- Flux SOPS decryption configuration referencing `flux-system/sops-age`.
- Exact SOPS/age preparation commands in the GitOps README.

No real credential or private encryption key was generated or committed.

### Safe environment defaults

Dev is configured for:

- One backend replica.
- ClusterIP access with ingress disabled.
- The SOPS-managed database Secret.
- Local deployment environment and CORS values.
- Disabled telemetry exporters until a collector exists.
- Resource requests and limits.
- Explicit immutable release placeholders.

Values ConfigMaps have `reconcile.fluxcd.io/watch: Enabled` for prompt Helm reconciliation.

The production source and HelmRelease are explicitly suspended. Production ingress, TLS, secrets, database, policies, and promotion are not implied by the local setup.

### Repository documentation

The GitOps README now documents:

- Repository layout and ownership.
- Release-tag pairing.
- SOPS/age setup.
- GitHub publication and Flux bootstrap.
- Suspension gates and their required order.
- Reconciliation and verification commands.
- Port-forward access without ingress.
- Configuration rollout behavior.

## Remaining manual blockers

### 1. Publish the GitOps repository

`/Users/simonbreit/Projects/sdfcc-gitops` still has no commit or remote. Publishing it is intentionally left to the repository owner.

Before bootstrap, confirm the GitHub owner and final repository name. Do not maintain this repository and `fsdfcc-gitops` as competing sources of truth for the same cluster.

### 2. Publish the new application artifacts

The application working tree remains uncommitted. Commit the reviewed changes and let GitHub Actions build and publish them. The GitOps placeholders cannot be replaced safely until all relevant jobs pass.

Verify the resulting image index contains ARM64 without pulling or running it:

```sh
docker buildx imagetools inspect \
  ghcr.io/simonbreit-dev/software-development-cloud-computing26/sdfcc-backend:sha-<commit>
```

Then replace:

```text
chart tag: 0.1.0-sha-REPLACE_ME
image tag: sha-REPLACE_ME
```

with tags from the same commit.

### 3. Increase Colima memory

The inspected node had insufficient free memory for a reliable Spring plus PostgreSQL deployment. Increase it to at least 4 GiB; 6 GiB is preferable if observability will run locally. This restarts Colima and was not performed automatically.

Afterward verify:

```sh
colima status
kubectl get nodes
kubectl top nodes
flux check
```

### 4. Create the SOPS age key and encrypted Secret

Follow `/Users/simonbreit/Projects/sdfcc-gitops/README.md` to:

1. Install `sops` and `age`.
2. Generate and safely back up the age private key.
3. Create a unique database password.
4. Encrypt the example Secret.
5. Add only `sdfcc-database.secret.sops.yaml` to the secret Kustomization.
6. Install the private key as `flux-system/sops-age`.

This cannot be completed safely without choosing and safeguarding real secret material.

### 5. Bootstrap the new Git source

The live cluster still reconciles:

```text
ssh://git@github.com/simonbreit-dev/fsdfcc-gitops
path: ./clusters/my-cluster
```

After publication, bootstrap against the new repository and `clusters/dev`. Ensure the bootstrap-generated `clusters/dev/flux-system` directory is included by the dev root Kustomization.

Record the old source URL and revision before switching so the change is recoverable.

### 6. Unsuspend in order

After the repository, artifacts, memory, and Secret are ready:

1. Set `spec.suspend: false` in `clusters/dev/infrastructure.yaml`.
2. Commit and push.
3. Wait for PostgreSQL Ready and PVC Bound.
4. Set `spec.suspend: false` in `clusters/dev/apps.yaml`.
5. Commit and push.
6. Verify the HelmRelease and backend readiness.

## First-deployment verification

```sh
flux check
flux reconcile source git flux-system
flux reconcile kustomization flux-system --with-source
flux get sources all --all-namespaces
flux get kustomizations --all-namespaces
flux get helmreleases --all-namespaces
kubectl get pods,services,persistentvolumeclaims --namespace sdfcc-dev
kubectl get events --namespace sdfcc-dev --sort-by=.lastTimestamp
```

Success requires:

- The Git source and both dev Kustomizations are Ready.
- The OCI source and HelmRelease are Ready.
- The PostgreSQL PVC is Bound and its pod is Ready.
- The backend pod reports the expected SHA image and is Ready.
- No readable credential exists in Git or a ConfigMap.
- Database-aware application readiness reports `UP`.

Access the service without ingress:

```sh
kubectl --namespace sdfcc-dev port-forward service/sdfcc-backend 8080:8080
curl --fail --silent http://127.0.0.1:8080/actuator/health/readiness
```

Test at least one database-backed API operation afterward.

## Problems intentionally not implemented

These require larger product or production-platform work and are outside this local preparation pass:

- Real login/JWT issuance and bearer-token enforcement.
- Versioned database migrations replacing `ddl-auto=update`.
- Production backup/restore automation and recovery objectives.
- Production PostgreSQL, high availability, ingress, DNS, TLS, NetworkPolicies, quotas, PDBs, autoscaling, and alerting.
- Image/chart signing, admission verification, and complete action SHA pinning.
- Automated production rollback, which is unsafe without schema compatibility.
- Production OpenTelemetry collector and operational dashboards.

The broader background and rationale remain in `tmp/DEPLOYMENT_OPERATIONS_AUDIT.md`.

## Validation performed

No image build or deployment was performed. Validation consisted of:

- YAML parsing across both repositories.
- `kubectl kustomize` rendering of dev root, infrastructure, apps, and prod.
- Kubernetes server-side dry-run for Flux resources.
- Client dry-run for namespaced infrastructure not yet present in the cluster.
- Helm 4.2.4 lint and template rendering using a temporary checksum-verified binary; Helm was not installed.
- Positive render tests for tag and digest image modes.
- Negative render test proving empty generated database credentials fail.
- Registry metadata verification of the pinned PostgreSQL multi-platform digest.
- `git diff --check` for the application repository.

Runtime validation remains pending until the manual blockers above are completed.
