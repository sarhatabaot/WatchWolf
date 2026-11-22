# Configuration

WatchWolfX reads deployment settings through Compose environment variables. Defaults favor a local
Docker host and the dynamic itzg Paper runtime.

## Control-plane settings

| Variable | Default | Purpose |
| --- | --- | --- |
| `WATCHWOLFX_SERVERMANAGER_IMAGE` | `watchwolfx-servermanager:dev` | Override the locally built ServerManager image |
| `WATCHWOLFX_CLIENTSMANAGER_IMAGE` | `watchwolfx-clientsmanager:dev` | Override the locally built ClientsManager image |
| `WATCHWOLF_MACHINE_IP` | `127.0.0.1` | Address advertised for machine-local services |
| `WATCHWOLF_PUBLIC_IP` | `127.0.0.1` | Address returned to external test runners |

Published stable images are available from GitHub Container Registry:

```dotenv
WATCHWOLFX_SERVERMANAGER_IMAGE=ghcr.io/sarhatabaot/watchwolfx-servermanager:latest
WATCHWOLFX_CLIENTSMANAGER_IMAGE=ghcr.io/sarhatabaot/watchwolfx-clientsmanager:latest
```

Use `v0.5.2` instead of `latest` to pin a release.

## Minecraft runtime settings

| Variable | Default | Allowed values or format |
| --- | --- | --- |
| `WATCHWOLF_MINECRAFT_RUNTIME` | `itzg` | `itzg` or `legacy` |
| `WATCHWOLF_ITZG_IMAGE` | `itzg/minecraft-server` | Docker image repository |
| `WATCHWOLF_ITZG_TAG` | `auto` | Runtime image tag |

The runtime image version and requested Minecraft version are independent. A test requests a
Minecraft version such as `1.20.6`; ServerManager passes it to the configured runtime image.

```text
Tester request: Paper 1.20.6
Runtime image:  itzg/minecraft-server:auto
Container env:  TYPE=PAPER, VERSION=1.20.6
```

See [Minecraft runtime providers](../concepts/runtime-providers.md) for lifecycle differences and the legacy
fallback.

## Ports

| Port range | Owner | Use |
| --- | --- | --- |
| `8000` | ServerManager | Tester control connection |
| `8001+` | Minecraft instances | Allocated Minecraft and WatchWolfX Server socket pairs |
| `7000` | ClientsManager | Tester control connection |
| `7001-7199` | Headless clients | Client control and recording streams |

Preserve the current port-pair behavior unless both Tester and ServerManager are changed together.

## Storage

- `watchwolf_logs` stores ServerManager logs outside the container lifecycle.
- Every itzg Minecraft instance receives isolated `/data` storage.
- Ephemeral instance data follows ServerManager's cleanup policy.
- Generated worlds, logs, runtime JARs, and server data must not be committed.

## Configuration precedence

Compose resolves values in this order:

1. variables supplied by the shell or `docker compose --env-file`;
2. values in the repository `.env` file;
3. defaults declared in `compose.yaml`.

Inspect the fully resolved configuration without starting services:

```bash
docker compose config
```
