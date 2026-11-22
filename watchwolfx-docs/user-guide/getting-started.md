# Install and run WatchWolfX

The supported deployment path is Docker Compose. Compose starts the long-lived ServerManager and
ClientsManager services; ServerManager creates Minecraft containers only when a test requests one.

## Prerequisites

- Docker Engine on Linux, or Docker Desktop using Linux containers on Windows
- Docker Compose v2
- Permission to access the Docker daemon
- A JDK supported by Gradle 9 when running the Java example locally

!!! warning "Docker socket access"

    ServerManager mounts `/var/run/docker.sock` so it can create and remove sibling Minecraft
    containers. This grants control over the Docker host. Do not expose ServerManager to untrusted
    networks, and never mount the socket into a Minecraft container.

## Start the control plane

```bash
git clone https://github.com/sarhatabaot/WatchWolfX.git
cd WatchWolfX
cp .env.example .env
docker compose up --build --wait -d
```

The first build compiles Core, ServerManager, and the WatchWolfX Server plugin. A healthy stack has
two services:

```bash
docker compose ps
```

| Service | Host port | Responsibility |
| --- | ---: | --- |
| ServerManager | `8000` | Provisions and owns ephemeral Minecraft servers |
| ClientsManager | `7000` | Creates and controls headless Minecraft clients |

## Set reachable addresses

The default `.env.example` uses loopback addresses, which is appropriate when tests and Docker run
on the same machine. For a remote test runner, set addresses that the runner and spawned containers
can reach:

```dotenv
WATCHWOLF_MACHINE_IP=192.0.2.10
WATCHWOLF_PUBLIC_IP=192.0.2.10
```

See [Configuration](configuration.md) before changing networking or runtime settings.

## Run the example plugin

The included example starts the published control plane, uploads a sample Bukkit plugin, requests
Paper `1.20.6`, and executes a JUnit test against the live server.

```bash
cd examples/hello-plugin
docker compose up --wait -d
./gradlew test
docker compose down
```

On Windows, run `gradlew.bat test`. The first test may take several minutes while Docker,
dependencies, and Paper are downloaded.

!!! tip "Pin reproducible runs"

    The example follows the latest stable control-plane images by default. Set
    `WATCHWOLFX_SERVERMANAGER_IMAGE`, `WATCHWOLFX_CLIENTSMANAGER_IMAGE`, and `WATCHWOLFX_VERSION`
    to matching `v<version>` values when reproducibility matters.

## Stop WatchWolfX

```bash
docker compose down
```

This removes the control-plane containers and network. The named log volume remains available for
diagnostics. Minecraft instance cleanup remains ServerManager's responsibility.

## Next steps

- [Configure runtime, images, and addresses](configuration.md)
- [Understand the request and cleanup flow](../concepts/architecture.md)
- [Diagnose startup and connectivity failures](troubleshooting.md)
