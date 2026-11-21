#!/bin/bash
set -euo pipefail

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname "$script_path")

if [[ $# -eq 1 && $1 == --preclean ]]; then
    "$base_path/gradlew" clean
elif [[ $# -ne 0 ]]; then
    echo "Usage: $0 [--preclean]" >&2
    exit 1
fi

"$base_path/gradlew" assemble
