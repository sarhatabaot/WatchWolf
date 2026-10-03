# WatchWolf example plugin

This project contains a small Bukkit plugin and a JUnit test that runs it on a real Paper server.
The plugin exposes `/samplecounter`; the test runs the command through WatchWolf and checks that
its state changes on the server.

## Run from an IDE

Keep this project beside the `WatchWolf-Server` and `WatchWolf-ServersManager` checkouts in the
workspace layout used here. Docker Desktop must use Linux containers on Windows; Linux Docker
Engine is the other intended deployment path. A JDK supported by Gradle 9 is needed for the
IDE project. Docker needs internet access on the first build to fetch build dependencies, the
Clients Manager source, and Paper.

1. From this directory, run `docker compose up --build --wait -d`. Compose builds the local
   WatchWolf-Server plugin and ServersManager from their sibling checkouts, builds the upstream
   Clients Manager, and waits for both control-plane ports. It uses the itzg provider; no
   prebuilt Minecraft server JAR is needed.
2. Open this directory as a Gradle project in the IDE. In IntelliJ IDEA, run **Gradle > Tasks >
   verification > test**, or enable **Run tests using: Gradle** and run
   `CounterPluginWatchWolfTest` from the gutter. The `test` task builds the example plugin JAR
   before starting the test.
3. When finished, run `docker compose down` from this directory.

The same test can be launched in a terminal with `./gradlew test` on Linux or
`gradlew.bat test` on Windows. It reads `src/test/resources/watchwolf.yaml`, requests Paper
1.20.6, uploads `build/libs/watchwolf-example-plugin.jar` to the ephemeral instance, checks
the command response, and asks WatchWolf to stop the instance afterward. The test has no
automated player, but the Tester still connects to Clients Manager on port 7000. The control
plane publishes ports 7000 and 8000; ServersManager publishes the requested Minecraft and
WatchWolf socket pair starting at 8001.

If you change WatchWolf-Server or ServersManager, rerun `docker compose up --build --wait -d` to
rebuild their image. If you change this example plugin, rerun the Gradle test; Compose does not
need rebuilding. The first run may take several minutes while dependencies and Paper download.

The end-to-end run was verified on Windows Docker Desktop with Linux containers. Linux Docker
Engine has not been run for this example in this workspace.

Once the GHCR images are published, the source checkouts are unnecessary for running tests.
From the `WatchWolf` repository root, copy `release.env.example` to `release.env`, set the
published tag, and run `docker compose --env-file release.env -f compose.release.yaml up --wait -d`.
Then run this example's Gradle `test` task from the IDE as above. Stop the published control
plane with `docker compose --env-file release.env -f compose.release.yaml down`.
