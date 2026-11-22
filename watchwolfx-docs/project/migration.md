================================================================================
WATCHWOLF → WATCHWOLFX MIGRATION PLAN (0.5.0)
================================================================================

1. NAMING / RENAMING
--------------------------------------------------------------------------------
  Upstream          →  Your Fork
  --------------------------------------------------------------------------
  WatchWolf         →  WatchWolfX
  WatchWolf Core    →  WatchWolfX Core
  WatchWolf Server  →  WatchWolfX Server
  WatchWolf ServerManager → WatchWolfX ServerManager
  WatchWolf Tester  →  WatchWolfX Tester

  - Hard fork; independently maintained with own bias
  - State clearly in docs: "No affiliation with upstream WatchWolf"
  - Link upstream so users can choose

2. VERSIONING STRATEGY
--------------------------------------------------------------------------------
  - 0.4.x  → stays as-is under OLD names (don't retro-rename published artifacts)
  - 0.5.0  → cut-over release with new WatchWolfX naming
  - Optional: one final 0.4.x release logging a deprecation warning
  - Consider whether to rename Java packages (dev.watchwolf → dev.watchwolfx)
    → RECOMMENDATION: skip package renames in 0.5.0 to limit downstream breakage

3. REPOSITORY STRUCTURE — MONOREPO (RECOMMENDED)
--------------------------------------------------------------------------------
  - Do NOT use Git submodules (complex, breaks unified release, poor JitPack fit)
  - Merge everything into ONE repo, starting from the main "watchwolf" repo
  - Move code from the other 4 repos in, then archive them

  watchwolfx/ (repo root)
  ├── settings.gradle.kts        # declare all submodules
  ├── build.gradle.kts           # unified version, shared publish logic (subprojects{})
  ├── .github/workflows/         # CI/CD: packages + Docker
  ├── docs/                      # 0.5.0 docs / migration page
  ├── watchwolfx-core/
  ├── watchwolfx-server/         # depends on core
  ├── watchwolfx-servermanager/
  ├── watchwolfx-tester/
  └── watchwolfx-clientsmanager/

4. PUBLISHING — GITHUB PACKAGES (Gradle/Maven)
--------------------------------------------------------------------------------
  - Apply maven-publish per module (or via subprojects block in root)
  - Repo: maven.pkg.github.com/OWNER/REPOSITORY
  - Auth: Personal Access Token (classic) w/ write:packages
    (GITHUB_TOKEN only works in Actions with packages:write permission)
  - Published artifacts independent per module, e.g.:
      com.github.YourUsername.WatchWolfX:watchwolfx-core:0.5.0
  - JitPack supports multi-module natively (alternative/complement to GH Packages)
      com.github.YourUsername:WatchWolfX:ModuleName:0.5.0
  - After first publish, packages default to PRIVATE → set visibility manually

5. PUBLISHING — DOCKER IMAGES (GHCR)
--------------------------------------------------------------------------------
  - Registry: ghcr.io/NAMESPACE/IMAGE_NAME:tag
  - Manual: docker login ghcr.io (PAT as password) → docker push
  - Automated (recommended): GitHub Actions
      docker/login-action  (password: ${{ secrets.GITHUB_TOKEN }})
      docker/build-push-action
      permissions: packages: write
  - Tags: 0.5.0, latest, commit SHA (keep in sync w/ package versions)
  - Use path filters (e.g. only build on watchwolfx-server/** changes)

6. DOCS PAGE (ship with 0.5.0)
--------------------------------------------------------------------------------
  1. What changed — old→new mapping table (repos + artifacts)
  2. Why — hard fork, independent maintenance
  3. Migration steps — dependency coordinates, package/import changes,
     config path changes (if any)
  4. Compatibility note — no affiliation w/ upstream, link upstream
  5. Version matrix — WatchWolfX version ↔ upstream baseline
  - Tell users: replace 5 old repo coords w/ 1 repo + module name

7. GOTCHAS / CHECKLIST
--------------------------------------------------------------------------------
  [ ] GitHub repo renames preserve redirects (old URLs won't 404)
  [ ] Update CI, badges, JitPack triggers to new names
  [ ] ServerManager: decide if 0.5.0 migrates old config/data or starts fresh
  [ ] Watch inter-module dependency versions (Server needs Core 0.5.0, etc.)
  [ ] Pin docs to NEW names; let redirects handle stragglers
  [ ] Keep 0.4.x artifacts untouched to avoid breaking existing builds

8. EXECUTION ORDER
--------------------------------------------------------------------------------
  1. Copy/move code from 5 old repos into monorepo subdirectories
  2. Configure settings.gradle.kts + root build.gradle.kts (unified version)
  3. Apply maven-publish per module
  4. Set up .github/workflows (packages + Docker, path-filtered)
  5. Verify JitPack/GH Packages multi-module publish with a 0.5.0 tag
  6. Write & publish docs page
  7. Archive the 4 old sub-repos (keep main as starting point)
================================================================================