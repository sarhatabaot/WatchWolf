# Current work

## WatchWolfX 0.5.0 monorepo migration

- Branch: `watchwolfx-0.5.0` in the current `WatchWolf` checkout.
- Scope: consolidate the maintained forks under the WatchWolfX name while preserving separate
  packages/images and the existing `dev.watchwolf` protocol/package compatibility for 0.5.0.
- Completed: inspected `DESIGN.md`, `migration.md`, all available repository instructions, the
  runtime call chain, Docker lifecycle, relevant tests, and `minecraft-plugin-agent-starter.zip`;
  recorded the evidence in `engineering/runtime-inventory.md`.
- Starter material adopted: durable current-work/architecture evidence, realistic integration
  test reporting, and protected-branch discipline.
- Starter material intentionally not copied: its generic plugin `AGENTS.md`, placeholder plugin
  architecture/support matrix, and assumed `testWatchWolf` task. Existing repository-specific
  instructions and actual build tasks are authoritative.
- Resolved: `WatchWolf-Client-dev` was supplied as the source for `watchwolfx-clientsmanager`.
  It is a source snapshot without `.git` metadata, so its archived repository remains the source
  of pre-monorepo history.
- Completed: imported all five implementation trees without nested repositories or generated
  `bin/` output; moved the protocol/API material into `watchwolfx-docs`; added root monorepo
  navigation and scope instructions; redirected Compose and release workflow contexts to the
  monorepo. Import revisions are recorded in `engineering/import-manifest.md`.
- Deployment decision: `compose.yaml` is the sole supported installer/runtime entry point; the
  legacy destructive setup script and duplicate release Compose file were removed.
- Verified: all four Git-backed module imports match their source files byte-for-byte, excluding
  deliberately relocated workflows. The sole supported `compose.yaml` configuration parses.
- Completed: modernized `watchwolfx-clientsmanager` as an installable Python 3.12 src-layout
  package using uv, Ruff, BasedPyright and pytest; its wheel and Docker image both build.
- Completed: moved ClientsManager to Wolfi/nonroot, locked its Mineflayer graph, and locked the
  existing Wolfi API-docs renderer. Container base policy is recorded in ADR 0001.
- Completed: made Compose build ServerManager and the Server plugin from monorepo sources into a
  Wolfi/OpenJDK runtime, eliminating prebuilt `ci/release` artifacts from deployment.
- Completed: aligned the Java release coordinates to WatchWolfX 0.5.0, added an ordered Java
  artifact workflow, and made the ServerManager image build Core, ServerManager and Server from
  the monorepo source tree.
- Verified: Core passed 33 tests on Java 8; Server assembled successfully; ServerManager passed
  103 tests with 5 skipped on Java 17; Tester assembled successfully. Tester's ordinary test
  source set is a live integration suite and is therefore not executed by the artifact release
  workflow without a running WatchWolf environment.
- Verified: the source-built Compose stack reached healthy status for both control-plane
  services on Windows Docker Desktop with Linux containers. This was a control-plane smoke test,
  not a full Minecraft/Tester integration run. Linux Docker Engine remains untested here.
