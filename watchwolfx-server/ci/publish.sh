#!/bin/bash
set -euo pipefail

if [[ $# -ne 0 || -z ${GITHUB_ACTOR:-} || -z ${GITHUB_TOKEN:-} ]]; then
    echo "Usage: GITHUB_ACTOR=<user> GITHUB_TOKEN=<token> $0" >&2
    exit 1
fi

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
"$(dirname "$script_path")/gradlew" publishPluginPublicationToGitHubPackagesRepository
