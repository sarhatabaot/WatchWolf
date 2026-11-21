# 0001: Package ClientsManager as a uv-managed Python project

- Status: accepted
- Date: 2026-11-21

## Context

ClientsManager was imported as a flat collection of Python scripts with a `requirements.txt`,
mixed-case module names and container-only dependency installation. That made imports dependent
on the working directory and left development, tests and image builds with different setup paths.

## Decision

Use Python 3.12, uv and a `src/watchwolfx_clientsmanager` package. Keep runtime, development and
build configuration in `pyproject.toml`, commit `uv.lock`, and expose the service through the
`watchwolfx-clientsmanager` console script. Ruff, BasedPyright and pytest form the local quality
gate. The container installs from the same lockfile and invokes the same console script.

The JavaScript bridge and Mineflayer packages remain container-managed for this patch so the
WatchWolf wire protocol and runtime behavior remain unchanged.

## Consequences

- Local development and the image use the same locked Python dependency graph.
- Imports work as a normal installed package and package data ships in the wheel.
- Python 3.12 is the only supported interpreter until compatibility is deliberately expanded.
- Node dependencies still need their own explicit manifest, lockfile and audit remediation.
