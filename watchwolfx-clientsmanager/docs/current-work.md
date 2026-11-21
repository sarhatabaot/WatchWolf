# Current work

## Python/uv modernization

- Status: complete and ready for review on the monorepo `watchwolfx-0.5.0` branch.
- Scope: installable package layout, uv dependency management, quality checks and reproducible
  container entry point without changing WatchWolf protocol behavior.
- Verified: locked dependency resolution, Ruff lint/format, BasedPyright, five pytest tests,
  source/wheel builds, packaged `items.json`, and Wolfi Docker image build as `nonroot`.
- Completed: Mineflayer and Pathfinder are pinned in `node/package-lock.json`; the unused viewer
  graph was removed, reducing npm audit results from 14 total (including high/critical) to six
  moderate upstream-blocked findings.
- Follow-up: re-evaluate the moderate authentication advisories on Mineflayer upgrades and design
  recording as an explicit optional image/profile if it is still wanted.
