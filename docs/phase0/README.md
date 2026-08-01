# Phase 0 — Discovery Workspace

Phase 0's job: **reduce uncertainty on a rooted Edge 20 before committing architecture details.** Findings live here; decisions that result land in CONTEXT.md / ADRs.

## Checklist (from ROADMAP Phase 0)

| Deliverable | Status | Where |
|---|---|---|
| Device baseline (firmware, Magisk, Zygisk, LSPosed pins) | ✅ done (2026-08-01) | `baseline/2026-08-01.txt` — firmware `T1RGS33.135-109-9-29`, Magisk 30.7, LSPosed v2.1.0 (7769) via ReZygisk |
| My UX SystemUI hook-point survey (top-right swipe) | ✅ done (2026-08-01) | `survey/systemui-hook-points.md` — PrcPanel `onInterceptTouchEvent` is the primary target; runtime validation in spike |
| Control Center overlay spike (gesture→event→panel round-trip) | ✅ done (2026-08-02) | R7 — chain validated 68ms e2e; hook-loading deferred to Phase 3 (fork legacy API broken; modern libxposed path) |
| Widget tinting survey | ✅ done (2026-08-02) | R8 — reference home-screen mix: 40-60% non-tintable → glass-framing core feature |
| Phase 0 close-out | 🔄 in progress | ADR-0021 (store split) accepted; open: phase exit review |
| iOS 27 design delta research | ✅ done | `research-log.md` R3 |
| Widget tinting reality check (API 33) | ⏳ device-gated | `research-log.md` |
| Toolchain pinning (version catalog draft) | ✅ done | `gradle/libs.versions.toml` |
| Risk register | ✅ seeded | `risk-register.md` |
| Open-questions resolution (CONTEXT.md §3) | 🔄 in progress | `CONTEXT.md` |

Legend: ✅ done · 🔄 in progress · ⏳ blocked on device access

## Key Phase 0 findings so far

1. **iOS 27 revises Liquid Glass** (readability) and adds a **user-adjustable translucency slider** — our token system must include a runtime-adjustable glass-intensity token from day one (feeds ADR-0011).
2. **iOS 27 changes the interaction model:** center-swipe-down opens Search (replacing Spotlight); Notification Center moves to upper-left swipe. Our Spotlight gesture mapping and any future notification surface must reflect this; the CC top-right swipe is unchanged.
3. **AGP 9 is a breaking jump:** new DSL, built-in Kotlin (the `org.jetbrains.kotlin.android` plugin is incompatible), Gradle ≥ 9.1, JDK 17+. Phase 1 bootstraps on AGP 9.3.1 + built-in Kotlin directly — no legacy migration path needed (greenfield).
4. **Ecosystem pins:** Magisk v30.7, LSPosed (JingMatrix fork) v2.0 — API 33 compatibility to be verified on-device in the baseline.
