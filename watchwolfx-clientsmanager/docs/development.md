# Development

## Prerequisites

- uv 0.9 or newer.
- Python 3.12 (declared by `requires-python`).
- Docker for the supported full runtime and real WatchWolfX integration tests.

## Setup and checks

```sh
uv sync --locked
uv run ruff check .
uv run ruff format --check .
uv run basedpyright
uv run pytest
uv build
npm ci --ignore-scripts --prefix node
npm audit --omit=dev --audit-level=high --prefix node
```

`./ci/tests.sh` runs the same quality suite. Unit tests mock the JavaScript bridge and do not
require Mineflayer or a Minecraft server.

## Run

For package-level debugging where Node dependencies already exist:

```sh
uv run watchwolfx-clientsmanager
```

The supported runtime is the Wolfi-based Docker image because Mineflayer, Node.js and JSPyBridge
must be installed together:

```sh
docker build -t watchwolfx-clientsmanager:dev .
./run.sh --force-recreate
```

Runtime configuration uses `MACHINE_IP` and `PUBLIC_IP`. Do not put address values or secrets in
the repository.
