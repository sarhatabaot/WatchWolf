# Minecraft runtime providers

ServerManager keeps Minecraft provisioning behind a runtime-provider boundary. The external test
contract stays the same regardless of provider.

## Provider comparison

| Capability | `itzg` | `legacy` |
| --- | --- | --- |
| Default | Yes | No |
| Paper acquisition | Downloads on first start | Requires prepared server artifacts |
| Java selection | Runtime image responsibility | WatchWolfX selects a Java image |
| Instance storage | Isolated `/data` | Existing legacy paths and mounts |
| Primary purpose | Current runtime path | Compatibility fallback |

## itzg mapping

For a supported Paper request, ServerManager maps WatchWolfX requirements to the container:

```text
server type = Paper       -> TYPE=PAPER
version     = 1.20.6      -> VERSION=1.20.6
memory      = requested   -> MEMORY=<requested, when mapped>
runtime     = itzg        -> configured image and tag
```

Docker CPU and memory limits remain separate from JVM memory configuration.

## Plugin delivery

ServerManager places the WatchWolfX Server plugin and selected usual plugins into every instance.
The itzg image runs Minecraft; it does not decide which WatchWolfX plugins a test requires.

## Readiness

A provider is ready only when:

1. the container started successfully;
2. Minecraft reached a usable state;
3. the WatchWolfX Server plugin loaded;
4. the test-side connection can proceed.

Startup errors should preserve the useful container failure and log context instead of becoming an
undifferentiated timeout.

## Cleanup and recovery

Managed containers carry WatchWolfX labels so ServerManager can identify and recover or clean up
instances after failures and restarts. Cleanup operations are designed to be idempotent across:

- failed container creation;
- failed startup or readiness timeout;
- normal completion;
- ServerManager restart;
- orphan discovery.

Use `WATCHWOLF_MINECRAFT_RUNTIME=legacy` only when a test requires behavior that the itzg path does
not yet preserve.
