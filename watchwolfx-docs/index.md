---
layout: home
title: WatchWolfX documentation
---

WatchWolfX is an independently maintained hard fork of WatchWolf for integration-testing
Minecraft plugins. The project keeps the existing protocol compatible while modernizing the
control plane, build, release, and Minecraft runtime paths.

## Start here

- [Run WatchWolfX](https://github.com/sarhatabaot/WatchWolfX#run-watchwolfx)
- [Current engineering work](current-work.md)
- [Minecraft runtime inventory](engineering/runtime-inventory.md)
- [WatchWolfX migration plan](migration.md)
- [Architecture decisions](decisions/)

## Protocol and API

- [Protocol specification](API/API.pdf)
- [Machine-readable API definitions](API/definitions/)

## Project status

The Java modules and ClientsManager currently share release version `0.5.2`. Stable release
workflows publish versioned artifacts and update the `latest` control-plane image tags. The
Minecraft runtime defaults to the dynamic `itzg/minecraft-server` provider, with the legacy
provider retained as a fallback.
