# Protocol and API

WatchWolfX components communicate through the existing WatchWolf socket protocol. Compatibility is
more important than internal implementation details: runtime modernization does not by itself
justify changing the wire format.

## Sources of truth

- [Protocol specification PDF](../API/API.pdf)
- [Machine-readable API definitions](../API/definitions/README.md)
- Core petition interfaces under `watchwolfx-core/src/main/java/dev/watchwolf`
- Generated Core RPC stubs under `watchwolfx-core/src/main/java/dev/watchwolf/core/rpc/stubs`

## Packet header

A petition begins with a 16-bit header followed by operation-specific arguments:

```text
15                                4   3   2       0
+------------------------------------+---+---------+
|             operation              | r |   DST   |
+------------------------------------+---+---------+
|                  arguments ...                   |
+--------------------------------------------------+
```

`DST` selects the destination component and `r` distinguishes calls from returns. Core's RPC
converter marshals shared entities into this framing.

## Generated code

RPC stubs and petition interfaces are generated from pinned definition URLs. Regenerate them with:

```bash
cd watchwolfx-core
./ci/rpc-gen.sh
```

Do not hand-edit files carrying the generated-code warning. Updating JSON definitions alone does
not regenerate Core until the pinned definition revision is updated.

## Compatibility rule

When a protocol change is unavoidable:

1. document the compatibility reason;
2. update every affected sender and receiver together;
3. regenerate stubs where applicable;
4. add protocol and integration coverage;
5. keep the existing runtime providers behaviorally aligned.
