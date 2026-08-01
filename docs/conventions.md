# docs/conventions.md — Coding Conventions

> Binding for all code in this repo. Amendments by PR; the review gate checks adherence. Owners enforce per-directory specifics on top of these (AGENTS.md §2).

## 1. Kotlin (all Android modules)

- **Formatting:** ktlint-enforced. No manual formatting debate — run `ktlint` (gradle `ktlintCheck` from Phase 1). 4-space indent, no trailing whitespace, newline at EOF.
- **Lint:** detekt (config committed in Phase 1). Treat warnings as errors in CI.
- **Visibility:** default to `internal` at module boundaries. Only `public` what other modules genuinely consume. Public API is documentation — KDoc it.
- **Imports:** no wildcard imports (`import foo.*` forbidden). Ordering per ktlint.
- **Nullability:** explicit. No `!!` in production code (review failure). `?.`/`?:` preferred; exceptions only at process boundaries.
- **Serialization:** kotlinx.serialization for every model that touches config, IPC, or files. Never hand-roll JSON.
- **Coroutines:** structured concurrency; `GlobalScope` forbidden; scopes owned by lifecycle owners. IO dispatchers only for blocking ops (and the config lib encapsulates all file IO anyway).
- **Composition over inheritance:** interfaces + delegation (`by`), sealed hierarchies for state models, no deep class hierarchies. Compose components are stateless where possible; state hoisted via `rememberSaveable`/viewmodels.

## 2. Jetpack Compose

- **Tokens only.** Colors, blur, elevation, type, spacing, motion come from `libs/design` tokens. Zero magic numbers in composables — a raw `dp` or `Color` literal is a review failure (ADR-0011).
- **State hoisting:** stateless composables + explicit parameters; state owners above; `@Stable` models; stable lambdas (`remember` where capture set is fixed).
- **Performance discipline** (ADR-0003): lazy layouts for any list; no blur layers off-screen; `Modifier.drawBehind`/Canvas for custom glass effects; avoid recomposition storms (keys, `derivedStateOf`, `snapshotFlow` for UI state).
- **Accessibility:** contentDescriptions, focus order, touch targets ≥ 48dp, contrast AA where tokens allow.
- **Preview/parity:** every new screen ships with a screenshot test (Roborazzi) — the preview is the test.

## 3. Schema (libs/schema)

- Every shared shape is authored first as JSON Schema (`libs/schema/definitions/*.json`).
- Generated artifacts (Kotlin in `libs/config`, TS in `webui/src/generated`) are **committed** and CI verifies freshness — never hand-edit generated files.
- Additive schema changes must be backward-compatible; breaking changes require a schema version bump + migration in `libs/config` (versioned in the store root).

## 4. TypeScript / WebUI

- Strict mode; no `any` (explicit `unknown` + narrowing); eslint + prettier enforced.
- Types come from zod contracts generated from the schema — hand-written duplicate types are a review failure.
- React 19 + Vite conventions; components in `webui/src/components/`; state via the app's chosen pattern (decided in Phase 6).
- No CSS-in-JS unless the design system demands; tokens flow from the same JSON Schema as Android where possible.

## 5. Hooks (LSPosed)

- One surface = one module; hooks never import launcher/companion code (ADR-0007).
- Hook code targets the pinned firmware only (ADR-0002); Moto-specific classes live in an explicit adapter class per seam interface.
- No Compose in hooks. No UI in hooks. Hooks emit events; hosts render.
- All reflection/dex accesses go through `hooks-common`; no scattered raw reflection.

## 6. Shell / provisioning (magisk, tools)

- POSIX sh for Magisk module scripts (busybox-compatible); no bash-isms in module scripts.
- Scripts are idempotent; flash/verify scripts print PASS/FAIL lines for Tier 3.
- No `sudo` inside scripts — provisioning runs as root by construction.

## 7. Commits & branches

- Conventional Commits: `feat:` `fix:` `refactor:` `chore:` `docs:` `test:` `perf:` `build:` — scope optional but preferred (`feat(spotlight): rank by usage`).
- One logical change per commit; no unrelated reformatting in the same commit.
- Branch naming: `phase/<phase>/<short-desc>` (e.g., `phase/2/widget-tinting`).

## 8. Files & naming

- Files: PascalCase for Kotlin types, lowercase for TS files (kebab-case for CSS), snake_case for JSON schema files, kebab-case for scripts.
- Packages: `dev.ios26.<context>` (e.g., `dev.ios26.springboard`); `internal` sub-packages for implementation details — never imported across modules.

## 9. Dependencies

- Everything through the version catalog (`gradle/libs.versions.toml`); no inline versions.
- New dependency = review + documented reason (license, size, maintenance). GPL-incompatible licenses are excluded (ADR-0014).
- No third-party analytics/crash SDKs, ever (ADR-0018).
