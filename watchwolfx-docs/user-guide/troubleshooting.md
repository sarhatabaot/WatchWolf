# Troubleshooting

Start with the control-plane state, then inspect the dynamically created Minecraft container.

## Compose does not become healthy

```bash
docker compose ps
docker compose logs servers-manager
docker compose logs clients-manager
```

Check that ports `7000` and `8000` are unused and that the Docker daemon is reachable.

## ServerManager cannot access Docker

ServerManager requires `/var/run/docker.sock`. Confirm that the socket is mounted and that the
container user can access it:

```bash
docker compose config
docker compose exec servers-manager ls -l /var/run/docker.sock
```

Treat socket access as host-level privilege; do not work around permissions by mounting the socket
into Minecraft containers.

## Minecraft container runs but tests time out

`running` is not the readiness signal. Inspect Minecraft logs for Paper startup, plugin loading,
and the WatchWolfX Server socket:

```bash
docker ps --filter label=watchwolf.managed=true
docker logs <minecraft-container>
```

Common causes include:

- first-start Paper download still in progress;
- unsupported or unavailable Minecraft version;
- WatchWolfX Server plugin failed to load;
- advertised machine/public IP is unreachable;
- allocated ports are blocked or already occupied.

## The example cannot download Tester

The example resolves Tester from a GitHub Release. Ensure the selected version exists:

```dotenv
WATCHWOLFX_RELEASE_REPOSITORY=sarhatabaot/WatchWolfX
WATCHWOLFX_VERSION=0.5.2
```

If `0.5.2` has not been published yet, select the latest available release explicitly.

## Windows Docker Desktop path failures

Use Linux containers and avoid passing Windows paths into nested Docker mounts. ServerManager asks
the Linux Docker daemon to create sibling containers, so a path valid to the Compose client is not
automatically valid inside that operation.

## Cleanup leaves an instance behind

List WatchWolfX-managed containers and inspect their labels before removing anything:

```bash
docker ps -a --filter label=watchwolf.managed=true
docker inspect <container>
```

Keep the container logs when diagnosing failed startup. Cleanup is designed to be idempotent, so a
subsequent ServerManager recovery pass should tolerate already-stopped or already-removed state.

## Documentation build fails

Use the pinned generator and a clean strict build:

```bash
python -m pip install --requirement watchwolfx-docs/pages-requirements.txt
zensical build --clean --strict
```

Strict mode reports broken internal links and navigation mistakes before Pages deployment.
