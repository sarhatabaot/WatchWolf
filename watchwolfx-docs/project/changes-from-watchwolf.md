# Changes from WatchWolf

WatchWolfX is an independently maintained hard fork of
[WatchWolf](https://github.com/watch-wolf/WatchWolf). The projects share history through the
original WatchWolf line, but releases begin to diverge at **0.4.0**. The WatchWolfX name was
adopted later, in 0.5.0.

!!! note "Version boundary"

    Versions through 0.3.2 belong to the shared upstream history. Version 0.4.0 is the first
    fork-maintained release. Existing 0.4.x artifacts kept the WatchWolf name; they were not
    retroactively renamed after the WatchWolfX transition.

## At a glance

| Version | Status | Difference from upstream WatchWolf |
| --- | --- | --- |
| 0.4.0 | Released | First divergent release; added the itzg bootstrap path, Compose development workflow, a complete example plugin, and container release automation |
| 0.4.1 | Released | Documented and stabilized the manual GitHub release process |
| 0.4.2 | Released | Updated examples and dependency references to published fork artifacts |
| 0.4.3 | Released | Final release preparation on the WatchWolf-named 0.4.x line |
| 0.5.0 | Released | Renamed the project to WatchWolfX, consolidated the repositories into a monorepo, and modernized deployment and publishing |
| 0.5.1 | Released | Corrected package publishing for the renamed repository |
| 0.5.2 | Current source; unreleased | Migrates Core from Maven to Gradle, removes duplicated version pins, strengthens release validation, and introduces the new documentation site |

## 0.4.x: the first fork releases

### 0.4.0

Version 0.4.0 established the fork's initial direction without changing the project name:

- added an `itzg/minecraft-server` bootstrap path so supported Minecraft runtimes could be
  acquired instead of relying exclusively on manually prepared server files;
- introduced a Compose-based local development environment;
- documented building the forked Server plugin with Gradle;
- added a runnable Bukkit example plugin with a Compose-backed WatchWolf test;
- added automation for publishing the ClientsManager container image and documented a release
  deployment workflow.

The protocol and Java package namespace remained compatible with WatchWolf.

### 0.4.1–0.4.3

These were consolidation releases rather than architectural rewrites:

- 0.4.1 documented the manual GitHub release flow;
- 0.4.2 moved examples and version references to the published 0.4.x artifacts;
- 0.4.3 prepared the final release in the WatchWolf-named fork line.

## 0.5.0: WatchWolfX

Version 0.5.0 was the distribution and project-identity cutover:

- renamed the maintained project and published outputs to **WatchWolfX**;
- imported Core, Server, ServerManager, Tester, ClientsManager, and documentation into one
  repository;
- kept the existing `dev.watchwolf` Java packages and protocol contracts to limit downstream
  breakage;
- made `compose.yaml` the supported control-plane deployment entry point;
- changed ServerManager images to build Core, ServerManager, and the Server plugin from monorepo
  sources instead of consuming checked-in release artifacts;
- modernized ClientsManager as a Python 3.12 package with locked dependencies, linting, type
  checking, and tests;
- moved owned runtime containers to minimal non-root images where compatible;
- added coordinated Java package, release JAR, and GHCR image workflows;
- made example control-plane images configurable instead of fixing them to one release.

This release changed repository layout, artifact names, image names, and deployment workflow, but
it intentionally avoided an unnecessary application-protocol or Java-package rename.

## 0.5.1: publishing correction

Version 0.5.1 fixed Java package publishing so artifacts are sent to the repository that triggered
the workflow. It is the latest published release before the current 0.5.2 development line.

## 0.5.2: current development line

The current source tree advances the build and release model:

- Core now uses Gradle while retaining Java 8 bytecode, its shaded JAR, RPC generation, and
  separate unit and integration test tasks;
- ServerManager's container build discovers the Core artifact version from the Gradle project
  instead of maintaining a second hard-coded version;
- release workflows verify that module versions match the release tag before publishing;
- stable container releases update `latest`, while prereleases remain version-only;
- examples can select an explicit WatchWolfX version or follow the current stable images;
- the repository and documentation are published under the WatchWolfX name;
- the documentation site now has task-oriented guides, architecture material, project records,
  search, and a maintained Pages build.

Version 0.5.2 has not been released yet. Details can change until the `v0.5.2` tag is published.

## Compatibility and upgrade impact

| Area | WatchWolf 0.3.x and earlier | WatchWolfX 0.5.x |
| --- | --- | --- |
| Repository | Separate WatchWolf repositories | One WatchWolfX monorepo |
| Java packages | `dev.watchwolf` | Preserved as `dev.watchwolf` |
| Wire protocol | WatchWolf protocol | Preserved unless a future change is explicitly documented |
| Deployment | Setup scripts and prepared artifacts | Compose control plane with dynamically provisioned Minecraft runtimes |
| Minecraft runtime | Primarily locally prepared server artifacts | `itzg` provider by default, with a legacy fallback |
| Java artifacts | WatchWolf coordinates and per-repository builds | WatchWolfX coordinates with coordinated module versions |
| Container images | WatchWolf-era image names and workflows | `ghcr.io/sarhatabaot/watchwolfx-*` images |
| Release versioning | Independent repository references | Shared WatchWolfX release version across maintained modules |

Users moving from the original project should update repository coordinates, artifact and image
names, and deployment configuration. Existing code that imports `dev.watchwolf` classes does not
need a package-wide rename solely because of the fork.

## Deliberate exclusions

WatchWolfX does not include or depend on the experimental Java CLI installer developed separately
upstream. The maintained deployment path is Docker Compose, and ServerManager continues to create
Minecraft instances dynamically.

## Provenance

The `upstream` Git remote is retained and points to the original WatchWolf repository. Imported
module revisions are recorded in the [monorepo import manifest](engineering/import-manifest.md).
That record distinguishes inherited module source from WatchWolfX-specific build, deployment, and
release work.
