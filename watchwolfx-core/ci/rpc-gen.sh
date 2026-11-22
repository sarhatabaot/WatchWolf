#!/bin/bash
set -euo pipefail

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname "$script_path")
local_gradle_cache="$HOME/.gradle"
generated_stub="$base_path/src/main/java/dev/watchwolf/core/rpc/stubs/serversmanager/ServersManagerLocalStub.java"
mkdir -p "$local_gradle_cache"

rm -f "$generated_stub"
docker run --rm \
    --volume "$base_path:/compile" \
    --volume "$local_gradle_cache:/gradle-cache" \
    --env GRADLE_USER_HOME=/gradle-cache \
    --workdir /compile \
    gradle:9.7.1-jdk17 \
    gradle --no-daemon clean generateRpcStubs
