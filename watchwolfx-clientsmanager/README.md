# WatchWolfX ClientsManager

The player half of WatchWolfX:

- ClientsManager listens on TCP 7000 and starts Minecraft bots.
- Each Client is a Mineflayer-backed headless player controlled by WatchWolfX Tester.

This is an installable Python 3.12 application managed with uv. Node.js and Mineflayer are reached
through the `javascript` bridge; Docker is the supported complete runtime.

## Development

```sh
uv sync --locked
uv run ruff check .
uv run ruff format --check .
uv run basedpyright
uv run pytest
uv build
```

The console entry point is:

```sh
uv run watchwolfx-clientsmanager
```

That command requires the Node dependencies and runtime environment. Unit tests mock the bridge;
use the container for the actual service.

## Container

```sh
docker build -t watchwolfx-clientsmanager:dev .
./run.sh --force-recreate
```

The image uses Wolfi, installs explicit Python 3.12 and Node 22 packages, and runs as the Wolfi
`nonroot` user. Python dependencies come from `uv.lock`; Mineflayer dependencies come from
`node/package-lock.json` and are installed with `npm ci` into JSPyBridge's module directory.
`run.sh` discovers host addresses in a disposable helper and starts the service with host
networking. ClientsManager uses 7000; client sockets are allocated from 7001 in steps of two.

Runtime environment:

- `MACHINE_IP`: reachable LAN/host fallback address.
- `PUBLIC_IP`: reachable public fallback address.

`ReplyAddress` prefers the address on which Tester reached the manager only when the service
shares the host network namespace.

## Layout

| Path | Responsibility |
| --- | --- |
| `src/watchwolfx_clientsmanager/clients_manager.py` | Entry point, allocation and client registry |
| `src/watchwolfx_clientsmanager/clients_manager_connector.py` | Port 7000 protocol loop |
| `src/watchwolfx_clientsmanager/mineflayer_client.py` | Mineflayer bot implementation |
| `src/watchwolfx_clientsmanager/client_connector.py` | Per-bot petition socket |
| `src/watchwolfx_clientsmanager/connector_helper.py` | Binary wire codec |
| `src/watchwolfx_clientsmanager/entities/` | Generated-compatible entity wire types |
| `src/watchwolfx_clientsmanager/items/` | Item types and generated item metadata |
| `src/watchwolfx_clientsmanager/view/` | Optional recording implementation |
| `tests/` | Bridge-independent unit tests |
| `node/` | Exact Mineflayer dependency manifest and lockfile |
| `docs/` | Architecture and development records |

`entities/entity_type.py` and `items/items.json` are ordinal-sensitive generated protocol data.
Do not reorder them.

## Known limitation

Recording remains disabled because `prismarine-viewer` and `node-canvas-webgl` are intentionally
not in the runtime lockfile. The locked Mineflayer authentication graph currently has six moderate
npm advisories with no non-breaking upstream fix; CI fails if a high or critical advisory appears.
