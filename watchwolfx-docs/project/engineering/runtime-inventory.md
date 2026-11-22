# Minecraft runtime inventory

Status: current workspace inspection on 2026-11-21. This note describes code that exists in the
five source trees; it does not infer behavior from repository names. Paths are relative to the
WatchWolfX repository root.

## Current flow

```text
JUnit test / AbstractTest
  -> Tester.run()
  -> TesterConnector.startServer() over TCP :8000
  -> generated ServersManagerLocalStub
  -> ServersManagerLocalImplementation.startServer()
  -> server.ServersManager.startServer()
  -> selected MinecraftRuntimeProvider
  -> Docker container + Minecraft stdout
  -> "Done (...)! For help, type \"help\"" readiness match
  -> ServerStartedEvent over the ServersManager RPC connection
  -> Tester opens the WatchWolf-Server socket on Minecraft port + 1
  -> test petitions execute
  -> Tester.close() sends WatchWolf-Server's stop petition
  -> Minecraft `stop`, container exit, observer cleanup
```

### 1. Creation files, classes, and functions

- `watchwolfx-tester/src/main/java/dev/watchwolf/tester/AbstractTest.java`
  `beforeAll()` reads the requested type/version matrix, opens ServersManager (`8000`) and
  ClientsManager (`7000`) sockets, constructs `Tester`, and waits for readiness callbacks.
- `watchwolfx-tester/src/main/java/dev/watchwolf/tester/Tester.java`: `run()` sends the start
  request; `onServerStart()` connects to the returned host at `<minecraft-port> + 1`, configures
  the server, and starts clients.
- `watchwolfx-tester/src/main/java/dev/watchwolf/tester/TesterConnector.java`:
  `startServer()` encodes operation 1 for DST `0b000` and reads the returned `IP:port`.
- `watchwolfx-core/src/main/java/dev/watchwolf/core/rpc/stubs/serversmanager/ServersManagerLocalStub.java`:
  generated RPC decoder dispatches operation 1 to `ServersManagerPetitions.startServer()`.
- `watchwolfx-servermanager/src/main/java/dev/watchwolf/serversmanager/rpc/ServersManagerLocalImplementation.java`:
  `startServer()` captures the requestee address, starts provisioning, and wires readiness/error
  events back to the RPC stub.
- `watchwolfx-servermanager/src/main/java/dev/watchwolf/serversmanager/server/ServersManager.java`:
  `startServer()` delegates to a provider, externalizes the returned address, retains logs, and
  subscribes scratch-folder cleanup.
- `watchwolfx-servermanager/src/main/java/dev/watchwolf/serversmanager/server/runtime/`:
  `MinecraftRuntimeProvider`, `LegacyRuntimeProvider`, `ItzgRuntimeProvider`, and
  `MinecraftRuntimeProviders` are the current provider boundary and selector.
- `watchwolfx-servermanager/src/main/java/dev/watchwolf/serversmanager/server/ServerRequirements.java`:
  `setupFolderInternal()` creates EULA, Bukkit/server properties, worlds, plugins, and plugin
  configuration. `setupFolder()` adds a legacy server JAR; `setupFolderWithoutJar()` does not.
- `watchwolfx-servermanager/src/main/java/dev/watchwolf/serversmanager/server/instantiator/`:
  `DockerizedServerInstantiator.startServer()` launches the legacy image and
  `ItzgDockerServerInstantiator.startServer()` launches the itzg image.

### 2. Exact Docker SDK calls

Both paths use `docker-java` (`DockerClientBuilder`), not the Docker CLI.

Legacy `DockerizedServerInstantiator.startServer()` calls:

- `createContainerCmd("eclipse-temurin:<java>-jdk")`;
- container name `MC_Server-<instance-id>`;
- `HostConfig.withPortBindings(<N>:25565/tcp, <N>:25565/udp, <N+1>:25566)`;
- `HostConfig.withAutoRemove(true)` and bind `<prepared-host-path>:/server`;
- exposed ports `25565/tcp`, `25565/udp`, `25566/tcp`;
- working directory `/server`, entrypoint `/bin/sh -c`, command
  `java -XX:MaxRAMFraction=1 -jar server.jar nogui`;
- `startContainerCmd`, `logContainerCmd(...).withFollowStream(true)`, and `killContainerCmd`.

Itzg `ItzgDockerServerInstantiator.startServer()` calls:

- `inspectImageCmd`, and if absent `pullImageCmd(...).awaitCompletion()`;
- `createVolumeCmd()` for `watchwolf-itzg-<instance-id>`;
- a temporary helper container with the volume at `/data`, followed by
  `copyArchiveToContainerCmd()` for each prepared entry and `execCreateCmd("chown", "-R",
  "1000:1000", "/data")`;
- `createContainerCmd(<configured image:tag>)`, the same three port bindings, named-volume bind
  `<volume>:/data`, `withAutoRemove(true)`, itzg environment, and WatchWolf labels;
- `startContainerCmd`, followed logs, and forced removal for failed starts/orphan recovery.

No Minecraft container receives `/var/run/docker.sock`; only ServersManager does.

### 3. Server type and version model

- Core declares `ServerType { Spigot, Paper }`, but the generated RPC and current Tester transmit
  `serverType` and `serverVersion` as strings. Tester YAML therefore supplies a map from arbitrary
  type names to string versions.
- Legacy resolves `server-types/<type>/<version>.jar`; this permits custom type directory names.
- Itzg currently accepts exactly case-sensitive `Paper` plus a numeric `x.y` or `x.y.z` version,
  then maps it to `TYPE=PAPER` and `VERSION=<requested>`.
- Current focused itzg tests cover Paper `1.19`, `1.20.2`, `1.20.6`, and `1.21.1`. This is test
  coverage, not a declaration that every numeric Paper release is supported.

### 4. Java runtime selection

- Legacy calls `DockerUtilities.getJavaVersion()`: Java 8 before 1.17, Java 16 for 1.17,
  Java 17 for 1.18 through 1.20.4, and Java 21 for 1.20.5+.
- Itzg separates Minecraft version from image tag. `WATCHWOLF_ITZG_TAG=auto` currently maps only
  `1.20.6` to `java21` and every other accepted version to `java17`; explicit tags bypass that
  mapping. The `1.21.1` integration test therefore passes `java21` explicitly.

### 5. Server artifact paths

- Legacy source JAR: `${SERVER_PATH_SHIFT:-.}/server-types/<type>/<version>.jar`.
- Usual plugins: `${SERVER_PATH_SHIFT:-.}/usual-plugins/`.
- Per-instance preparation: `${PARENT_PWD}/{SERVER_PATH_SHIFT}/tmp/<millis>` as seen by nested
  legacy Docker, and local `${SERVER_PATH_SHIFT}/tmp/<millis>` for itzg seeding.
- Retained diagnostics: `${SERVER_PATH_SHIFT}/logs/<instance-id>/{info.txt,latest.log}`.
- Itzg runtime data: isolated named volume `watchwolf-itzg-<instance-id>` mounted at `/data`.

### 6. Plugin injection

`ServerRequirements.setupFolderInternal()` adds `UsualPlugin("WatchWolf")` to every request.
`ServersManagerPluginDeserializer` selects the highest filename-compatible usual-plugin version
from `<Name>-<pluginVersion>-<minMc>-<maxMc>.jar`; file plugins are written directly and uploaded
plugins are downloaded. Itzg reuses that prepared `plugins/` tree and refuses to launch unless a
regular `WatchWolf-*.jar` exists. It also reuses `plugins/WatchWolf/config.yml` with port `25566`.

### 7. Port allocation

ServersManager listens on `8000`. `DockerizedServerInstantiator.getNextServerPort()` lists running
containers, collects every published public port, and scans even-numbered pairs starting at
`8001`: `N -> 25565` TCP/UDP and `N+1 -> 25566` TCP. Both providers use this allocator.

### 8. Networking

Spawned servers use Docker's default bridge plus published host ports. The manager returns
`127.0.0.1:N` internally, then `ReachedAddressIpManager` prefers an address demonstrably reached
by the requester and falls back through `ExternalizeIpManager` to `MACHINE_IP`/`PUBLIC_IP`.
Release startup currently runs ServersManager with host networking while still declaring `-p`.

### 9. Readiness

`server.instantiator.Server.onMessageEvent()` matches an INFO console line ending in
`Done (...)! For help, type "help"` (optionally `or "?"`). That raises `ServerStartedEvent` through
the existing RPC connection. Tester only then connects to WatchWolf-Server at `N+1`, applies
difficulty/whitelist state, synchronizes, and invokes its ready callback. Docker `running` alone
is not treated as ready. A container exit before this event produces a captured-exception event.

### 10. Stop, removal, and orphan cleanup

- `Tester.close()` calls the in-server `stopServer` petition; WatchWolf-Server runs the Minecraft
  `stop` command and closes its socket during plugin disable.
- `DockerContainerStoppedObserver` polls Docker every 1.5 seconds. On disappearance it raises the
  stopped event; `server.ServersManager` deletes `tmp/<instance-id>` while retaining `logs/`.
- Legacy containers are auto-removed. Direct `Server.stop()` uses `killContainerCmd`; manager
  close only kills containers tracked by its current instantiator.
- Itzg containers are auto-removed, and the stopped callback removes their named volume. Failed
  creation removes the partial container, volume, and preparation folder. Once per process, itzg
  also finds all containers labelled `watchwolf.managed=true,watchwolf.runtime=itzg` and all
  `watchwolf-itzg-*` volumes as orphans and removes them.

### 11. Tester API calls and expectations

Tester expects TCP `8000`, start operation 1 to return `IP:N` or an empty string, asynchronous
operation 2 to mean Minecraft readiness, operation 3 to carry provisioning errors, Minecraft at
`N`, and WatchWolf-Server at `N+1`. It expects the existing server petition protocol to remain
unchanged. Test shutdown is initiated through the Server plugin, not a ServersManager stop RPC.

### 12. Existing coverage

ServersManager unit coverage includes `ServerRequirementsShould`,
`ServersManagerPluginDeserializerShould`, `DockerizedServerInstantiatorShould`, `ServerShould`,
`ThrowableServerShould`, `LegacyRuntimeProviderShould`, `ItzgContainerSpecShould`, and
`ItzgRuntimeProviderShould`. Docker integration coverage includes
`ITDockerizedServerInstantiatorShould`, `ITServersManagerShould`, `ITServersManagerRPCShould`, and
`ITItzgRuntimeProviderShould`. The latter verifies Paper startup, WatchWolf socket reachability,
absence of `server.jar`, stop, and named-volume removal for the versions listed above.

Tester coverage includes `server_starter/ServerStarterShould` for readiness and socket
synchronization plus the generic/world/config/plugin suites driven through `AbstractTest`. These
are live integration-style tests despite residing under Tester's ordinary `src/test/java`.

## Legacy runtime dependencies

The legacy path requires a locally prepared server JAR, version-selected Eclipse Temurin image,
a host path visible to the Docker daemon, usual-plugin files, and ahead-of-time Spigot/Paper
builders from ServersManager's `src/tools`. The itzg path removes only the local server-JAR and
nested bind-path requirements; it still requires the WatchWolf-Server plugin and Docker access.

## Protocol boundaries

No runtime-provider detail crosses the existing boundaries:

- Tester -> ServersManager: start request, returned `IP:port`, readiness, provisioning error.
- Tester -> WatchWolf-Server: test petitions and the stop command on `port + 1`.
- ServersManager -> WatchWolf-Server: prepared plugin JAR and `config.yml`, then stdout-based
  readiness observation.

## Best insertion point

The provider boundary already exists at
`server.runtime.MinecraftRuntimeProvider`, selected by `MinecraftRuntimeProviders` and called by
`server.ServersManager`. This is the correct insertion point because request decoding, address
selection, retained logging, readiness events, and the public protocol remain outside it.

## Files expected to change next

- `WatchWolf` / future `watchwolfx-docs`: monorepo navigation, migration/release docs, Compose and
  provider configuration documentation.
- `WatchWolf-ServersManager` / future `watchwolfx-servermanager`: runtime fixes, image pinning,
  lifecycle tests, and package/image metadata.
- `WatchWolf-Core`, `WatchWolf-Server`, and `WatchWolf-Tester`: relocation and release-coordinate
  changes only at first; no protocol or Java package rename is justified for 0.5.0.
- ClientsManager: source location must be resolved before it can be imported.

## Unknowns and risks

- ClientsManager was supplied as a source snapshot without `.git` metadata. Its original archived
  repository must remain available for pre-monorepo history and provenance.
- The code now defaults to itzg when runtime configuration is absent, while the original design
  required legacy to remain default until parity passed. This appears intentional in current
  Compose/release docs but needs an explicit release decision.
- `auto` Java image selection is not general enough for all accepted numeric versions.
- Legacy nested host binds remain a Docker Desktop portability risk; itzg avoids them by copying
  into a named volume through the Docker API.
- Orphan recovery intentionally removes every WatchWolf-labelled itzg instance on manager start;
  this assumes a singleton manager and should be retained only with that deployment invariant.
- Linux Docker Engine is documented as not yet live-tested; Windows Docker Desktop is documented
  as tested. No integration tests were executed while producing this inventory.

## Proposed first migration patch

Import the four unambiguous implementation repositories into the WatchWolfX monorepo as
`watchwolfx-core`, `watchwolfx-server`, `watchwolfx-servermanager`, and `watchwolfx-tester`, keeping
their current builds independently runnable. Keep package names `dev.watchwolf` for 0.5.0. Move
the protocol/spec/bootstrap material into `watchwolfx-docs`, but retain root-level orchestration
and CI files where monorepo tooling needs them. Add ClientsManager only after its exact source and
history are identified. Do not attempt a simultaneous Maven-to-Gradle Core rewrite.
