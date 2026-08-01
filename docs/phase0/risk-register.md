# Phase 0 — Risk Register

> Seeded 2026-08-01. Review at every Phase 0 checkpoint; update status as research resolves items. Scale: L/M/H.

| # | Risk | L | I | Mitigation | Owner | Status |
|---|---|---|---|---|---|---|
| R01 | **CC overlay latency/gesture conflict on My UX** — the top-right swipe may fight Moto's QS gesture; overlay animation quality unknown | M | H | Phase 0 spike: overlay window + file-event round-trip measured on device (acceptance: inotify-scale latency, clean animation) | control-center-owner | ⏳ device-gated |
| R02 | **Moto SystemUI hook points diverge from AOSP docs** — internals renamed/moved in My UX | M | H | Hook-point survey on the actual firmware; adapter seam (ADR-0019) isolates; launcher-only fallback (ADR-0005) | hooks-owner | ⏳ device-gated |
| R03 | **AGP 9 built-in Kotlin + new DSL friction** — greenfield but new-doc-light | M | M | `agp-9-upgrade` skill as SOP; Phase 1 bootstrap spike; version catalog pinned (R1) | build-owner | 🔄 mitigated by R1 |
| R04 | **Widget tinting variance on API 33** — many third-party widgets resist platform tinting | H | M | Tint survey on device; glass framing fallback (ADR-0015); honest docs | widgets-owner | ⏳ device-gated |
| R05 | **iOS 27 design drift** — 27's GA (Sept 2026) may revise more than the beta shows | M | M | Token abstraction (ADR-0011); glass-intensity token dimension (R3); delta notes in research log | design-owner | ✅ mitigated |
| R06 | **LSPosed v2.0 (fork) on API 33** — fork targets newer Android; API 33 behavior unverified | M | H | Baseline verification on device; pin exact versions; document fallback to last known-good | provisioning-owner | ⏳ device-gated |
| R07 | **Performance on Snapdragon 778G** — blur-heavy UI on a mid-range SoC | M | H | Phase 8 budgets; blur discipline from day one (ADR-0003); early macrobenchmark smoke in Phase 2 | springboard-owner | open |
| R08 | **Legal — Apple IP drift** | L | H | ADR-0012 enforcement; asset review in every gate; personal import stays on-device | doc-owner | open |
| R09 | **Device access logistics** — adb/workflow for Tier 3 work not yet established on the dev machine | M | M | Decision pending (interview); baseline script ready (`device-tests/baseline/`) | provisioning-owner | ⏳ decision |
| R10 | **Schema/migration debt** — config store shape frozen early, migrations accumulate | M | M | Versioned schema + migration tests from Phase 1 (ADR-0006); codegen freshness in CI | config-owner | open |

**Open risks to watch:** none currently outside the device-gated block.
