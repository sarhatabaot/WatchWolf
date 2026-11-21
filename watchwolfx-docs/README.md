# WatchWolfX documentation

This module owns the WatchWolfX website/documentation sources, protocol specification and API
documentation tooling.

- [`API/API.tex`](API/API.tex) is the protocol source of truth; [`API/API.pdf`](API/API.pdf) is its
  checked-in rendered form.
- [`API/definitions`](API/definitions/) contains machine-readable protocol definitions and
  generated reference material.
- [`api-docs.py`](api-docs.py) and [`api-docs.Dockerfile`](api-docs.Dockerfile) generate API docs.
- [`migration.md`](migration.md) records the WatchWolf-to-WatchWolfX 0.5.0 migration plan.
- [`engineering/runtime-inventory.md`](engineering/runtime-inventory.md) records the discovered
  provisioning and test execution path.
- [`current-work.md`](current-work.md) records active migration state and handoff information.
- [`engineering/import-manifest.md`](engineering/import-manifest.md) pins the source revisions used
  for the initial monorepo import.
- [`legacy-standard-readme.md`](legacy-standard-readme.md) preserves the pre-monorepo standard
  repository guide while its useful material is split into current WatchWolfX documentation.

Run the API documentation container with this directory as its build context so the Dockerfile's
relative paths continue to resolve. The builder uses Wolfi with pinned Python requirements and a
locked `bytefield-svg` graph under `node/`.
