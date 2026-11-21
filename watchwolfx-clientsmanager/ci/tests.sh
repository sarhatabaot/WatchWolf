#!/usr/bin/env bash
set -euo pipefail

script_path=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
base_path=$(dirname -- "$script_path")
test_pattern=""

while [[ $# -gt 0 ]]; do
	case "$1" in
		--unit)
			;;
		--tests)
			if [[ $# -lt 2 ]]; then
				echo "[e] --tests requires a unittest filename pattern." >&2
				exit 2
			fi
			test_pattern="$2"
			shift
			;;
		*)
			echo "[e] Unknown parameter: $1" >&2
			exit 2
			;;
	esac
	shift
done

cd "$base_path"

echo "[v] Syncing the locked WatchWolfX ClientsManager environment..."
uv sync --locked

echo "[v] Verifying the locked Node runtime..."
npm ci --ignore-scripts --prefix node
npm audit --omit=dev --audit-level=high --prefix node

echo "[v] Running Ruff..."
uv run ruff check .
uv run ruff format --check .

echo "[v] Running BasedPyright..."
uv run basedpyright

echo "[v] Running pytest..."
if [ -n "$test_pattern" ]; then
	uv run pytest -q "$test_pattern"
else
	uv run pytest -q
fi

echo "[i] Unit tests passed"
