# Development

The monorepo keeps separate build and release units. There is no unified root Gradle build.

## Module commands

| Module | Build | Fast verification |
| --- | --- | --- |
| Core | `./gradlew assemble` | `./gradlew test` |
| Server | `./gradlew assemble` | compile/assemble; exercised end to end by Tester |
| ServerManager | `./gradlew assemble` | `./gradlew test` |
| Tester | `./gradlew assemble` | live tests require `-PrunTests=true` and a running environment |
| ClientsManager | `uv build` | `uv run ruff check .`, `uv run basedpyright`, `uv run pytest` |
| Documentation | `zensical build --clean --strict` | same command used by Pages CI |

Run Java commands from the corresponding module directory. On Windows use `gradlew.bat`.

## Build Core

Core targets Java 8 bytecode and produces a shaded JAR:

```bash
cd watchwolfx-core
./gradlew clean test shadowJar
```

The artifact lands at `build/libs/watchwolfx-core-<version>.jar`. Server and ServerManager consume
the current Core artifact from their ignored `lib/` directories during standalone builds. The
release workflow and ServerManager image populate those directories automatically.

## Test layers

Report each layer separately:

1. unit and static checks;
2. build or container-image checks;
3. control-plane health checks;
4. real Minecraft/Tester integration tests;
5. platform used, especially Linux Engine versus Windows Docker Desktop.

A healthy Compose stack is not proof that Minecraft starts or that a representative Tester flow
passes.

## Preview documentation

Install the pinned documentation generator and start its local server:

```bash
python -m pip install --requirement watchwolfx-docs/pages-requirements.txt
zensical serve
```

The preview listens on `http://localhost:8000`. Before committing documentation, run:

```bash
zensical build --clean --strict
```

## Repository hygiene

- Keep generated builds, worlds, logs, server JARs, credentials, and IDE state out of Git.
- Preserve `upstream` remotes and project attribution.
- Keep changes within each module's documented responsibility.
- Do not modify generated RPC, block, or entity classes by hand.
- Distinguish the itzg runtime image version from the requested Minecraft version.
