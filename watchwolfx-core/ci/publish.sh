#!/bin/bash
set -euo pipefail

if [[ $# -gt 0 ]]; then
    echo "Usage: $0" >&2
    exit 1
fi

: "${GITHUB_ACTOR:?Set GITHUB_ACTOR before publishing}"
: "${GITHUB_TOKEN:?Set GITHUB_TOKEN before publishing}"

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname "$script_path")
local_gradle_cache="$HOME/.gradle"
mkdir -p "$local_gradle_cache"

echo "[v] Building, testing, and publishing WatchWolfX Core..."
docker run --rm \
    --volume "$base_path:/compile" \
    --volume "$local_gradle_cache:/gradle-cache" \
    --env GRADLE_USER_HOME=/gradle-cache \
    --env GITHUB_ACTOR \
    --env GITHUB_TOKEN \
    --env GITHUB_REPOSITORY \
    --workdir /compile \
    gradle:9.7.1-jdk17 \
    gradle --no-daemon clean check assemble publish
