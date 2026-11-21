# WatchWolfX Server

The in-game half of [WatchWolf](https://watchwolf.dev/): a **Spigot plugin** that opens a socket
and executes the Tester's petitions against the live server — place and read blocks, spawn and
inspect entities, teleport players, give items, run commands, manage WorldGuard regions, record
video and collect timings reports.

`dev.watchwolfx:watchwolfx-server:0.5.0` · **Java 8** · Spigot 1.8 → latest

You can download a pre-compiled version [here](https://watchwolf.dev/versions/).

## How it runs

You do not normally install this by hand.
[WatchWolf-ServersManager](https://github.com/miranda1000/WatchWolf-ServersManager) keeps a
copy in its `usual-plugins/` folder and drops it into every server it starts, together with a
generated config:

```yaml
# plugins/WatchWolf/config.yml
target-ip: 127.0.0.1   # the only IP allowed to talk to the socket
use-port: 25566        # published by the container as <minecraftPort>+1
```

On `onEnable` the plugin starts a socket thread on `use-port`, hooks a log4j appender onto the
console (so command output and stack traces can be forwarded), and registers a 1-tick repeating
Bukkit task. Every petition that touches the Bukkit API is queued onto that task, because the
socket thread is not the server's main thread.

## Features

| Area | Operations |
| --- | --- |
| World | set/get block, spawn entity, get entities, change difficulty |
| Players | whitelist, OP, teleport, give item, read position/pitch/yaw/inventory, list players |
| Console | run command and capture its reply, forward captured exceptions |
| Safety | invincible mode (cancel all player damage) |
| WorldGuard | create region, list regions, list regions at a position |
| Enhanced info | place/move/stop camera (video), start/stop timings report |

Optional integrations degrade gracefully: WorldGuard is picked by version (API 6 below `7.0.0`,
API 7 above) and falls back to a no-op implementation when the plugin is absent; timings switch
between Spigot and Paper implementations at runtime.

## Compile

Use a JDK supported by Gradle 9 (Java 17 or newer). The compiled plugin targets **Java 8**.

```bash
./gradlew assemble
```

On Windows, use `gradlew.bat assemble`. The shaded artifact is
named `WatchWolf-<version>-1.8-LATEST.jar` — that `-<minMc>-<maxMc>` suffix is the naming
convention the ServersManager's `usual-plugins/` folder expects, so the jar can be copied straight
across.

### Dependencies

Gradle resolves `spigot-api:1.16.5-R0.1-SNAPSHOT` from the Spigot snapshots repository. It is a
compile-only dependency: Minecraft supplies the API at runtime. This plugin does not import NMS or
CraftBukkit classes, so building it needs no local Spigot server JAR. Gradle resolves
`watchwolfx-core:0.5.0` from `lib/` and `XSeries:9.9.0`, shading both into the plugin JAR.
ServersManager now defaults to the itzg runtime, which downloads the requested supported Paper
version when an instance starts. Prebuilt server JARs under `server-types/` are needed only when
the explicit `legacy` fallback is selected.

XSeries keeps the plugin working
across every Minecraft version despite compiling against the 1.16.5 API.

### Publish to GitHub Packages

For a GitHub Release, push tag `v<plugin version>` to this fork. The tag workflow builds the
shaded Java 8 plugin and creates the release with `WatchWolf-<version>-1.8-LATEST.jar` plus its
SHA-256 file. Let this workflow create the release so immutable releases are supported. Finish this
release before the matching ServersManager image release; that image downloads this exact asset.

The optional Maven Packages publication uses this fork's package endpoint. To publish it manually,
set `GITHUB_ACTOR` and `GITHUB_TOKEN` (with package publish access), then run:

```bash
./ci/publish.sh
```

This publishes the shaded JAR to `https://maven.pkg.github.com/sarhatabaot/WatchWolf-Server`.

## Testing

There are no tests in this repository. The plugin is exercised end to end by
[WatchWolf-Tester](https://github.com/miranda1000/WatchWolf-Tester)'s own suite, which starts
real servers running this jar.

## Adding support for a new block property

Block properties are code-generated. The procedure spans several repositories and is documented in
[WatchWolf-MaterialGetter](https://github.com/miranda1000/WatchWolf-MaterialGetter); on this
side it ends in `SpigotToWatchWolfTranslator#getBlock()` and
`WatchWolfToSpigotTranslator#getBlockData()`. Because the wire layout changes, it is also a
protocol change — update `API/API.tex` and `API/A-Blocks.tex` in the
[WatchWolf](https://github.com/watch-wolf/WatchWolf) repository.

## Related

- [WatchWolf](https://github.com/watch-wolf/WatchWolf) — the protocol specification
- [WatchWolf-ServersManager](https://github.com/miranda1000/WatchWolf-ServersManager) — starts the servers this plugin runs on
- [WatchWolf-Tester](https://github.com/miranda1000/WatchWolf-Tester) — sends the petitions
- [WatchWolf-MaterialGetter](https://github.com/miranda1000/WatchWolf-MaterialGetter) — generates the block classes
