# AGENTS.md — WatchWolfX ServerManager

ServerManager is the Java 17 control-plane service on TCP 8000. It provisions ephemeral Minecraft
containers, injects WatchWolfX Server, observes readiness and owns cleanup.

## Structure

- `src/main/java/dev/watchwolf/serversmanager/server/runtime/`: provider selection and mapping.
- `src/main/java/dev/watchwolf/serversmanager/server/instantiator/`: Docker lifecycle.
- `src/main/java/dev/watchwolf/serversmanager/rpc/`: Tester-facing protocol implementation.
- `ci/ghcr/Dockerfile`: monorepo-context Compose and release image.
- `ci/debug/`: module-focused build and test helpers.
- `src/tools/`: legacy Spigot/Paper compatibility builders.

## Deployment rule

The repository-root `compose.yaml` is the only supported installer/runtime workflow. Do not add a
second release Compose file or revive the destructive setup script. The Compose image must build
from a fresh monorepo checkout without prebuilt `ci/release` artifacts.

ServerManager retains Docker socket access because it launches sibling Minecraft workloads.
Never pass the Docker socket to a Minecraft container.

## Runtime safety

- Preserve the external Tester/Server protocol and existing readiness contract.
- Keep legacy and itzg providers behind `MinecraftRuntimeProvider`.
- Keep `/data` isolated per itzg instance and cleanup idempotent.
- Preserve Docker labels and orphan recovery.
- Do not treat Docker `running` as Minecraft readiness.
- Do not hard-code host-specific paths.

## Checks

```sh
./ci/debug/tests.sh --unit
./ci/debug/validator.sh
```

Integration tests start real containers and are not hermetic. Report unit, Compose health and real
Minecraft integration results separately.

The normal deployment check runs from the monorepo root:

```sh
docker compose up --build --wait -d
```

## Git

Keep changes scoped to ServerManager responsibility, preserve upstream attribution, and do not
commit generated JARs, worlds, logs, server data or credentials.
