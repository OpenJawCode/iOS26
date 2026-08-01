# ARCHITECTURE.md

> System architecture for iOS26. Read this before touching any code. ADRs (`docs/adr/`) carry the reasoning behind every decision; this document describes the resulting design.

## 1. System overview

iOS26 is a stack of cooperating components that together recreate the iOS 26/27 experience on stock Motorola My UX Android 13. The stack is organized around one invariant: **all shared state flows through the schema-validated config store, and no process may depend on another process being alive.**

```
                      ┌─────────────────────────────────────────────┐
                      │              CONFIG STORE (libs/config)      │
                      │   /data/adb/ios26/ — JSON, schema-validated │
                      │   atomic writes · FileObserver live-reload  │
                      └──────▲──────────────▲───────────▲───────────┘
                             │              │           │
                 ┌───────────┴───┐   ┌──────┴─────┐   ┌─┴───────────┐
                 │  LAUNCHER     │   │ COMPANION  │   │  LSPOSED    │
                 │  (priv-app)   │   │   (app)    │   │   HOOKS     │
                 │  springboard  │   │ settings   │   │ (SystemUI   │
                 │  app library  │   │ server(Ktor│   │  process)   │
                 │  spotlight    │   │ import     │   │  gesture    │
                 │  widgets      │   │  └─ WebUI  │   │  intercept  │
                 │  cc overlay ──┼──▶│  (React)   │   │  (thin)     │
                 └───────────────┘   └────────────┘   └──────┬──────┘
                                                            │ signal
                                                 ┌──────────▼──────────┐
                                                 │   HOOK SEAM         │
                                                 │ hooks-api (contract)│
                                                 │ file-event bus      │
                                                 └─────────────────────┘
```

## 2. Bounded contexts

The system decomposes into 12 bounded contexts. Each context owns its domain language and its persistence; contexts communicate only through defined ports.

| # | Context | Modules | Responsibilities |
|---|---|---|---|
| 1 | **Configuration** | `libs/config`, `libs/schema` | The deep module. Schema-validated file store: read, parse, validate, atomic write, live reload, event files. **Depends on nothing**; everything depends on it. |
| 2 | **Design System** | `libs/design`, `assets/` | Design tokens, Liquid Glass component kit, theming engine. No business logic. |
| 3 | **Domain** | `libs/domain`, `libs/icons` | Pure models: apps, categories, layouts, theme, icon mapping. No Android dependencies where possible. |
| 4 | **Springboard** | `launcher/springboard` | Workspace: icon grid, pages, dock, folders, jiggle-edit, context menus, page-dots. |
| 5 | **App Library** | `launcher/app-library` | Auto-categorized app grid; category data layer (bundled mapping + overrides). |
| 6 | **Spotlight** | `launcher/spotlight` | Search: apps, contacts, app actions; indexing and ranking. Offline. |
| 7 | **Widget Host** | `launcher/widgets` | AppWidgetHost lifecycle, slot sizing, platform tinting, glass framing, add-widget flow. |
| 8 | **Control Center** | `launcher/control-center` | Overlay window service + panel UI: toggles, brightness/volume, media, shortcuts. Consumes hook events; falls back to launcher-only mode. |
| 9 | **System Hook** | `hooks/*` | LSPosed layer. `hooks-api` = seam contract; `hooks-common` = shared hook infra (embedded per module); `hooks/control-center` = Moto SystemUI gesture adapter. |
| 10 | **Settings** | `companion/app`, `companion/import` | Settings hub: theme, icon packs, wallpaper, backup/restore, module toggles, diagnostics, personal asset import. |
| 11 | **WebUI** | `webui/`, `companion/server` | Layout editor, theme presets, wallpaper gallery, import/export, diagnostics readout. Served by the companion's embedded Ktor server; auth via one-time token. |
| 12 | **Provisioning** | `magisk/ios26-stack`, `overlays/*` | Install-time: systemless priv-app install, system RROs, config-store bootstrap, hook provisioning, update flow. |

## 3. Module boundaries & dependency rules

Module coordinates mirror directory paths (`:launcher:springboard`, `:hooks:control-center`, …).

### 3.1 Hard rules

1. **Strict acyclic dependency graph** — enforced by Gradle module boundaries (and, from Phase 9, by dependency-cruiser-style checks in CI). No module may depend on another module's `internal` implementation package.
2. **The config store is the only cross-component channel.** Launcher ↔ Companion ↔ Hooks never import each other's code. They exchange state exclusively through `libs/config` (values + event files).
3. **`libs/config` has zero dependencies** (beyond Kotlin stdlib + kotlinx.serialization + the generated schema model). It is the deep module: a small interface, a lot of behavior behind it.
4. **`hooks-api` is the only contract between the hook layer and the app layer.** Hooks emit typed events; hosts subscribe. No hook may reach into launcher code.
5. **UI depends on domain, never the reverse.** `libs/domain` has no Compose dependency.
6. **`libs/schema` is source-of-truth-first.** Every config shape exists first as a JSON Schema; Kotlin and TS models are generated, never hand-maintained. Generated artifacts are committed and CI verifies freshness.
7. **Process isolation.** The launcher, companion, and each hook run in separate processes. The SystemUI process (hooks) must never require any other process to be running. (ADR-0006)

### 3.2 Allowed dependency edges (summary)

```
libs/schema (defines shapes)
   ▲
libs/config  ◀──── every component
   ▲
libs/domain ◀── launcher/*, companion/*
   ▲
libs/design ◀── launcher/*, companion/*     (Compose UI only)
   ▲
hooks/hooks-api ◀── launcher/*, hooks/*
   ▲
hooks/hooks-common ◀── hooks/<surface>      (embedded at build time)
   ▲
companion/server ◀── webui/ (via HTTP API + generated TS types)
```

Magisk/overlays depend on nothing at build time; they package artifacts produced by other modules.

## 4. Key flows

### 4.1 Control Center gesture (flagship)

1. User swipes down from the top-right edge in any app.
2. **SystemUI process**: `hooks/control-center` (LSPosed, Moto adapter) intercepts the gesture — the only thing it does.
3. The hook writes a typed event file into the config store (atomic temp+rename) and returns control.
4. **Launcher process**: `launcher/control-center` observes the event (FileObserver on the config store) and raises the root-granted overlay window with the panel.
5. If the hook is disabled or broken, the overlay host falls back to in-launcher-only reachability. **Degradation is always graceful.** (ADR-0005)

### 4.2 Configuration write

1. Companion or WebUI (via `companion/server`) writes through `libs/config`'s single write API.
2. The write is validated against the JSON Schema, atomically staged, and renamed into place.
3. FileObserver fans out to every process; each component hot-reloads what it owns. No restarts.

### 4.3 Install (release train)

1. User flashes `magisk/ios26-stack` module zip in Magisk.
2. Boot script provisions: config store path/permissions → systemless priv-app (launcher) → system overlays → optional hook APK install + enable.
3. First launch: launcher bootstraps defaults (original token set, icon pack, wallpaper) into the store; companion becomes available as settings.

## 5. Composition & extensibility

- **Composition over inheritance** everywhere: Compose UI is compositional by nature; hook adapters implement `hooks-api` interfaces; widget tinting and glass framing are strategy objects.
- **New surfaces arrive as new bounded contexts**, not as edits to existing ones: a future lock screen = new `hooks/lockscreen` module + new `launcher/lockscreen` module + new schema shapes. Existing modules are untouched except for config-store additions.
- **iOS 27 is a token set**, not a code change (ADR-0011). `libs/design` renders exclusively through tokens; a new theme registers as a new token set in the store.
- **AI-agent collaboration is a first-class constraint**: module boundaries are directory-shaped, ownership is declared in `AGENTS.md`, every module has a `README.md` stating purpose + conventions, and the decision trail is explicit (ADRs + CONTEXT.md).

## 6. Non-functional commitments

| Concern | Commitment |
|---|---|
| Reliability | No process dependency for SystemUI hooks; every surface degrades gracefully to a lesser mode; hooks are isolated per surface (one failing hook can't disable others). |
| Performance | Perf budgets set in Phase 8 and enforced by macrobenchmark baselines in CI: frame cadence, cold start, blur budget (RenderEffect only where visible), memory ceiling on the 778G. |
| Security | WebUI bound to LAN with one-time token auth; no secrets in repo; intents audited (`android-intent-security` skill); no third-party analytics/crash SDKs; crash data stays on-device (ADR-0018). |
| Privacy | Everything local. No accounts, no cloud, no telemetry. Backup/restore is user-driven. |
| Maintainability | ADR-gated changes; docs-as-code; generated contracts; small deep modules with hidden internals; per-directory agent ownership. |
| Legal | GPL-3.0; zero Apple IP in the repo; all assets original or user-supplied on-device (ADR-0012). |

## 7. Known fragility (owned by the team)

1. **Moto My UX SystemUI internals** — the hook seam's only fragile surface; mitigated by isolation, version pinning to the frozen firmware, and the launcher-only fallback.
2. **Overlay-window quirks** — SYSTEM_ALERT_WINDOW / overlay type edge cases (animation overlap, input focus) — a Phase 0 spike validates this before Phase 2 commits.
3. **Widget tinting variance** — third-party widgets resist theming; glass framing is the mitigation.
4. **File-store write contention** — mitigated by single-writer discipline and atomic rename; watch for growth, schema migrations versioned.

## 8. Directory map

```
launcher/   springboard · app-library · spotlight · widgets · control-center
hooks/      hooks-api (seam) · hooks-common (shared infra) · control-center (surface)
magisk/     ios26-stack (provisioning module)
overlays/   systemui · framework (RRO projects)
companion/  app (settings hub) · server (Ktor) · import (asset pipeline)
webui/      React SPA (Vite, zod contracts)
libs/       config (deep module) · schema (JSON Schema + codegen) · design (tokens/components) · domain (models) · icons (icon pack runtime)
assets/     icons · wallpapers · sounds (original artwork sources + pipeline)
device-tests/  Tier-3 harness (instrumented + flash/verify scripts)
tools/      codegen · scripts (dev tooling)
docs/       adr/ (decision records) · conventions.md · testing.md · ci.md
```
