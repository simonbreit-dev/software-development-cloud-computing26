# Manual Checklist: Local SDFCC Deployment with Flux

Date: 2026-09-01

Application repository:
`/Users/simonbreit/Projects/software-development-cloud-computing26`

GitOps repository:
`/Users/simonbreit/Projects/sdfcc-gitops`

Cluster: existing Colima/K3s profile `default`, Kubernetes context `colima`

This checklist starts from the repository state prepared on 2026-09-01. Run
the phases in order. Do not unsuspend a later phase when an earlier gate has not
passed.

## Safety rules

- [ ] Do not commit `.age-key.txt` or any other age private key.
- [ ] Do not commit `sdfcc-database.secret.yaml` or another plaintext Secret.
- [ ] Commit only the SOPS-encrypted `sdfcc-database.secret.sops.yaml`.
- [ ] Keep `clusters/prod` suspended. This checklist deploys only `clusters/dev`.
- [ ] Do not use `latest` or `main` as a GitOps image or chart version.
- [ ] Do not run direct `helm upgrade` against a Flux-owned release.
- [ ] Do not delete the existing Flux repository or resources until the new
      source has reconciled successfully and the expected cleanup is understood.
- [ ] Do not delete the PostgreSQL PVC unless the local database is intentionally
      disposable.
- [ ] Do not build an image locally for this workflow. GitHub Actions is the
      release builder.

## Phase 0: Confirm the starting state

- [ ] Open a terminal and select the application repository:

  ```sh
  cd /Users/simonbreit/Projects/software-development-cloud-computing26
  ```

- [ ] Confirm the current branch and remote. At the time this checklist was
      written, the branch was `feat/devops`:

  ```sh
  git branch --show-current
  git remote -v
  ```

- [ ] Review both staged and unstaged application changes before committing:

  ```sh
  git status --short
  git diff --stat
  git diff
  git diff --cached --stat
  git diff --cached
  git diff --cached --check
  ```

- [ ] Confirm that the new deployment files are present:

  ```sh
  test -f LOCAL_K8S_FLUX_DEPLOYMENT_REPORT.md
  test -f LOCAL_K8S_FLUX_MANUAL_CHECKLIST.md
  test -f charts/sdfcc-backend/examples/values-ci.yaml
  ```

- [ ] Stage the newly updated report and checklist after reviewing them:

  ```sh
  git add \
    LOCAL_K8S_FLUX_DEPLOYMENT_REPORT.md \
    LOCAL_K8S_FLUX_MANUAL_CHECKLIST.md
  git status --short
  git diff --cached --check
  ```

- [ ] Confirm that the intended Kubernetes context is active:

  ```sh
  kubectl config current-context
  kubectl get nodes -o wide
  flux check
  ```

  Expected context: `colima`. Expected node architecture: `arm64`.

- [ ] Record the old Flux source before switching repositories:

  ```sh
  kubectl --namespace flux-system get gitrepository flux-system \
    -o jsonpath='{.spec.url}{"\n"}{.spec.ref.branch}{"\n"}'
  kubectl --namespace flux-system get kustomization flux-system \
    -o jsonpath='{.spec.path}{"\n"}{.status.lastAppliedRevision}{"\n"}'
  ```

  Expected old source before the switch:
  `ssh://git@github.com/simonbreit-dev/fsdfcc-gitops`, path
  `./clusters/my-cluster`.

### Gate 0

- [ ] Stop if the context is not `colima`, the node is not Ready, or the staged
      application changes contain something you do not intend to publish.

## Phase 1: Commit and publish the application release

- [ ] Commit the reviewed application changes:

  ```sh
  cd /Users/simonbreit/Projects/software-development-cloud-computing26
  git commit -m "Prepare backend deployment for Flux and local Kubernetes"
  ```

  Do not run this command until `git diff --cached` has been reviewed. The
  application repository already had staged changes when this checklist was
  written.

- [ ] Push the current feature branch:

  ```sh
  git push --set-upstream origin feat/devops
  ```

- [ ] Open or update the pull request into `main`:

  ```sh
  gh pr create \
    --base main \
    --head feat/devops \
    --title "Prepare backend deployment for Flux and local Kubernetes" \
    --body "Adds deployment hardening, multi-platform publishing, Helm validation, and local Flux preparation."
  ```

- [ ] Wait for the pull-request checks. The PR image smoke test runs here, but
      release artifacts are not published by a pull-request workflow:

  ```sh
  gh pr checks --watch
  ```

- [ ] Review and merge the pull request only after required checks pass. Use the
      repository's normal merge policy; do not bypass branch protection.

- [ ] Fetch the updated `main` branch after merge and record the seven-character
      merge/squash commit SHA used by the push workflow:

  ```sh
  git fetch origin main
  git rev-parse --short=7 origin/main
  ```

  For the resulting `main` push, the expected artifact names are:

  ```text
  image: sha-<seven-character-SHA>
  chart: 0.1.0-sha-<seven-character-SHA>
  ```

- [ ] Confirm GitHub CLI authentication if it will be used to inspect CI:

  ```sh
  gh auth status
  ```

- [ ] Locate the post-merge `main` workflow run that publishes the artifacts:

  ```sh
  gh run list --workflow backend-ci.yml --branch main --limit 5
  ```

- [ ] Wait for the exact run to finish and require a successful exit status:

  ```sh
  gh run watch <run-id> --exit-status
  ```

- [ ] Confirm that all relevant jobs passed across the PR and post-merge runs:

  - Backend Maven verification.
  - Pull-request smoke test if this was promoted through a PR.
  - Deployment configuration and Helm validation.
  - Multi-platform image publication.
  - OCI Helm chart publication.
  - Security workflow jobs required by branch policy.

- [ ] Verify that the published image manifest contains both required runtime
      platforms without running the image:

  ```sh
  docker manifest inspect \
    ghcr.io/simonbreit-dev/software-development-cloud-computing26/sdfcc-backend:sha-<seven-character-SHA> \
    | jq -r '.manifests[].platform | "\(.os)/\(.architecture)"'
  ```

  Required output includes:

  ```text
  linux/amd64
  linux/arm64
  ```

  `unknown/unknown` entries may be attached SBOM or provenance manifests; they
  are not runnable platforms.

- [ ] Verify that the matching OCI Helm artifact exists. This downloads only the
      small chart, not the application image:

  ```sh
  CHART_CHECK_DIR="$(mktemp -d /private/tmp/sdfcc-chart-check.XXXXXX)"
  flux pull artifact \
    oci://ghcr.io/simonbreit-dev/software-development-cloud-computing26/charts/sdfcc-backend:0.1.0-sha-<seven-character-SHA> \
    --output "$CHART_CHECK_DIR"
  find "$CHART_CHECK_DIR" -maxdepth 3 -type f -print
  ```

- [ ] Inspect the extracted `Chart.yaml` and confirm that its `appVersion` is the
      matching `sha-<seven-character-SHA>`.

### Gate 1

- [ ] Stop if CI is not green, the ARM64 platform is absent, the chart is absent,
      or the chart and image refer to different commits.

## Phase 2: Increase Colima resources

This phase temporarily stops Kubernetes. Colima should retain its VM disk and
Kubernetes state, but record important state first.

- [ ] Check the current profile and storage before stopping it:

  ```sh
  colima status
  colima list
  kubectl get nodes
  kubectl get persistentvolumes
  kubectl get persistentvolumeclaims --all-namespaces
  ```

- [ ] Stop Colima:

  ```sh
  colima stop
  ```

- [ ] Restart the existing profile with additional CPU and memory. This updates
      only the specified values and keeps the existing Kubernetes configuration:

  ```sh
  colima start --cpus 4 --memory 6
  ```

  Keep the architecture `aarch64`, runtime `docker`, Kubernetes enabled, and the
  existing `--disable=traefik` K3s argument. Do not opt into an unplanned K3s
  version upgrade.

- [ ] Verify the restarted environment:

  ```sh
  colima status
  kubectl config current-context
  kubectl get nodes -o wide
  kubectl top nodes
  flux check
  ```

- [ ] Confirm that the node reports approximately 6 GiB memory and remains
      `arm64` and Ready.

### Gate 2

- [ ] Stop if Kubernetes did not return Ready, Flux checks fail, or the profile
      unexpectedly changed architecture, runtime, or Kubernetes version.

## Phase 3: Prepare SOPS and age locally

- [ ] Change to the GitOps repository:

  ```sh
  cd /Users/simonbreit/Projects/sdfcc-gitops
  ```

- [ ] Install the currently missing tools:

  ```sh
  brew install sops age
  ```

- [ ] Generate a dedicated age key for this local cluster:

  ```sh
  age-keygen -o .age-key.txt
  age-keygen -y .age-key.txt
  ```

- [ ] Immediately save `.age-key.txt` in a password manager or other protected
      backup location. Do not commit or send it.

- [ ] Copy the printed public recipient beginning with `age1`.

- [ ] Create `/Users/simonbreit/Projects/sdfcc-gitops/.sops.yaml` with this
      content, replacing only the recipient:

  ```yaml
  creation_rules:
    - path_regex: clusters/dev/infrastructure/secrets/.*\.secret\.sops\.yaml$
      encrypted_regex: ^(data|stringData)$
      age: age1REPLACE_WITH_YOUR_PUBLIC_RECIPIENT
  ```

  The public recipient and `.sops.yaml` are safe to commit. The private key is
  not.

- [ ] Verify that the private key is ignored:

  ```sh
  git check-ignore -v .age-key.txt
  ```

  This command must identify `.gitignore` as the matching rule.

## Phase 4: Create the encrypted database Secret

- [ ] Copy the example to the ignored plaintext filename:

  ```sh
  cp clusters/dev/infrastructure/secrets/sdfcc-database.secret.yaml.example \
    clusters/dev/infrastructure/secrets/sdfcc-database.secret.yaml
  ```

- [ ] Generate a unique password and save it directly into the password manager.
      Avoid using a password shared with any existing PostgreSQL installation.

- [ ] Edit the plaintext file and replace both `CHANGE_ME` values with the same
      new password:

  ```text
  POSTGRES_PASSWORD
  SPRING_DATASOURCE_PASSWORD
  ```

- [ ] Confirm the intended non-secret values:

  ```text
  POSTGRES_DB=sdfcc
  POSTGRES_USER=sdfcc_app
  SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql.sdfcc-dev.svc.cluster.local:5432/sdfcc
  SPRING_DATASOURCE_USERNAME=sdfcc_app
  ```

- [ ] Encrypt the plaintext file:

  ```sh
  sops --encrypt \
    clusters/dev/infrastructure/secrets/sdfcc-database.secret.yaml \
    > clusters/dev/infrastructure/secrets/sdfcc-database.secret.sops.yaml
  ```

- [ ] Confirm that SOPS can decrypt it without printing the plaintext:

  ```sh
  sops --decrypt \
    clusters/dev/infrastructure/secrets/sdfcc-database.secret.sops.yaml \
    >/dev/null
  ```

- [ ] Confirm the committed candidate contains SOPS metadata and no placeholders:

  ```sh
  rg -n '^sops:|ENC\[' \
    clusters/dev/infrastructure/secrets/sdfcc-database.secret.sops.yaml
  if rg -n 'CHANGE_ME' \
    clusters/dev/infrastructure/secrets/sdfcc-database.secret.sops.yaml; then
    echo "ERROR: encrypted Secret still contains a placeholder"
  fi
  ```

- [ ] Edit
      `clusters/dev/infrastructure/secrets/kustomization.yaml` so it contains:

  ```yaml
  apiVersion: kustomize.config.k8s.io/v1beta1
  kind: Kustomization
  resources:
    - sdfcc-database.secret.sops.yaml
  ```

- [ ] Delete the ignored plaintext working file after encryption and validation:

  ```sh
  rm clusters/dev/infrastructure/secrets/sdfcc-database.secret.yaml
  ```

- [ ] Confirm the plaintext file is gone and the private key remains ignored:

  ```sh
  test ! -e clusters/dev/infrastructure/secrets/sdfcc-database.secret.yaml
  git check-ignore -v .age-key.txt
  git status --short --ignored
  ```

### Gate 4

- [ ] Stop if decryption fails, a `CHANGE_ME` value remains, the plaintext file is
      visible to Git, or the age private key is not ignored.

## Phase 5: Select the application release in GitOps

- [ ] Replace the dev chart placeholder in:

  ```text
  clusters/dev/apps/sdfcc-backend/source.yaml
  ```

  Replace:

  ```text
  0.1.0-sha-REPLACE_ME
  ```

  with the chart tag from Phase 1.

- [ ] Replace the dev image placeholder in:

  ```text
  clusters/dev/apps/sdfcc-backend/values.yaml
  ```

  Replace:

  ```text
  sha-REPLACE_ME
  ```

  with the image tag from the same commit.

- [ ] Leave both dev Flux Kustomizations suspended for now:

  ```sh
  rg -n 'suspend: true' clusters/dev/infrastructure.yaml clusters/dev/apps.yaml
  ```

- [ ] Confirm no dev release placeholders remain. Production placeholders are
      expected because production stays suspended:

  ```sh
  if rg -n 'REPLACE_ME' clusters/dev; then
    echo "ERROR: a dev promotion placeholder remains"
  fi
  ```

- [ ] Render every Kustomize entry point:

  ```sh
  kubectl kustomize clusters/dev >/dev/null
  kubectl kustomize clusters/dev/infrastructure >/dev/null
  kubectl kustomize clusters/dev/apps >/dev/null
  kubectl kustomize clusters/prod >/dev/null
  ```

- [ ] Validate the Flux resources without applying them:

  ```sh
  kubectl apply --dry-run=server -k clusters/dev -o name
  kubectl kustomize clusters/dev/apps \
    | kubectl apply --dry-run=server -f - -o name
  ```

- [ ] Confirm production remains suspended:

  ```sh
  rg -n 'suspend: true' \
    clusters/prod/apps/sdfcc-backend/source.yaml \
    clusters/prod/apps/sdfcc-backend/release.yaml
  ```

## Phase 6: Review, commit, and publish the GitOps repository

- [ ] Review all files before staging:

  ```sh
  cd /Users/simonbreit/Projects/sdfcc-gitops
  git status --short
  find . -path './.git' -prune -o -type f -print | sort
  ```

- [ ] Confirm no private key or plaintext Secret is tracked:

  ```sh
  git ls-files | rg 'age-key|sdfcc-database\.secret\.yaml$' || true
  git check-ignore -v .age-key.txt
  ```

- [ ] Stage the prepared repository. Git ignore rules should exclude the private
      key and plaintext Secret:

  ```sh
  git add .
  ```

- [ ] Inspect the exact staged content:

  ```sh
  git status --short
  git diff --cached --stat
  git diff --cached --check
  git diff --cached
  ```

- [ ] Repeat the secret scan against staged filenames:

  ```sh
  if git diff --cached --name-only \
    | rg -q '(^|/)(\.age-key\.txt|age-key\.txt|sdfcc-database\.secret\.yaml)$'; then
    echo "ERROR: secret material is staged"
    exit 1
  else
    echo "No private key or plaintext Secret is staged"
  fi
  ```

  Manually confirm that the output does not contain private/plaintext files. The
  encrypted `.secret.sops.yaml` file is expected.

- [ ] Commit the initial GitOps state:

  ```sh
  git commit -m "Prepare SDFCC local deployment with Flux"
  ```

- [ ] Publish the repository using one of these mutually exclusive paths.

  If GitHub should create a new private personal repository:

  ```sh
  gh auth status
  gh repo create simonbreit-dev/sdfcc-gitops \
    --private \
    --source=. \
    --remote=origin \
    --push
  ```

  If the GitHub repository was created separately:

  ```sh
  git remote add origin git@github.com:simonbreit-dev/sdfcc-gitops.git
  git push --set-upstream origin main
  ```

  If a public GitOps repository is explicitly intended, use `--public` instead
  of `--private`. Recheck the encrypted Secret and private-key exclusions first.

- [ ] Verify the published state:

  ```sh
  git remote -v
  git status --short
  git log -1 --oneline
  gh repo view simonbreit-dev/sdfcc-gitops
  ```

### Gate 6

- [ ] Stop if the remote is wrong, the push failed, the worktree contains an
      unreviewed staged change, or secret material appears on GitHub.

## Phase 7: Re-bootstrap Flux to the new repository

This changes the cluster's Git source and writes bootstrap files to the GitHub
repository. The GitHub CLI must be authenticated with repository administration
rights.

- [ ] Confirm the new remote is complete and both dev Kustomizations are still
      suspended.

- [ ] Supply the GitHub token to Flux without printing it:

  ```sh
  export GITHUB_TOKEN="$(gh auth token)"
  ```

- [ ] Bootstrap the private personal repository:

  ```sh
  flux bootstrap github \
    --owner=simonbreit-dev \
    --repository=sdfcc-gitops \
    --branch=main \
    --path=clusters/dev \
    --personal \
    --private
  ```

  For an intentionally public repository, replace `--private` with
  `--private=false`.

- [ ] Remove the GitHub token from the current shell after bootstrap:

  ```sh
  unset GITHUB_TOKEN
  ```

- [ ] Pull the Flux-generated commit into the local GitOps checkout:

  ```sh
  cd /Users/simonbreit/Projects/sdfcc-gitops
  git pull --ff-only
  ```

- [ ] Confirm that bootstrap created:

  ```text
  clusters/dev/flux-system/gotk-components.yaml
  clusters/dev/flux-system/gotk-sync.yaml
  clusters/dev/flux-system/kustomization.yaml
  ```

- [ ] Add `flux-system` to `clusters/dev/kustomization.yaml`. The result should
      contain:

  ```yaml
  resources:
    - flux-system
    - namespace.yaml
    - infrastructure.yaml
    - apps.yaml
  ```

- [ ] Validate, commit, and push that inclusion:

  ```sh
  kubectl kustomize clusters/dev >/dev/null
  git add clusters/dev/kustomization.yaml
  git diff --cached --check
  git diff --cached
  git commit -m "Include Flux bootstrap resources in dev reconciliation"
  git push
  ```

- [ ] Reconcile the root and verify the new source:

  ```sh
  flux reconcile source git flux-system
  flux reconcile kustomization flux-system --with-source
  kubectl --namespace flux-system get gitrepository flux-system \
    -o jsonpath='{.spec.url}{"\n"}{.status.artifact.revision}{"\n"}'
  kubectl --namespace flux-system get kustomization flux-system \
    -o jsonpath='{.spec.path}{"\n"}{.status.lastAppliedRevision}{"\n"}'
  flux check
  ```

  Expected URL contains `simonbreit-dev/sdfcc-gitops`; expected path is
  `./clusters/dev`.

- [ ] Check what happened to the old `podinfo` sample. With pruning enabled it
      may be removed when it is no longer in the new desired state:

  ```sh
  flux get all --all-namespaces
  kubectl get pods --all-namespaces
  ```

### Gate 7

- [ ] Stop if Flux still points at `fsdfcc-gitops`, the root Kustomization is not
      Ready, or unexpected non-sample workloads were pruned.

## Phase 8: Install the SOPS private key in the cluster

- [ ] Confirm the local key exists and is still ignored:

  ```sh
  cd /Users/simonbreit/Projects/sdfcc-gitops
  test -s .age-key.txt
  git check-ignore -v .age-key.txt
  ```

- [ ] Create or update the bootstrap Secret idempotently without printing its
      contents:

  ```sh
  kubectl --namespace flux-system create secret generic sops-age \
    --from-file=age.agekey=.age-key.txt \
    --dry-run=client \
    -o yaml \
    | kubectl apply -f -
  ```

- [ ] Verify only its presence and key name, not its value:

  ```sh
  kubectl --namespace flux-system get secret sops-age \
    -o go-template='{{.metadata.name}}{{" keys="}}{{range $k, $v := .data}}{{$k}}{{" "}}{{end}}{{"\n"}}'
  ```

  Expected key name: `age.agekey`.

## Phase 9: Unsuspend infrastructure

- [ ] Edit `clusters/dev/infrastructure.yaml` and change only:

  ```yaml
  suspend: false
  ```

- [ ] Validate and publish the change:

  ```sh
  kubectl kustomize clusters/dev >/dev/null
  git add clusters/dev/infrastructure.yaml
  git diff --cached --check
  git diff --cached
  git commit -m "Enable SDFCC dev infrastructure"
  git push
  ```

- [ ] Reconcile the root:

  ```sh
  flux reconcile kustomization flux-system --with-source
  ```

- [ ] Wait with a finite timeout for infrastructure reconciliation:

  ```sh
  kubectl --namespace flux-system wait \
    --for=condition=ready \
    kustomization/sdfcc-dev-infrastructure \
    --timeout=5m
  ```

- [ ] Verify PostgreSQL and storage:

  ```sh
  kubectl --namespace sdfcc-dev get statefulset,pods,service,persistentvolumeclaim
  kubectl --namespace sdfcc-dev rollout status statefulset/postgresql --timeout=5m
  kubectl --namespace sdfcc-dev get persistentvolumeclaim \
    -o custom-columns=NAME:.metadata.name,STATUS:.status.phase,CLASS:.spec.storageClassName,SIZE:.status.capacity.storage
  ```

- [ ] Confirm PostgreSQL accepts connections without printing the password:

  ```sh
  kubectl --namespace sdfcc-dev exec statefulset/postgresql -- \
    pg_isready -U sdfcc_app -d sdfcc
  ```

- [ ] If infrastructure fails, inspect only the relevant status and events:

  ```sh
  flux get kustomization sdfcc-dev-infrastructure --namespace flux-system
  kubectl --namespace flux-system describe kustomization sdfcc-dev-infrastructure
  kubectl --namespace sdfcc-dev get events --sort-by=.lastTimestamp
  kubectl --namespace sdfcc-dev logs statefulset/postgresql --tail=200
  ```

### Gate 9

- [ ] Do not unsuspend the application until the infrastructure Kustomization is
      Ready, the PVC is Bound, PostgreSQL is Ready, and `pg_isready` succeeds.

## Phase 10: Unsuspend the application

- [ ] Edit `clusters/dev/apps.yaml` and change only:

  ```yaml
  suspend: false
  ```

- [ ] Validate and publish the change:

  ```sh
  kubectl kustomize clusters/dev >/dev/null
  git add clusters/dev/apps.yaml
  git diff --cached --check
  git diff --cached
  git commit -m "Enable SDFCC dev application"
  git push
  ```

- [ ] Reconcile the root:

  ```sh
  flux reconcile kustomization flux-system --with-source
  ```

- [ ] Wait for the application Kustomization, OCI source, and Helm release:

  ```sh
  kubectl --namespace flux-system wait \
    --for=condition=ready \
    kustomization/sdfcc-dev-apps \
    --timeout=5m
  kubectl --namespace flux-system wait \
    --for=condition=ready \
    ocirepository/sdfcc-backend \
    --timeout=5m
  kubectl --namespace flux-system wait \
    --for=condition=ready \
    helmrelease/sdfcc-backend \
    --timeout=5m
  ```

- [ ] Verify the backend rollout:

  ```sh
  kubectl --namespace sdfcc-dev rollout status deployment/sdfcc-backend --timeout=5m
  kubectl --namespace sdfcc-dev get pods,services
  kubectl --namespace sdfcc-dev get deployment sdfcc-backend \
    -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
  ```

  The image must be the intended immutable SHA tag or digest, not `latest`.

- [ ] If deployment fails, inspect focused evidence:

  ```sh
  flux get sources all --all-namespaces
  flux get helmreleases --all-namespaces
  kubectl --namespace flux-system describe ocirepository sdfcc-backend
  kubectl --namespace flux-system describe helmrelease sdfcc-backend
  kubectl --namespace sdfcc-dev describe deployment sdfcc-backend
  kubectl --namespace sdfcc-dev get events --sort-by=.lastTimestamp
  kubectl --namespace sdfcc-dev logs deployment/sdfcc-backend --tail=200
  ```

### Gate 10

- [ ] Stop if the pod uses the wrong image, enters `ImagePullBackOff` or
      `CrashLoopBackOff`, or the HelmRelease does not become Ready.

## Phase 11: Local smoke test

- [ ] Start port-forwarding in a separate terminal. This command intentionally
      remains attached until interrupted:

  ```sh
  kubectl --namespace sdfcc-dev port-forward service/sdfcc-backend 8080:8080
  ```

- [ ] From another terminal, verify liveness and database-aware readiness:

  ```sh
  curl --fail --silent http://127.0.0.1:8080/actuator/health/liveness
  curl --fail --silent http://127.0.0.1:8080/actuator/health/readiness
  ```

- [ ] Confirm readiness reports `UP`. The readiness group includes the database,
      although Spring may hide individual component details in the HTTP response.

- [ ] Run at least one API operation that writes to PostgreSQL and one that reads
      the same data back.

- [ ] Inspect the backend logs for datasource, migration/schema, probe, or
      OpenTelemetry errors:

  ```sh
  kubectl --namespace sdfcc-dev logs deployment/sdfcc-backend --tail=200
  ```

- [ ] Remember that deployment success does not fix the documented dummy login
      token or incomplete bearer authentication flow.

## Phase 12: Final state verification

- [ ] Confirm all Flux resources are Ready:

  ```sh
  flux get all --all-namespaces
  ```

- [ ] Confirm no unexpected pending or unhealthy resources:

  ```sh
  kubectl get pods --all-namespaces
  kubectl get persistentvolumeclaims --all-namespaces
  kubectl get events --all-namespaces --sort-by=.lastTimestamp
  ```

- [ ] Confirm both local repositories are clean and pushed:

  ```sh
  git -C /Users/simonbreit/Projects/software-development-cloud-computing26 status --short
  git -C /Users/simonbreit/Projects/sdfcc-gitops status --short
  git -C /Users/simonbreit/Projects/sdfcc-gitops log -1 --oneline
  ```

- [ ] Confirm production is still suspended:

  ```sh
  rg -n 'suspend: true' \
    /Users/simonbreit/Projects/sdfcc-gitops/clusters/prod/apps/sdfcc-backend/source.yaml \
    /Users/simonbreit/Projects/sdfcc-gitops/clusters/prod/apps/sdfcc-backend/release.yaml
  ```

- [ ] Record the deployed GitOps revision, chart revision, and image:

  ```sh
  kubectl --namespace flux-system get kustomization flux-system \
    -o jsonpath='{.status.lastAppliedRevision}{"\n"}'
  kubectl --namespace flux-system get ocirepository sdfcc-backend \
    -o jsonpath='{.status.artifact.revision}{"\n"}'
  kubectl --namespace sdfcc-dev get deployment sdfcc-backend \
    -o jsonpath='{.spec.template.spec.containers[0].image}{"\n"}'
  ```

## Ongoing manual operations

### Promote a new application version

- [ ] Wait for one green application CI run.
- [ ] Verify its ARM64 image manifest.
- [ ] Update the dev OCI chart tag and matching image tag/digest together.
- [ ] Review, commit, and push the GitOps change.
- [ ] Let Flux reconcile; do not run direct Helm commands.
- [ ] Repeat readiness and database-backed smoke checks.

### Change an encrypted database Secret

- [ ] Edit the encrypted file with SOPS:

  ```sh
  sops clusters/dev/infrastructure/secrets/sdfcc-database.secret.sops.yaml
  ```

- [ ] Increment `secret.rolloutToken` in
      `clusters/dev/apps/sdfcc-backend/values.yaml` so the backend pods restart.
- [ ] Commit the encrypted Secret and rollout token in the same GitOps change.
- [ ] Verify PostgreSQL/application coordination before revoking old credentials.

### Roll back an application promotion

- [ ] Inspect the failed HelmRelease, pods, logs, and events.
- [ ] Revert the GitOps promotion commit rather than invoking Helm directly:

  ```sh
  git revert <gitops-promotion-commit>
  git push
  flux reconcile kustomization flux-system --with-source
  ```

- [ ] Treat rollback as schema-unsafe while Hibernate uses `ddl-auto=update`.
- [ ] Do not delete or recreate the PostgreSQL PVC as part of an application
      rollback.

### Stop local compute without deleting data

- [ ] Stop the Colima VM normally:

  ```sh
  colima stop
  ```

- [ ] Restart it later and verify Flux before using the application:

  ```sh
  colima start
  kubectl get nodes
  flux check
  flux get all --all-namespaces
  ```

## Deferred work not required for the first local deployment

- [ ] Real JWT/login and bearer-token enforcement.
- [ ] Flyway or Liquibase migrations replacing `ddl-auto=update`.
- [ ] Production database and backup/restore automation.
- [ ] Ingress controller, DNS, and TLS.
- [ ] Production secrets, policies, quotas, NetworkPolicies, PDB, and scaling.
- [ ] Artifact signing and policy enforcement.
- [ ] Kubernetes OpenTelemetry collection, alerting, and dashboards.
- [ ] Production promotion and rollback policy.

These items remain documented in `LOCAL_K8S_FLUX_DEPLOYMENT_REPORT.md` and
`tmp/DEPLOYMENT_OPERATIONS_AUDIT.md`.
