# WatchWolf - Core

Common classes for the [WatchWolf](https://watchwolf.dev/) framework: the domain model every
module exchanges, the RPC runtime that carries it over a socket, and the generator that turns the
[API definitions](https://github.com/watch-wolf/WatchWolf/tree/main/API/definitions) into Java
stubs.

`dev.watchwolfx:watchwolfx-core:0.5.2` · **Java 8 bytecode** · Gradle

This is a library — it has no `main`. It is consumed by
[WatchWolf-ServersManager](https://github.com/miranda1000/WatchWolf-ServersManager) and, as
the migration progresses, by the rest of the framework.

## What's inside

| Package | Contents |
| --- | --- |
| `dev.watchwolf.core.entities` | `Position`, `ServerType`, `WorldType`, `Difficulty`, `Version`, `Container` |
| `…entities.blocks` | `Block`, the property mixins (`Ageable`, `Openable`, `Directionable`, …), their `transformer/`s, and 562 generated block classes under `blocks.special` |
| `…entities.entities` | `Entity` plus one class per mob, and the `EntityType` ordinal enum |
| `…entities.items` | `Item`, `ItemType` |
| `…entities.files` | `ConfigFile`, `ZipFile`, `WorldFile`, and `plugins/` (`UsualPlugin`, `UploadedPlugin`, `FilePlugin`) |
| `dev.watchwolf.core.rpc` | `RPC`, `RPCFactory`, the `MessageChannel` abstraction and its socket implementations, the `RPCObject`/`RPCConverter` marshalling tree, and the generated `stubs/` |
| `dev.watchwolf.core.utils` | `DockerUtilities` (Minecraft version → required JDK), `Version` |
| `dev.watchwolf.{client,clientsmanager,server,serversmanager,tester}` | The petition interfaces of each module — the protocol surface |

## Requirements

- Docker (the convenience scripts run Gradle inside a container, so no local JDK is needed)

## Compile

```bash
./ci/build.sh --preclean
```

Produces `build/libs/watchwolfx-core-<version>.jar`, a shaded jar with no classifier. Downstream
projects discover `lib/watchwolfx-core-*.jar`, so the project version controls the artifact name.

## Test

```bash
./ci/tests.sh --unit                            # unit tests
./ci/tests.sh --integration                     # integration tests
./ci/tests.sh --unit --tests 'VersionShould'    # filter to one class
```

Reports are written to `build/reports/tests/test` (unit) and
`build/reports/tests/integrationTest` (integration).

Test names are part of the contract, and `./ci/validator.sh` checks them:

- unit tests live in `src/test/java` and **must end in `Should`**
- integration tests live in `src/integration-test/java`, **must start with `IT`**, and **must
  declare `@Timeout`** on the class

A file that breaks those rules is silently never executed.

## Regenerate the RPC stubs

The classes under `dev.watchwolf.core.rpc.stubs` are generated from the JSON API definitions:

```bash
./ci/rpc-gen.sh
```

The definitions are fetched over the network from a **pinned commit** listed in
[`DefinitionDataFactory`](src/scripts/java/dev/watchwolf/rpc/DefinitionDataFactory.java) — editing
the JSON in the [WatchWolf](https://github.com/watch-wolf/WatchWolf) repository has no effect here
until that URL is updated.

Generated files carry a `/!\ Class generated automatically; do not modify /!\` header. The block
classes under `entities.blocks.special` are generated too, but by a different tool —
[WatchWolf-MaterialGetter](https://github.com/miranda1000/WatchWolf-MaterialGetter).

## Use it as a dependency

Releases are published to GitHub Packages
(`https://maven.pkg.github.com/<owner>/<repository>`, selected from `GITHUB_REPOSITORY`) and attached to
[GitHub releases](https://github.com/watch-wolf/WatchWolf-Core/releases). The other WatchWolf
modules take the simpler route: drop the jar into `lib/` and let Gradle consume it locally.

```xml
<dependency>
    <groupId>dev.watchwolfx</groupId>
    <artifactId>watchwolfx-core</artifactId>
    <version>0.5.2</version>
</dependency>
```

## Related

- [WatchWolf](https://github.com/watch-wolf/WatchWolf) — the protocol specification
- [WatchWolf-ServersManager](https://github.com/miranda1000/WatchWolf-ServersManager) — first consumer
- [WatchWolf-Tester](https://github.com/miranda1000/WatchWolf-Tester) — consumes the shared entities and protocol helpers from this library
