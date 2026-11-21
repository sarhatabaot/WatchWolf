# 0002: Use Docker Compose as the only deployment workflow

- Status: accepted
- Date: 2026-11-21

## Context

The inherited setup script cloned separate repositories, downloaded release artifacts, prepared
host directories and installed startup services. That workflow conflicts with the WatchWolfX
monorepo and duplicates responsibilities already handled by Docker Compose and image builds.

## Decision

`compose.yaml` is the only supported installer and runtime entry point. A fresh checkout plus
Docker Compose must be sufficient to build and start the control plane.

The ServerManager image therefore builds the local ServerManager and WatchWolfX Server plugin
from monorepo sources. The ClientsManager image builds its locked Python and Node runtimes. Compose
supplies configuration, health checks, the Docker socket and retained log storage.

The legacy setup script, duplicate release Compose file, release environment example and
ServerManager `ci/release` artifact-download workflow are removed.

## Consequences

- Local and published images share the same Dockerfiles.
- Deployment no longer clones archived repositories or requires prebuilt control-plane JARs.
- The default itzg provider requires no host `server-types` directory.
- Legacy JAR-backed provisioning remains an internal compatibility provider but is not part of the
  default Compose deployment contract.
- Host startup integration, if desired later, should wrap `docker compose up` instead of becoming
  a second installer.
