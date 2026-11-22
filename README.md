# WatchWolfX

WatchWolfX is an independently maintained hard fork of
[WatchWolf](https://github.com/watch-wolf/WatchWolf) for integration-testing Minecraft plugins on
real servers and clients. It follows its own compatibility and release decisions and is **not
affiliated with the upstream WatchWolf project**.

Starting with 0.5.0, the maintained components live in one repository while remaining separately
buildable and releasable:

| Module | Previous source | Responsibility |
| --- | --- | --- |
| [`watchwolfx-docs`](watchwolfx-docs/) | WatchWolf standard repository | Website/docs, protocol and API docs |
| [`watchwolfx-clientsmanager`](watchwolfx-clientsmanager/) | WatchWolf-Client | Clients Manager and headless client |
| [`watchwolfx-server`](watchwolfx-server/) | WatchWolf-Server | In-server testing plugin |
| [`watchwolfx-servermanager`](watchwolfx-servermanager/) | WatchWolf-ServersManager | Dynamic Minecraft provisioning |
| [`watchwolfx-tester`](watchwolfx-tester/) | WatchWolf-Tester | JUnit-side orchestration |
| [`watchwolfx-core`](watchwolfx-core/) | WatchWolf-Core | Shared entities and RPC runtime |

The 0.4.x releases keep their original names and coordinates. The 0.5.0 cut-over does not rename
the Java package namespace (`dev.watchwolf`) so protocol and consumer migration can remain
separate from product naming.

## Development status

The source trees have been consolidated without rewriting their build systems. Use each module's
wrapper or CI scripts and read its local `AGENTS.md` before changing it. The current runtime flow,
Docker behavior, test coverage and known risks are recorded in
[`runtime-inventory.md`](watchwolfx-docs/project/engineering/runtime-inventory.md). Active migration
work is tracked in [`current-work.md`](watchwolfx-docs/project/current-work.md).

Root Compose files manage the long-lived WatchWolfX control plane. ServerManager retains Docker
daemon access because it dynamically launches ephemeral Minecraft instances; Minecraft instances
are not Compose services and never receive the Docker socket.

## Run WatchWolfX

Docker Compose is the supported installation and runtime workflow:

```sh
cp .env.example .env
docker compose up --build --wait -d
```

`compose.yaml` builds both control-plane services from the monorepo, includes the WatchWolfX
Server plugin in ServerManager, and preserves ServerManager's dynamic Docker access for ephemeral
Minecraft instances. Set `WATCHWOLFX_SERVERMANAGER_IMAGE` and `WATCHWOLFX_CLIENTSMANAGER_IMAGE`
to published tags when building locally is not desired.

See the [WatchWolfX documentation](http://sarhatabaot.net/WatchWolfX/) for the protocol
specification, migration plan and runtime engineering notes. Its sources live under
[`watchwolfx-docs`](watchwolfx-docs/).
