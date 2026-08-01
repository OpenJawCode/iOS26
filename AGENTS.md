# AGENTS.md — Project Intelligence for AI Agents

> This repository is built for human + AI-agent collaboration. If you are an agent, read this file, then ARCHITECTURE.md and CONTEXT.md before touching anything. If you are a human, keep this file accurate — agents depend on it.

## 0. Ground rules

1. **Decisions before code.** The project is decision-gated (see ADRs). If any task would require guessing on an *architectural* point not covered by `docs/adr/` or CONTEXT.md, **stop, and ask the user one question at a time** until the uncertainty is resolved. Never guess architecture. Feature-level details you may decide, but say so.
2. **The config store is the spine** (ADR-0006). Cross-component state travels only through `libs/config`. Never wire two components directly.
3. **Thin hooks** (ADR-0005). The LSPosed surface stays minimal; everything testable lives in normal app code.
4. **Tokens, not hardcoded styles** (ADR-0011). UI never hardcodes color/blur/type — it renders through design tokens.
5. **No Apple IP** (ADR-0012). Never copy Apple assets, fonts, sounds, or artwork into this repo.
6. **Single release train** (ADR-0004). Version changes that cross components land together.
7. **Never commit secrets.** The pre-commit hook enforces this. `.env` files are for your machine only.
8. **Small surgical diffs.** Prefer narrow edits over rewrites. Never reformat files you aren't changing.
9. **Log mistakes.** Wrong approach? Append to the project mistake log (see §6) so the team doesn't repeat it.

## 1. First moves (agents)

1. Read `ARCHITECTURE.md` → `CONTEXT.md` → the ADR that covers your task (index in CONTEXT.md §2).
2. Read the `README.md` of your target directory (ownership, conventions, test command).
3. Load the matching skill (see §4) — skills are SOPs; follow them literally.
4. Identify which test tier covers your work (§5) and run it before finishing.

## 2. Directory ownership

Each directory has a **designated owner agent** and a **domain specialist**. Ownership means: you are responsible for that directory's conventions, tests, and architecture quality; changes elsewhere that touch your directory must not break its invariants.

| Directory | Owner agent | Domain | Skills |
|---|---|---|---|
| `libs/config` + `libs/schema` | config-owner | The deep module — change it only through the ADR process; its API is sacred | testing-setup, context7 |
| `libs/design` + `assets/` | design-owner | Tokens, glass, theming | styles, adaptive |
| `libs/domain` + `libs/icons` | domain-owner | Pure models, icon mapping | — |
| `launcher/springboard` | springboard-owner | Grid, pages, dock, folders, jiggle | adaptive, styles |
| `launcher/app-library` | app-library-owner | Categories, mapping data layer | — |
| `launcher/spotlight` | spotlight-owner | Search, ranking | — |
| `launcher/widgets` | widgets-owner | AppWidgetHost, tinting | — |
| `launcher/control-center` | control-center-owner | Overlay host, panel | edge-to-edge, adaptive |
| `hooks/*` | hooks-owner | LSPosed layer, Moto adapters, seam | android-intent-security |
| `magisk/` + `overlays/` | provisioning-owner | Install, bootstrap, RROs | android-cli |
| `companion/*` | companion-owner | Settings hub, server, import | navigation-3, android-intent-security |
| `webui/` | webui-owner | SPA, layout editor | gha |
| `device-tests/` + `tools/` | qa-owner | Tier-3 harness, tooling | perfetto-trace-analysis, perfetto-sql |
| `docs/` | doc-owner | ADRs, glossary, standards | (write docs per §6) |

## 3. Conventions (summary — details in `docs/conventions.md`)

- **Kotlin:** ktlint + detekt clean; explicit visibility (`internal` by default across modules); no wildcard imports; kotlinx.serialization for models; Compose: state hoisting, stable lambdas, tokens only, no magic numbers.
- **TypeScript (webui):** strict mode, eslint + prettier, zod types generated from `libs/schema` — never hand-written.
- **Schema:** every shared shape originates in `libs/schema` JSON Schema; generated Kotlin/TS are committed; CI checks freshness.
- **Commits:** conventional — `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`, `test:`, `perf:`, `build:`. One logical change per commit.
- **Docs:** any behavior change that alters a term, a decision, or an API updates CONTEXT.md and/or an ADR in the same PR.

## 4. Skill routing

Skills live in the workspace skill store (OpenCode: `.agents/skills`, loaded at session start). Load the skill whose description matches your task *before* acting — skills are SOPs:

| Task | Skill |
|---|---|
| Android CLI / emulator / adb / screenshots / SDK | `android-cli` |
| Compose theming, tokens, styles API | `styles` |
| Adaptive/responsive Compose UI | `adaptive` |
| Migrating XML views → Compose | `migrate-xml-views-to-jetpack-compose` |
| AGP upgrades / build system | `agp-9-upgrade` |
| Edge-to-edge / insets handling | `edge-to-edge` |
| Testing strategy / test setup | `testing-setup` |
| R8 / shrinking / keep rules | `r8-analyzer` |
| Jetpack Navigation 3 (companion app) | `navigation-3` |
| Intent / IPC security review | `android-intent-security` |
| Perfetto trace analysis (Phase 8) | `perfetto-trace-analysis`, `perfetto-sql` |
| GitHub Actions workflow work | `gha` |
| Library/API docs lookup | `context7` (or `find-docs`) |
| TDD for a new behavior | `tdd` (red-green-refactor) |
| Reviewing a diff/branch | `code-review` |
| Debugging a hard bug | `diagnosing-bugs` |
| Planning a big effort | `wayfinder` / `grill-with-docs` |

## 5. Testing tiers (details in `docs/testing.md`)

| Tier | Scope | Where it runs |
|---|---|---|
| 1 | Unit — config, schema, domain, hooks-common logic | CI, fast |
| 2 | UI — Roborazzi screenshots, Compose UI tests, WebUI Playwright | CI (AVD API 33 + node) |
| 3 | Device-gated — hooks against My UX SystemUI, overlays, Magisk flash | Physical Edge 20 via `device-tests/` harness; documented, scripted, pass/fail output |

**Always run Tier 1 + 2 for your change before handing off.** Tier 3 runs when the device is wired (scripts are idempotent).

## 6. Documentation & logs

- `CONTEXT.md` — glossary + decision log. Update on term/decision changes.
- `docs/adr/` — new ADR for every significant decision (template in `docs/adr/README.md`). Statuses: Proposed → Accepted → Superseded.
- **Mistake log** (`docs/logs/mistakes.md`, gitignored) — wrong approaches, why, and the fix. Append, never rewrite.
- **Bug log** (`docs/logs/bugs.md`, gitignored) — every bug with reproduction steps, resolution, regression-test status.

## 7. Handoff protocol

When a session's context is exhausted mid-task: write a handoff note under `docs/logs/handoffs/` (state, decisions taken, next actions, open questions) and tell the user. The next session starts from the handoff.

## 8. Quality gate before any commit

1. Tier 1 + 2 tests green for the change.
2. ktlint/detekt clean (Kotlin), eslint/tsc clean (TS).
3. Docs updated if terms/decisions/APIs changed.
4. No secrets, no Apple IP, no unrelated reformatting.
5. Conventional commit message.
