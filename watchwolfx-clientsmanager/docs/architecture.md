# Architecture

## Purpose and scope

This module contains two Python components that share one process and distribution:

- ClientsManager listens on TCP 7000 (WatchWolf destination `0b010`) and creates bots.
- Each headless Minecraft Client exposes its petition socket (destination `0b011`) on a port
  allocated from 7001 in steps of two.

The binary protocol remains compatible with the other WatchWolfX modules. Protocol opcodes and
generated entity/item ordinals are compatibility boundaries, not internal implementation details.

## Package layout

Production code is installed from `src/watchwolfx_clientsmanager`. The console entry point
`watchwolfx-clientsmanager` calls `clients_manager.main`. `entities/entity_type.py` and
`items/items.json` are generated, order-sensitive protocol data and must not be reordered by hand.

Mineflayer runs in Node.js through the Python `javascript` bridge. Its exact npm graph is committed
under `node/` and installed into JSPyBridge's private module directory during the image build. The
Wolfi image supplies Python 3.12, Node 22 and Xvfb and runs the application as `nonroot`.

## Networking and lifecycle

`ReplyAddress` chooses the address returned to Tester. It uses the reached address only when the
process shares the host network namespace; bridged deployments fall back to `MACHINE_IP` or
`PUBLIC_IP`. Bot connectors bind `0.0.0.0`. Login currently blocks `start_client` and has an
existing eight-second workaround delay; this packaging migration does not change that behavior.

## Known constraints

- The locked Mineflayer authentication graph has six moderate npm advisories for which npm offers
  only an incompatible downgrade. High and critical advisories fail the module CI gate.
- Recording dependencies remain disabled on the Node side; `prismarine-viewer` and
  `node-canvas-webgl` are not installed.
- The existing implementation uses synchronous sockets and shared threads. Any concurrency
  redesign requires protocol-level integration tests.
