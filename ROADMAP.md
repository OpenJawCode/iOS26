# ROADMAP.md — Milestone Roadmap

> Status: **Phase 0 in progress** (started 2026-08-01). Phases are sequential; exit criteria must be met before the next phase opens. Every phase has an owner (AGENTS.md §2) and updates CONTEXT.md + ADRs as it learns.

## Phase 0 — Discovery *(current)*

**Goal:** Reduce uncertainty on a rooted Edge 20 before committing architecture details. No code.

| Deliverable | Owner | Exit criterion |
|---|---|---|
| Device baseline: firmware/security patch level, Magisk + Zygisk version, LSPosed version, root state | provisioning-owner | Versions pinned and recorded in CONTEXT.md §3 |
| My UX SystemUI hook-point survey for the top-right swipe intercept | hooks-owner | Concrete class/method map + feasibility verdict (feeds ADR-0005) |
| Control Center overlay-spike: overlay window + gesture-signal + FileObserver round-trip on device | control-center-owner | Spike repo note: latency, animation quality, fallback verified (ADR-0019) |
| iOS 27 design delta research | design-owner | Token-delta notes (feeds ADR-0011) |
| Widget tinting reality check on API 33 | widgets-owner | Which widgets tint vs need framing (feeds ADR-0015) |
| Toolchain pinning: AGP / Kotlin / Compose BOM / Gradle / JDK | build-owner | Version catalog draft committed (Phase 1 input) |
| Risk register + open-questions resolution (CONTEXT.md §3) | all | Every CONTEXT.md §3 item resolved or explicitly deferred with reason |

**Exit:** every open question answered or deferred with a named owner; risk register reviewed by the user.

## Phase 1 — Architecture & Toolchain

**Goal:** A green, bootstrapped monorepo with the deep modules in place.

- Gradle wrapper + version catalog; settings wiring `:launcher/* :hooks/* :libs/* :companion/*`; `libs/schema` codegen pipeline (JSON Schema → Kotlin, and → TS via npm workspace); `libs/config` skeleton with full Tier 1 tests.
- CI armed: unit + lint + AVD + WebUI jobs (docs/ci.md), release workflow scaffolded.
- ADR finalization pass; repository conventions docs ratified.

**Exit:** `./gradlew build` green on an empty-but-wired build; CI green on a PR; codegen freshness check in CI.

## Phase 2 — Launcher

**Goal:** Springboard, App Library, Spotlight, widgets — installable on the device as a systemless priv-app.

- Springboard: grid, pages, dock, folders, jiggle-edit, context menus, page dots, wallpaper integration.
- App Library: category data layer (bundled localized mapping + per-app overrides).
- Spotlight: apps + contacts + app actions, offline ranking.
- Widget host: AppWidgetHost, iOS-style slots, tinting/framing, iOS-idiom add-widget flow.
- Design system v1: token sets, Liquid Glass component kit, light/dark.
- Every screen screenshot-tested (Roborazzi); Core flows Compose-UI-tested.

**Exit:** launcher usable as daily driver on the Edge 20; fallback (hook-less) mode clean.

## Phase 3 — LSPosed Framework

**Goal:** The hook seam, proven end-to-end with one surface.

- `hooks-api` seam contract + file-event bus (ADR-0019); `hooks-common` shared infra.
- `hooks/control-center`: Moto SystemUI gesture adapter per Phase 0 survey.
- Graceful degradation verified: hook off → launcher-only mode; hook crash → nothing else affected.
- Provisioning path for hook install + enable.

**Exit:** top-right swipe in *any* app raises the panel; Tier 3 script green; fallback verified.

## Phase 4 — Magisk Modules

**Goal:** One-flash install experience.

- `ios26-stack` module: systemless priv-app install, system overlays, config-store bootstrap (perms, defaults), optional hook auto-provisioning; update flow (`update.json`).
- Overlays: SystemUI + framework RROs with token-driven resources.

**Exit:** factory-reset-to-working single flash, documented and scripted (Tier 3).

## Phase 5 — Companion App

**Goal:** The settings hub + embedded server.

- Settings surfaces: theme/token sets, icon packs, wallpaper, module toggles, diagnostics (hook health, config validation), backup/restore.
- Personal asset import pipeline (on-device; never in repo).
- Ktor server: auth (one-time token), config read/write via `libs/config`, WebUI static hosting.

**Exit:** every config shape editable from companion; backup round-trip tested; server secured on LAN.

## Phase 6 — WebUI

**Goal:** Big-screen control.

- Layout editor (drag-drop springboard arrangement), theme presets, wallpaper gallery, import/export, diagnostics readout.
- zod contracts generated from `libs/schema`; Playwright suite against the companion server.

**Exit:** end-to-end layout edit → applied on device, Playwright green.

## Phase 7 — System Polish

**Goal:** Feel, not just function.

- Animation/motion system (springs, blur transitions), haptics, sound design (original assets), empty/error states, icon edit-mode completeness, accessibility pass (labels, focus, contrast, touch targets).

**Exit:** accessibility audit pass; motion consistent across surfaces.

## Phase 8 — Performance

**Goal:** Budgets, enforced.

- Set budgets: frame cadence, cold start, blur budget (RenderEffect), memory ceiling on the 778G.
- Macrobenchmark baselines + Perfetto analysis (perfetto skills) on device; regressions fail CI.

**Exit:** budgets met on device; CI enforces them.

## Phase 9 — Testing

**Goal:** The full matrix, wired.

- Tier 3 harness CI integration (self-hosted device runner, optional), coverage reports, baseline management, dependency-graph enforcement in CI (ARCHITECTURE.md §3.1).

**Exit:** full tier matrix green; coverage + budgets enforced; docs accurate.

## Phase 10 — Release

**Goal:** The first public release train.

- Versioning, tags, changelog, GitHub release with module zip + APKs, install guide, community on-ramp (CONTRIBUTING verified by a stranger).

**Exit:** v1.0.0 tag with documented, reproducible install; Phase 1–9 docs match reality.

---

## Cross-phase invariants

- **Decision gate:** no architectural change without an ADR; no guessing (AGENTS.md §0).
- **Docs parity:** docs always describe reality at phase exit.
- **Single release train** (ADR-0004): version bumps that cross components land together.
- **No Apple IP** (ADR-0012): enforced from Phase 2 onward (asset reviews in every gate).
