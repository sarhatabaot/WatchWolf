# WatchWolf example plugin

This Gradle project contains a small Bukkit plugin and a JUnit test that runs it on a real
Paper server. The plugin exposes `/samplecounter`; the test checks that command state is kept
on the server.

## Run from an IDE

You need Docker Engine with Linux containers, a JDK supported by Gradle 9, and network access
for the first dependency and Paper downloads. The Compose file uses the published WatchWolf
`v0.4.1` ServersManager and Clients Manager images. The test resolves the WatchWolf-Tester
`v0.4.1` JAR from its GitHub Release. No sibling WatchWolf source checkouts are needed.

1. From this directory, run `docker compose up --wait -d`. Compose starts the control plane
   and waits for ports 7000 and 8000. ServersManager uses the itzg Paper runtime and creates
   each Minecraft container when a test requests it.
2. Open this directory as a Gradle project in the IDE. In IntelliJ IDEA, run **Gradle > Tasks >
   verification > test**, or enable **Run tests using: Gradle** and run
   `CounterPluginWatchWolfTest` from the gutter. The Gradle `test` task builds the example
   plugin JAR first.
3. When finished, run `docker compose down` from this directory.

You can also run `./gradlew test` on Linux or `gradlew.bat test` on Windows. The test reads
`src/test/resources/watchwolf.yaml`, requests Paper 1.20.6, uploads
`build/libs/watchwolf-example-plugin.jar` to the instance, checks the command output, and
stops the instance. ServersManager publishes the requested Minecraft and WatchWolf socket
pair starting at port 8001. Compose publishes ports 7000–7199 for Clients Manager.

When you change this example plugin, rerun the Gradle test; Compose does not need rebuilding.
The first run may take several minutes while dependencies and Paper download. Compose stores
manager logs in a named volume and gives only ServersManager access to the Docker socket.

The `v0.4.1` Compose and Gradle test flow passed on Windows Docker Desktop with Linux
containers. Linux Docker Engine has not been run for this example in this workspace.
