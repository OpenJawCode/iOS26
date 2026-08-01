# CONTEXT.md — Glossary, Decisions, Open Questions

> **Living document.** This is the project's stateful memory: the ubiquitous language, the decision log, and everything still open. Update it whenever a term is clarified, a decision is made, or a question resolves. ADRs hold the full reasoning; this file is the index.

Last updated: 2026-08-01

---

## 1. Ubiquitous language

| Term | Definition |
|---|---|
| **Springboard** | The home-screen workspace: icon grid, pages, dock, folders, edit (jiggle) mode, context menus. The launcher's core surface. |
| **App Library** | Auto-categorized full-app grid reachable from the springboard — iOS's app drawer replacement. |
| **Spotlight** | The search surface: apps, contacts, app actions. Fully local, no cloud. |
| **Control Center** | The iOS-style panel for connectivity, brightness/volume, media, and shortcuts. Delivered as a root-granted overlay window hosted by the launcher. |
| **Thin Hook** | The minimal LSPosed surface: intercept the top-right swipe in SystemUI and signal the panel host. Everything else lives in normal, testable app code. |
| **Overlay Host** | The launcher's window-overlay service that renders the Control Center panel over any app. |
| **Hook Seam** | The contract between the LSPosed layer and app layer (`hooks-api`): what hooks can do, what events they emit, and how the host reacts. |
| **Config Store** | The schema-validated file tree under `/data/adb/ios26/` — the single source of truth for all configuration and state. |
| **Token Set** | A named collection of design tokens (color, blur, typography, spacing, motion). Themes are token sets. |
| **Release Train** | The single versioning model: one stack version = one installable experience; per-component semver aggregated. |
| **Provisioning** | What the Magisk module does at install time: systemless priv-app install, overlay install, config-store bootstrap, hook enabling. |
| **Tier-3** | Device-gated testing: anything that must run on real hardware (hooks against My UX SystemUI, Magisk flash, overlays). |
| **Glass** | The Liquid Glass rendering treatment: translucency, live blur, refraction-inspired highlights, dynamic tinting. |
| **Systemless** | Magisk's overlay-install mechanism — modifies the live system without touching partitions. |

## 2. Decision log (17 decisions + derived commitments)

Full reasoning in `docs/adr/`.

| # | Decision | ADR |
|---|---|---|
| D1 | Curated surface subset: v1 = Springboard + App Library + Spotlight + Control Center. Deferred: lock screen, status bar, notifications, live activities, dynamic island | ADR-0001 |
| D2 | Rooted primary device: Edge 20, bootloader unlocked, Magisk + Zygisk | ADR-0002 |
| D3 | Reference platform: **stock My UX Android 13 (API 33)** — final firmware, frozen target. Personal device first; community later | ADR-0002 |
| D4 | Compose everywhere; liquid glass via Canvas + RenderEffect with a perf discipline budget | ADR-0003 |
| D5 | Monorepo; Gradle multi-module + `webui/` with own toolchain; per-component semver, single release train | ADR-0004 |
| D6 | Control Center = overlay host + thin LSPosed hook; launcher-only fallback | ADR-0005 |
| D7 | Config = schema-validated file store; JSON Schema → Kotlin/TS codegen; shared `config` lib; zero process coupling | ADR-0006 |
| D8 | LSPosed = per-surface modules + build-time-shared `hooks-common` | ADR-0007 |
| D9 | Packaging = hybrid: Magisk for system bits, direct APK install for dev, regenerated module per release | ADR-0008 |
| D10 | Companion = on-device settings hub; no launcher-internal settings; layout editor deferred to WebUI | ADR-0009 |
| D11 | WebUI = React SPA served by embedded Ktor server in companion; LAN + one-time token; layout editor, presets, wallpapers, import/export, diagnostics | ADR-0010 |
| D12 | Design anchor = iOS 26 Liquid Glass; full token abstraction (iOS 27 = future token set) | ADR-0011 |
| D13 | Assets = original permissive defaults + on-device personal import; no Apple IP in repo | ADR-0012 |
| D14 | Testing = three tiers: exhaustive unit; AVD UI (Roborazzi/Compose) + Playwright in CI; device-gated Tier-3 harness | ADR-0013 |
| D15 | License = GPL-3.0 everywhere | ADR-0014 |
| D16 | Widgets = full AppWidgetHost in v1, iOS-styled slots, platform tinting where possible | ADR-0015 |
| D17 | App Library = auto-categories with bundled localized mapping + per-app overrides; Spotlight = apps + contacts + actions, offline | ADR-0016 |

**Derived commitments** (recorded as ADRs where they carry weight): minSdk = targetSdk = 33 (ADR-0017) · no third-party crash/analytics SDKs (ADR-0018) · hook seam + file-based event signaling (ADR-0019) · GitHub Actions CI/CD model (ADR-0020) · Kotlin on Android, TypeScript/React (Vite) + zod for WebUI · no Rust, no cloud.

## 3. Open questions

These are owned by Phase 0 (Discovery). Do not guess — research, then decide (one question at a time).

1. **Moto SystemUI hook-point survey** — map the actual classes/methods on this device's My UX SystemUI for the top-right swipe intercept. (Phase 0 spike, ADR-0005 depends on it)
2. **iOS 27 design delta** — what changed vs iOS 26, and which refinements become a second token set. (feeds ADR-0011)
3. **Toolchain bootstrap** — exact AGP / Kotlin / Compose BOM / Gradle versions for Phase 1, pinned via the version catalog. (feeds ADR-0003)
4. **Widget tinting reality** — which Android 13 widgets actually honor platform tinting vs which will need glass framing. (feeds ADR-0015)
5. **Category data layer** — initial category taxonomy and coverage strategy for the App Library mapping. (feeds ADR-0016)
6. **LSPosed + Magisk version pins** — versions verified against API 33 My UX, recorded for reproducible provisioning.
7. **Backup/restore format** — whether backup is a single bundle (config + assets) or config-only in v1.

## 4. Rules for this document

- **Never delete history.** New decisions append; superseded decisions get a `SUPERSEDED BY` marker.
- Every decision listed here must have an ADR, and vice versa.
- When a term in the glossary is overloaded (two meanings), split it or rename — flag it during review.
