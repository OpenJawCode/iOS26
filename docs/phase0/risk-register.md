# Phase 0 — Risk Register

> Seeded 2026-08-01. Review at every Phase 0 checkpoint; update status as research resolves items. Scale: L/M/H.

| # | Risk | L | I | Mitigation | Owner | Status |
|---|---|---|---|---|---|---|
| R01 | **CC overlay latency/gesture conflict on My UX** — the top-right swipe may fight Moto's QS gesture; overlay animation quality unknown | M | H | ✅ **Mitigated (spike R7 2026-08-02):** overlay renders over any app; event→panel 68ms e2e (200ms poll; inotify sub-10ms expected); QS-gesture conflict untested until hook loads (Phase 3, modern libxposed API) | control-center-owner | ✅ spike done |
| R02 | **Moto SystemUI hook points diverge from AOSP docs** — internals renamed/moved in My UX | M | H | ✅ **Mitigated (survey 2026-08-01):** divergence mapped — no QuickSettingsController; PrcPanel + Cli overlay; primary target `NotificationPanelViewController#onInterceptTouchEvent`; adapter seam (ADR-0019) isolates; runtime check pending in spike | hooks-owner | ✅ survey done |
| R03 | **AGP 9 built-in Kotlin + new DSL friction** — greenfield but new-doc-light | M | M | `agp-9-upgrade` skill as SOP; Phase 1 bootstrap spike; version catalog pinned (R1) | build-owner | 🔄 mitigated by R1 |
| R04 | **Widget tinting variance on API 33** — many third-party widgets resist platform tinting | H | M | ✅ **Mitigated (survey R8):** ~40-60% of real home-screen widgets non-tintable on this device → glass-framing is a core Phase 2 feature + per-widget override | widgets-owner | ✅ surveyed |
| R05 | **iOS 27 design drift** — 27's GA (Sept 2026) may revise more than the beta shows | M | M | Token abstraction (ADR-0011); glass-intensity token dimension (R3); delta notes in research log | design-owner | ✅ mitigated |
| R06 | **LSPosed v2.0 (fork) on API 33** — fork targets newer Android; API 33 behavior unverified | M | H | ✅ **Mitigated (R5):** v2.1.0 (7769) confirmed running on this exact device/firmware via ReZygisk; pin updated | provisioning-owner | ✅ mitigated |
| R07 | **Performance on Snapdragon 778G** — blur-heavy UI on a mid-range SoC | M | H | Phase 8 budgets; blur discipline from day one (ADR-0003); early macrobenchmark smoke in Phase 2 | springboard-owner | open |
| R08 | **Legal — Apple IP drift** | L | H | ADR-0012 enforcement; asset review in every gate; personal import stays on-device | doc-owner | open |
| R09 | **Device access logistics** — adb/workflow for Tier 3 work not yet established on the dev machine | M | M | ✅ **Resolved:** Tailscale mesh lab (persist.adb.tcp.port + Always-on VPN); device baseline automated | provisioning-owner | ✅ resolved |
| R10 | **Schema/migration debt** — config store shape frozen early, migrations accumulate | M | M | Versioned schema + migration tests from Phase 1 (ADR-0006); codegen freshness in CI | config-owner | open |

**Open risks to watch:** none currently outside the device-gated block.
