# Observability Stack

Reusable Docker Compose observability stack for Docker-based applications. It provides Grafana, Prometheus, Loki, Tempo, and Grafana Alloy as a generic skeleton that can be attached to different projects.

The stack supports two intentional integration modes:

- Mode A: app-native OpenTelemetry. Preferred for applications you control.
- Mode B: Docker fallback. Useful for third-party services or applications that do not emit OpenTelemetry.

Use one mode per application by default. Sending OTLP logs and also collecting Docker stdout/stderr for the same app can duplicate logs. Sending OTLP metrics and also scraping the same app's `/metrics` endpoint can duplicate metrics.

## Architecture

```text
Mode A: app-native OpenTelemetry

app OTLP gRPC/HTTP
  -> Alloy :4317/:4318
    -> metrics -> Prometheus remote_write
    -> logs    -> Loki push API
    -> traces  -> Tempo OTLP gRPC

Mode B: Docker fallback

Docker stdout/stderr logs
  -> Alloy Docker discovery
    -> Loki push API

Docker /metrics endpoints
  -> Alloy Docker discovery
    -> Prometheus remote_write

Grafana
  -> Prometheus, Loki, Tempo
```

Alloy is the central ingest point. Prometheus, Loki, and Tempo stay internal to the Docker network. Grafana is bound to `127.0.0.1:3000` by default.

## Components

- `compose.yaml`: base stack.
- `compose.npm-proxy.yaml`: optional override that attaches Grafana to an external Nginx Proxy Manager network.
- `alloy/config.alloy`: OTLP ingest, Docker discovery, scraping, and forwarding.
- `prometheus/prometheus.yml`: Prometheus configuration. Prometheus receives remote_write from Alloy.
- `loki/config.yml`: single-node Loki for logs from Alloy.
- `tempo/tempo.yml`: single-node Tempo with internal OTLP receivers for traces from Alloy.
- `provisioning/datasources/datasources.yml`: Grafana datasources for Prometheus, Loki, and Tempo.

## Quick Start

Create your environment file:

```bash
cp .env.example .env
```

Review at least these values:

```dotenv
GRAFANA_ADMIN_USER=admin
GRAFANA_ADMIN_PASSWORD=change-me
GRAFANA_HTTP_BIND=127.0.0.1:3000
ALLOY_UI_BIND=127.0.0.1:12345
ALLOY_OTLP_GRPC_BIND=4317
ALLOY_OTLP_HTTP_BIND=4318
OBSERVABILITY_NETWORK=observability_observability
```

Start the stack:

```bash
docker compose up -d
```

Check status:

```bash
docker compose ps
docker logs alloy
docker logs prometheus
```

Open:

- Grafana: `http://localhost:3000`
- Alloy UI: `http://localhost:12345`

Prometheus, Loki, and Tempo are not published on host ports by default.

## Optional Nginx Proxy Manager Network

The base stack does not require an external proxy network. If Grafana should be reachable from Nginx Proxy Manager by container name, create or reuse the proxy network and start with the override:

```bash
docker network create npm_proxy
docker compose -f compose.yaml -f compose.npm-proxy.yaml up -d
```

Set this when your NPM network has a different name:

```dotenv
NPM_PROXY_NETWORK=npm_proxy
GRAFANA_DOMAIN=grafana.example.com
GRAFANA_ROOT_URL=https://grafana.example.com/
```

In NPM, proxy to:

```text
Forward hostname: grafana
Forward port: 3000
Scheme: http
```

## Mode A: OTLP-First Apps

Prefer this for applications you own. The app exports OpenTelemetry directly to Alloy:

- OTLP metrics -> Alloy -> Prometheus
- OTLP logs -> Alloy -> Loki
- OTLP traces -> Alloy -> Tempo

Example app Compose snippet:

```yaml
services:
  app:
    image: example/app:latest
    environment:
      OTEL_SERVICE_NAME: app
      OTEL_RESOURCE_ATTRIBUTES: service.namespace=example,deployment.environment=local
      OTEL_EXPORTER_OTLP_ENDPOINT: http://alloy:4317
      OTEL_EXPORTER_OTLP_PROTOCOL: grpc
    networks:
      - observability

networks:
  observability:
    external: true
    name: observability_observability
```

For OTLP/HTTP exporters:

```dotenv
OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4318
OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
```

Only add Docker fallback labels to the same app when you intentionally want duplicate or supplemental collection.

## Mode B: Docker Fallback

Use this for services that do not emit OpenTelemetry. Alloy discovers containers through the Docker socket and only collects data from containers that opt in with labels.

Docker labels containing dots are exposed by Docker discovery with underscores. For example:

```text
observability.logs -> __meta_docker_container_label_observability_logs
observability.metrics_port -> __meta_docker_container_label_observability_metrics_port
```

### Docker Logs Fallback

Collect stdout/stderr from one container:

```yaml
services:
  app:
    image: example/app:latest
    labels:
      observability.logs: "true"
    networks:
      - observability

networks:
  observability:
    external: true
    name: observability_observability
```

Logs flow as:

```text
Docker stdout/stderr -> Alloy loki.source.docker -> Loki -> Grafana
```

### Metrics Scraping Fallback

Scrape a container's internal `/metrics` endpoint:

```yaml
services:
  app:
    image: example/app:latest
    expose:
      - "8080"
    labels:
      observability.metrics: "true"
      observability.metrics_port: "8080"
      observability.metrics_path: "/metrics"
    networks:
      - observability

networks:
  observability:
    external: true
    name: observability_observability
```

Important details:

- `observability.metrics: "true"` opts the container into scraping.
- `observability.metrics_port` must be the internal container port, not the host-published port.
- `observability.metrics_path` defaults to Prometheus' normal `/metrics` behavior when omitted, but setting it is clearer.
- The app and Alloy must share a Docker network.

Metrics flow as:

```text
container internal IP:metrics_port/metrics_path -> Alloy prometheus.scrape -> Prometheus remote_write -> Grafana
```

## OTLP Connectivity Tests

From an app container on the shared Docker network:

```bash
docker exec <app> getent hosts alloy
docker exec <app> nc -vz alloy 4317
docker exec <app> nc -vz alloy 4318
```

From the Docker host:

```bash
nc -vz 127.0.0.1 4317
nc -vz 127.0.0.1 4318
```

If `ALLOY_OTLP_GRPC_BIND=4317` and `ALLOY_OTLP_HTTP_BIND=4318`, Alloy listens on all host interfaces for those ports. Use `127.0.0.1:4317` and `127.0.0.1:4318` in `.env` for host-local ingest only.

## Verify In Grafana

Open Grafana Explore at `http://localhost:3000/explore`.

PromQL examples:

```promql
up
{job="docker-services"}
{service_name="app"}
```

LogQL examples:

```logql
{job="docker-logs"}
{service_name="app"}
{compose_project="my-project"}
```

Tempo:

- Select the Tempo datasource in Explore.
- Search by service name, such as `app`.
- Recent traces should appear after the application emits spans.

## Debug Commands

Stack status and logs:

```bash
docker compose ps
docker logs alloy
docker logs prometheus
docker logs loki
docker logs tempo
docker logs grafana
```

Inspect app labels:

```bash
docker inspect <container> --format '{{json .Config.Labels}}' | jq
```

Inspect app networks:

```bash
docker inspect <container> --format '{{json .NetworkSettings.Networks}}' | jq
```

Check the app can resolve and reach Alloy:

```bash
docker exec <app> getent hosts alloy
docker exec <app> nc -vz alloy 4317
docker exec <app> nc -vz alloy 4318
```

Check Prometheus was started with remote_write receiving enabled:

```bash
docker inspect prometheus --format '{{json .Args}}' | jq
```

Look for:

```text
--web.enable-remote-write-receiver
```

## Troubleshooting

### App Cannot Resolve `alloy`

The app is probably not attached to the same Docker network as Alloy.

Check:

```bash
docker inspect <app> --format '{{json .NetworkSettings.Networks}}' | jq
docker network inspect observability_observability
```

Fix the app Compose file:

```yaml
networks:
  observability:
    external: true
    name: observability_observability
```

### No Logs In Loki

For Mode A, confirm the app really exports OTLP logs. Many OpenTelemetry SDK setups enable traces first and do not automatically export logs.

For Mode B, confirm the label exists:

```bash
docker inspect <container> --format '{{json .Config.Labels}}' | jq
```

Required label:

```yaml
observability.logs: "true"
```

Also check Alloy and Loki:

```bash
docker logs alloy
docker logs loki
```

### No Metrics In Prometheus

For Mode A, confirm the app exports OTLP metrics to Alloy.

For Mode B, confirm all labels:

```yaml
observability.metrics: "true"
observability.metrics_port: "8080"
observability.metrics_path: "/metrics"
```

The metrics port must be the internal container port. If the app is published as `9090:8080`, use `8080`, not `9090`.

Check Prometheus remote_write receiver:

```bash
docker logs prometheus
docker inspect prometheus --format '{{json .Args}}' | jq
```

The Compose command includes `--web.enable-remote-write-receiver`. Without it, Alloy cannot write metrics to Prometheus.

### No Traces In Tempo

Confirm the app sends traces to Alloy, not directly to Tempo:

```dotenv
OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4317
OTEL_EXPORTER_OTLP_PROTOCOL=grpc
```

Then check:

```bash
docker logs alloy
docker logs tempo
docker exec <app> nc -vz alloy 4317
```

Alloy exports traces internally to `tempo:4317` using OTLP gRPC.

### Labels Missing

Compose labels must be under the service:

```yaml
services:
  app:
    labels:
      observability.logs: "true"
```

Verify Docker sees them:

```bash
docker inspect <container> --format '{{json .Config.Labels}}' | jq
```

### Wrong Metrics Port

Alloy deliberately matches `observability.metrics_port` against Docker's internal port metadata, `__meta_docker_port_private`. Host-published ports are not used.

Use the port the process listens on inside the container:

```yaml
labels:
  observability.metrics: "true"
  observability.metrics_port: "8080"
```

### Docker Socket Not Mounted

Mode B needs the Docker socket mounted into Alloy:

```yaml
volumes:
  - /var/run/docker.sock:/var/run/docker.sock:ro
```

This is present in `compose.yaml`. If Docker discovery stops working, check the mount:

```bash
docker inspect alloy --format '{{json .Mounts}}' | jq
docker logs alloy
```

### App And Alloy Not In Same Network

Mode A with `http://alloy:4317` and Mode B metrics scraping both require a shared Docker network.

Check both containers:

```bash
docker inspect alloy --format '{{json .NetworkSettings.Networks}}' | jq
docker inspect <app> --format '{{json .NetworkSettings.Networks}}' | jq
```

Attach the app to the configured external network:

```yaml
networks:
  observability:
    external: true
    name: observability_observability
```

## Configuration Notes

- Alloy listens for OTLP gRPC on `0.0.0.0:4317` inside the container.
- Alloy listens for OTLP HTTP on `0.0.0.0:4318` inside the container.
- `ALLOY_OTLP_GRPC_BIND` and `ALLOY_OTLP_HTTP_BIND` control host port publishing.
- Prometheus is internal and receives metrics through `/api/v1/write`.
- Loki is internal and receives logs through `/loki/api/v1/push`.
- Tempo is internal and receives traces from Alloy on `tempo:4317`.
- Grafana datasources are provisioned automatically.
