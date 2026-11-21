# AGENTS.md — WatchWolfX monorepo

WatchWolfX is an independently maintained hard fork of WatchWolf. It is not affiliated with the
upstream WatchWolf project. Preserve links and attribution so users can distinguish the projects.

## Repository layout

| Path | Responsibility |
| --- | --- |
| `watchwolfx-docs/` | Website/docs, protocol specification, API definitions and migration notes |
| `watchwolfx-clientsmanager/` | Python/Node Clients Manager and headless Minecraft Client |
| `watchwolfx-server/` | Bukkit/Paper in-server plugin |
| `watchwolfx-servermanager/` | Dynamic Minecraft provisioning and Docker lifecycle |
| `watchwolfx-tester/` | Java/JUnit test orchestration library |
| `watchwolfx-core/` | Shared entities, RPC runtime and stub generator |

Root Compose, environment examples, CI, release orchestration and cross-module
documentation belong at the repository root. Dynamic Minecraft lifecycle logic remains in
`watchwolfx-servermanager`.

Each module retains its own `AGENTS.md` where one existed. Read the closest applicable file before
editing. Root instructions apply everywhere; module instructions add implementation details.

## Migration rules

- The 0.5.0 release is the WatchWolfX naming cut-over. Do not rename previously published 0.4.x
  artifacts.
- Keep Java packages under `dev.watchwolf` for 0.5.0 unless a separately reviewed compatibility
  plan says otherwise.
- Release modules as separate packages/images even though their source is co-located.
- Do not introduce Git submodules.
- Do not rewrite archived repository history. Preserve upstream attribution and remotes.
- Do not import build outputs, IDE state, runtime worlds, logs, server JARs, credentials or other
  generated data.
- Work on a dedicated feature/fix branch. Do not force-push or commit directly to a protected
  default branch.

## Runtime modernization rules

Read `watchwolfx-docs/engineering/runtime-inventory.md` and the workspace-level `DESIGN.md` before
changing Minecraft provisioning. Preserve the existing external protocol and legacy provider while
the itzg runtime is stabilized. ServersManager continues to launch ephemeral Minecraft containers;
they must not become static Compose services.

The preferred boundary is the existing `MinecraftRuntimeProvider` implementation in
`watchwolfx-servermanager`. Keep readiness tied to usable Minecraft/WatchWolf-Server state, not
merely Docker's `running` state. Keep cleanup idempotent and `/data` isolated. Never mount the
Docker socket into Minecraft containers.

## Builds and tests

The imported projects intentionally retain their established build systems during the first
monorepo migration step:

- Core: Maven through `watchwolfx-core/ci/*.sh`.
- Server, ServerManager and Tester: their checked-in Gradle wrappers.
- ClientsManager: Docker image plus `watchwolfx-clientsmanager/ci/tests.sh`.
- Docs/API: tools under `watchwolfx-docs/`.

Do not claim a unified root Gradle build exists until all module dependencies and Java toolchains
have actually been reconciled. Distinguish fast checks from real Docker/Minecraft integration
tests, and record exact versions and outcomes for live runs.

## Documentation continuity

- Active migration state belongs in `watchwolfx-docs/current-work.md`.
- Runtime discoveries belong in `watchwolfx-docs/engineering/`.
- Consequential architecture decisions should use a short ADR under
  `watchwolfx-docs/decisions/`.
- Generated API Markdown/SVG files must be regenerated from the JSON definitions; do not edit
  them by hand.
