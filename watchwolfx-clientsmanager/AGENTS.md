# AGENTS.md — WatchWolfX ClientsManager

This module contains the Python ClientsManager (WatchWolf destination `0b010`) and the headless
Minecraft Client (`0b011`). uv manages Python 3.12, environments, dependencies, builds and
quality tools. Docker remains the supported full runtime because Mineflayer runs through Node.js
and recording support needs Xvfb/native libraries.

Read `docs/current-work.md`, `docs/architecture.md` and `docs/development.md` before structural,
dependency or runtime changes.

## Project structure

- `src/watchwolfx_clientsmanager/`: installable application package.
- `tests/`: isolated pytest-compatible unit tests.
- `node/package.json` and `node/package-lock.json`: exact Mineflayer runtime graph.
- `pyproject.toml` and `uv.lock`: Python requirements and exact development resolution.
- `Dockerfile`: Python/Node/Xvfb runtime image.

Use snake_case module names and package-relative imports. New code uses four-space indentation;
do not reintroduce the former top-level-script import assumptions.

## Protocol and generated-data safety

- Packet headers in the connector modules are the cross-language WatchWolf protocol. Do not
  renumber or reinterpret them without coordinated Core, Tester, Server and API changes.
- `entities/entity_type.py` and `items/items.json` are generated and ordinal-sensitive. Never
  sort, reformat or insert values manually.
- Preserve the public behavior of ports 7000 and 7001+, address selection, empty-string startup
  failure, and Tester callbacks unless a separately tested protocol change requires otherwise.
- The Python/Node bridge shares one Node process across bots. Treat unhandled bridge errors and
  concurrency changes as process-wide reliability risks.

## Dependencies and tooling

Use uv rather than pip or ad-hoc virtual environments:

```sh
uv sync --locked
uv add <dependency>
uv add --dev <tool>
uv lock --check
uv build
```

Do not hand-edit `uv.lock` or `node/package-lock.json`. Direct imports must have explicit project
dependencies. Update Node dependencies through npm and verify the audit gate.

Ruff is the formatter/linter, BasedPyright runs in standard mode, and pytest is the test runner.
Do not weaken checks to hide failures. Run:

```sh
uv run ruff check .
uv run ruff format --check .
uv run basedpyright
uv run pytest
```

## Containers

Use the Wolfi base, explicit Python/Node APK package names and intentional native packages. The
runtime must remain non-root. Never bake secrets into the image.

Recording is currently incomplete because its Node viewer dependencies remain disabled. Do not
claim it works merely because imageio/ffmpeg is installed.

## Git and handoff

Work on a dedicated branch, preserve unrelated monorepo changes, and do not push/merge without
approval. Update `docs/current-work.md` for active work and `docs/architecture.md` or an ADR for
lasting decisions. Final reports must distinguish unit checks, image builds and real WatchWolfX
integration tests.
