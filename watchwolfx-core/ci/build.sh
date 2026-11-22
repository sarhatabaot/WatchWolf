#!/bin/bash
set -euo pipefail

preclean=0
while [[ $# -gt 0 ]]; do
    case $1 in
        --preclean) preclean=1 ;;
        *) echo "[e] Unknown parameter passed: $1" >&2; exit 1 ;;
    esac
    shift
done

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname "$script_path")
local_gradle_cache="$HOME/.gradle"
mkdir -p "$local_gradle_cache"

tasks=(assemble)
if [[ $preclean -eq 1 ]]; then tasks=(clean assemble); fi

echo "[v] Building WatchWolfX Core..."
docker run --rm \
    --volume "$base_path:/compile" \
    --volume "$local_gradle_cache:/gradle-cache" \
    --env GRADLE_USER_HOME=/gradle-cache \
    --workdir /compile \
    gradle:9.7.1-jdk17 \
    gradle --no-daemon "${tasks[@]}" -x test
