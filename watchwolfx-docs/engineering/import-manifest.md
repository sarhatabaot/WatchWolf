# Monorepo import manifest

Imported on 2026-11-21 into the `watchwolfx-0.5.0` branch.

| WatchWolfX module | Source checkout | Source branch | Source commit |
| --- | --- | --- | --- |
| `watchwolfx-core` | `WatchWolf-Core` | `dev` | `bffa403b5c8e7626488ec1502331b932b3c33025` |
| `watchwolfx-server` | `WatchWolf-Server` | `feature/release-0_4_2` | `b5e3dfeeca97bebb9c4edade5cae1d5eb1ef63b0` |
| `watchwolfx-servermanager` | `WatchWolf-ServersManager` | `feature/release-0_4_2` | `eee2a86225fef5354649c04ecbd2890bf8f15f7b` |
| `watchwolfx-tester` | `WatchWolf-Tester` | `feature/release-0_4_2` | `7ed3258e9a8c789cfe472a8c1e099a8122742aa7` |
| `watchwolfx-clientsmanager` | `WatchWolf-Client-dev` snapshot | unavailable | unavailable (`.git` was not supplied) |

The four Git-backed modules were copied from `git ls-files`, excluding untracked `bin/` build
output. SHA-256 comparison found no differences between imported tracked files and their source
checkouts, except that module release workflows were deliberately relocated to the repository-root
`.github/workflows/` directory and updated for monorepo paths/names.

The ClientsManager snapshot contained no nested repository or runtime `logs/` directory. Its
source files were copied as supplied. Preserve the archived WatchWolf-Client repository for commit
history, authorship and provenance before this import.

The protocol/API files retain the original WatchWolf repository history through Git renames into
`watchwolfx-docs`.
