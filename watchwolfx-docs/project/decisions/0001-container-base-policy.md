# 0001: Use Wolfi or minimal Chainguard bases for owned runtime images

- Status: accepted
- Date: 2026-11-21

## Context

WatchWolfX owns multiple service and tooling images but also invokes third-party build and
Minecraft runtime images. Treating every image reference the same would either retain oversized
general-purpose bases or create unsupported forks of upstream runtimes.

## Decision

WatchWolfX-owned runtime images use `cgr.dev/chainguard/wolfi-base` when they need a shell or a
mixed runtime, and a minimal Chainguard language image when one runtime is sufficient.

- ClientsManager uses Wolfi with explicit Python 3.12, Node 22, uv and Xvfb packages.
- API documentation tooling uses Wolfi with explicit Python 3.12, Node 22 and uv packages.
- ServerManager runtime images retain Chainguard JRE bases; the published image uses the minimal
  JRE and local debug/release images use the development variant because their command logs
  environment diagnostics through a shell.

Application dependency lockfiles remain separate from the base-image choice. Release builds may
consume the refreshed `latest` Wolfi/Chainguard tag so security rebuilds are picked up; the
resolved base digest must remain visible in build provenance. A future release-hardening pass may
pin digests together with an automated update mechanism.

## Exclusions

- `itzg/minecraft-server` remains an upstream, configurable Minecraft runtime and is not rebuilt.
- Eclipse Temurin images remain the legacy provider's compatibility runtime until that provider is
  retired or a tested Java-version mapping is introduced.
- Gradle images are build tools, not shipped WatchWolfX runtime layers.

## Consequences

- Owned mixed-runtime images share one apk-based operating-system family.
- ClientsManager no longer depends on the third-party `nikolaik/python-nodejs` image.
- Wolfi package names and non-root filesystem ownership are part of image tests.
- Base freshness and exact application dependency reproducibility are handled independently.
