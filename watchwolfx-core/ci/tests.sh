#!/bin/bash
set -euo pipefail

unit=0
integration=0
test_match=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --unit) unit=1 ;;
        --integration) integration=1 ;;
        --tests) test_match=${2:?"[e] --tests requires a pattern"}; shift ;;
        *) echo "[e] Unknown parameter passed: $1" >&2; exit 1 ;;
    esac
    shift
done

if [[ $integration -eq 0 && $unit -eq 0 ]]; then
    echo "[e] You must specify at least one type of test to run!" >&2
    exit 1
fi

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname "$script_path")
local_gradle_cache="$HOME/.gradle"
mkdir -p "$local_gradle_cache"

tasks=(clean)
if [[ $unit -eq 1 ]]; then tasks+=(test); fi
if [[ $integration -eq 1 ]]; then tasks+=(integrationTest); fi

filter_args=()
if [[ -n "$test_match" ]]; then
    echo "[v] Running filtered tests: $test_match"
    filter_args=(--tests "$test_match")
fi

docker run --rm \
    --volume "$base_path:/compile" \
    --volume "$local_gradle_cache:/gradle-cache" \
    --env GRADLE_USER_HOME=/gradle-cache \
    --workdir /compile \
    gradle:9.7.1-jdk17 \
    gradle --no-daemon "${tasks[@]}" "${filter_args[@]}"
