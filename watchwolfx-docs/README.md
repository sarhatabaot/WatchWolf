# WatchWolfX documentation

This module owns the WatchWolfX website/documentation sources, protocol specification and API
documentation tooling.

The documentation is built with the pinned Zensical version in `pages-requirements.txt` and
deployed to <http://sarhatabaot.net/WatchWolfX/> by `.github/workflows/docs-pages.yml` after
changes reach `main`. Site structure, navigation, search, theme, and exclusions are defined in the
repository-root `zensical.toml`.

Preview the site locally:

```bash
python -m pip install --requirement watchwolfx-docs/pages-requirements.txt
zensical serve
```

Run the same strict build used by CI:

```bash
zensical build --clean --strict
```

## Site structure

- `index.md` is the public landing page.
- `user-guide/` contains installation, configuration, and troubleshooting material.
- `concepts/` describes architecture, runtime providers, and the protocol boundary.
- `contributing/` contains development and release workflows.
- `project/` contains migration status, architecture decisions, and engineering records.
- `stylesheets/` contains site-specific presentation rules.

Protocol-generation sources remain at the documentation root because their scripts and container build use that
layout.

## Protocol and project records

- [`API/API.tex`](API/API.tex) is the protocol source of truth; [`API/API.pdf`](API/API.pdf) is its
  checked-in rendered form.
- [`API/definitions`](API/definitions/) contains machine-readable protocol definitions and
  generated reference material.
- [`api-docs.py`](api-docs.py) and [`api-docs.Dockerfile`](api-docs.Dockerfile) generate API docs.
- [`project/migration.md`](project/migration.md) records the WatchWolf-to-WatchWolfX 0.5.0 migration plan.
- [`project/changes-from-watchwolf.md`](project/changes-from-watchwolf.md) documents the divergent
  release history beginning with 0.4.0.
- [`project/engineering/runtime-inventory.md`](project/engineering/runtime-inventory.md) records the discovered
  provisioning and test execution path.
- [`project/current-work.md`](project/current-work.md) records active migration state and handoff information.
- [`project/engineering/import-manifest.md`](project/engineering/import-manifest.md) pins the source revisions used
  for the initial monorepo import.
- [`legacy-standard-readme.md`](legacy-standard-readme.md) preserves the pre-monorepo standard
  repository guide while its useful material is split into current WatchWolfX documentation.

Run the API documentation container with this directory as its build context so the Dockerfile's
relative paths continue to resolve. The builder uses Wolfi with pinned Python requirements and a
locked `bytefield-svg` graph under `node/`.
