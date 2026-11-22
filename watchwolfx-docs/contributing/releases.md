# Releases

WatchWolfX modules remain separately published but share one release version. The current source
version is `0.5.2`.

## Published outputs

| Output | Publication |
| --- | --- |
| Core | GitHub Packages and release JAR |
| Server plugin | GitHub Packages and release JAR |
| Tester | GitHub Packages and release JAR |
| ServerManager | GHCR image |
| ClientsManager | GHCR image |
| Checksums | Attached to the GitHub Release |

## Version validation

Publishing a GitHub Release named `v<version>` starts independent Java and container workflows.
Each workflow verifies that its participating module versions match the release tag before
publishing anything.

For a stable release, container workflows publish both:

```text
ghcr.io/sarhatabaot/watchwolfx-servermanager:v0.5.2
ghcr.io/sarhatabaot/watchwolfx-servermanager:latest
ghcr.io/sarhatabaot/watchwolfx-clientsmanager:v0.5.2
ghcr.io/sarhatabaot/watchwolfx-clientsmanager:latest
```

Prereleases receive the versioned tag but do not move `latest`.

## Release checklist

- [ ] All Gradle project versions match.
- [ ] `watchwolfx-clientsmanager/pyproject.toml` and `uv.lock` match the release.
- [ ] Core unit tests and shaded JAR pass.
- [ ] ServerManager unit tests pass.
- [ ] ClientsManager lint, types, and tests pass.
- [ ] ServerManager's multi-stage image builds from a clean checkout.
- [ ] A representative real Paper/Tester workflow passes.
- [ ] Documentation builds in strict mode.
- [ ] Release notes distinguish tested platforms and Minecraft versions.

## Reproducibility

The release tag fixes the WatchWolfX artifacts. Runtime reproducibility additionally requires
pinning the itzg runtime image or digest and explicitly requesting a Minecraft version. These are
separate version dimensions and should not be conflated.
