# WatchWolfX ServerManager

ServerManager listens on TCP 8000 and provisions ephemeral Minecraft servers for WatchWolfX
tests. It selects the configured runtime provider, injects the WatchWolfX Server plugin, reports
readiness, retains diagnostics and cleans up containers and storage.

## Deployment

Docker Compose is the supported installer and runtime entry point. From the monorepo root:

```sh
cp .env.example .env
docker compose up --build --wait -d
```

The Compose build compiles ServerManager and the matching local Server plugin, then packages both
in a Wolfi/OpenJDK image. The manager receives `/var/run/docker.sock` because it creates sibling
Minecraft containers dynamically. Minecraft containers never receive the Docker socket.

The image contains `/servers/usual-plugins/WatchWolf-*.jar`; no prebuilt release directory or
bootstrap script is required. Retained logs live in the `watchwolf_logs` named volume.

## Runtime providers

`WATCHWOLF_MINECRAFT_RUNTIME` selects `itzg` or `legacy`; Compose defaults to `itzg`.

The itzg provider currently accepts Paper and maps the requested version to the configured
`itzg/minecraft-server` image and Java variant. Each instance receives an isolated named volume at
`/data`. ServerManager copies configuration and plugins through the Docker API and removes the
volume when the instance stops.

The legacy provider remains for compatibility and requires matching JARs under
`server-types/<type>/<version>.jar`. Its host-path requirements are not part of the default
Compose workflow.

Relevant configuration:

| Variable | Meaning |
| --- | --- |
| `WATCHWOLF_MINECRAFT_RUNTIME` | `itzg` or `legacy` |
| `WATCHWOLF_ITZG_IMAGE` | itzg repository; defaults to `itzg/minecraft-server` |
| `WATCHWOLF_ITZG_TAG` | image tag or `auto` Java selection |
| `MACHINE_IP` | reachable host address returned to local testers |
| `PUBLIC_IP` | fallback public address |

The image tag and requested Minecraft version are independent version dimensions.

## Development and tests

The normal Compose build is self-contained. Module-focused checks remain under `ci/debug`:

```sh
./ci/debug/tests.sh --unit
./ci/debug/tests.sh --integration
./ci/debug/tests.sh --unit --tests 'ServerRequirementsShould'
./ci/debug/validator.sh
```

Integration tests start real Docker workloads and require compatible server/plugin fixtures.
Readiness is based on Minecraft startup output and the existing WatchWolfX protocol, not merely a
Docker `running` state.

## Lifecycle contract

- ServerManager listens on 8000.
- Each Minecraft instance receives a consecutive host port pair starting at 8001.
- The first port maps Minecraft; the second maps the WatchWolfX Server plugin socket.
- Temporary instance data is removed after shutdown; retained logs remain available.
- itzg containers and volumes carry WatchWolf management labels for orphan recovery.

See `watchwolfx-docs/engineering/runtime-inventory.md` for the full discovered call chain and
cross-platform notes.
