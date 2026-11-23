# Install and run WatchWolfX

The supported deployment path is Docker Compose. Compose starts the long-lived ServerManager and
ClientsManager services; ServerManager creates Minecraft containers only when a test requests one.

## Prerequisites

- Docker Engine on Linux, or Docker Desktop using Linux containers on Windows
- Docker Compose v2
- Permission to access the Docker daemon

!!! warning "Docker socket access"

    ServerManager mounts `/var/run/docker.sock` so it can create and remove sibling Minecraft
    containers. This grants control over the Docker host. Do not expose ServerManager to untrusted
    networks, and never mount the socket into a Minecraft container.

## Start the control plane

Create an empty directory and download the deployment manifest. You do not need the WatchWolfX
source code or a JDK to run the published control plane.

=== "Linux and macOS"

    ```bash
    mkdir watchwolfx && cd watchwolfx
    curl --fail --location --output compose.yaml \
      https://raw.githubusercontent.com/sarhatabaot/WatchWolfX/main/compose.yaml
    docker compose pull
    docker compose up --no-build --wait -d
    ```

=== "Windows PowerShell"

    ```powershell
    New-Item -ItemType Directory -Path watchwolfx
    Set-Location watchwolfx
    Invoke-WebRequest `
      https://raw.githubusercontent.com/sarhatabaot/WatchWolfX/main/compose.yaml `
      -OutFile compose.yaml
    docker compose pull
    docker compose up --no-build --wait -d
    ```

The manifest uses the stable `latest` ServerManager and ClientsManager images from GitHub
Container Registry. `--no-build` guarantees that this installation path never compiles the
project locally.

Check that both services are healthy:

```bash
docker compose ps
```

| Service | Host port | Responsibility |
| --- | ---: | --- |
| ServerManager | `8000` | Provisions and owns ephemeral Minecraft servers |
| ClientsManager | `7000` | Creates and controls headless Minecraft clients |

## Optional configuration

The defaults use loopback addresses, which are appropriate when tests and Docker run on the same
machine. Create a `.env` beside `compose.yaml` only when you need an override. For a remote test
runner, set addresses that the runner and spawned containers can reach:

```dotenv
WATCHWOLF_MACHINE_IP=192.0.2.10
WATCHWOLF_PUBLIC_IP=192.0.2.10
```

See [Configuration](configuration.md) before changing networking or runtime settings.

## Optional: run the example plugin

The example source uploads a sample Bukkit plugin, requests Paper `1.20.6`, and executes a JUnit
test against the live server. This optional step requires Git and a JDK supported by Gradle 9:

```bash
git clone --depth 1 https://github.com/sarhatabaot/WatchWolfX.git
cd WatchWolfX
cd examples/hello-plugin
docker compose up --wait -d
./gradlew test
docker compose down
```

On Windows, run `gradlew.bat test`. The first test may take several minutes while Docker,
dependencies, and Paper are downloaded.

!!! tip "Pin reproducible runs"

    The control plane follows the latest stable images by default. Set
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
