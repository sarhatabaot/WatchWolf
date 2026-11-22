#!/usr/bin/env bash
set -euo pipefail

release_tag="${1:-}"
modules=(
  watchwolfx-core
  watchwolfx-server
  watchwolfx-servermanager
  watchwolfx-tester
)

if [[ -n "$release_tag" && "$release_tag" != v* ]]; then
  echo "Release tag must start with 'v': $release_tag" >&2
  exit 1
fi

project_version=""

for module in "${modules[@]}"; do
  wrapper="$module/gradlew"
  wrapper_jar="$module/gradle/wrapper/gradle-wrapper.jar"
  wrapper_properties="$module/gradle/wrapper/gradle-wrapper.properties"

  for required_file in "$wrapper" "$wrapper_jar" "$wrapper_properties"; do
    if [[ ! -s "$required_file" ]]; then
      echo "Missing required Gradle wrapper file: $required_file" >&2
      exit 1
    fi

    if ! git ls-files --error-unmatch "$required_file" >/dev/null 2>&1; then
      echo "Gradle wrapper file is not tracked by Git: $required_file" >&2
      exit 1
    fi
  done

  module_version=$(bash "./$wrapper" -q -p "$module" properties | awk '/^version: / { print $2 }')
  if [[ -z "$module_version" ]]; then
    echo "Unable to read Gradle project version for $module" >&2
    exit 1
  fi

  if [[ -z "$project_version" ]]; then
    project_version="$module_version"
  elif [[ "$module_version" != "$project_version" ]]; then
    echo "$module uses version $module_version; expected $project_version" >&2
    exit 1
  fi
done

clients_version=$(python -c 'import tomllib; print(tomllib.load(open("watchwolfx-clientsmanager/pyproject.toml", "rb"))["project"]["version"])')
if [[ "$clients_version" != "$project_version" ]]; then
  echo "watchwolfx-clientsmanager uses version $clients_version; expected $project_version" >&2
  exit 1
fi

if [[ -n "$release_tag" && "${release_tag#v}" != "$project_version" ]]; then
  echo "Release tag $release_tag does not match project version $project_version" >&2
  exit 1
fi

if [[ -n "${GITHUB_ENV:-}" ]]; then
  echo "PROJECT_VERSION=$project_version" >> "$GITHUB_ENV"
fi

echo "Release preflight passed for WatchWolfX $project_version"
