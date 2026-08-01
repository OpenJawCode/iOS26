# Phase 0 — Discovery Workspace

Phase 0's job: **reduce uncertainty on a rooted Edge 20 before committing architecture details.** Findings live here; decisions that result land in CONTEXT.md / ADRs.

## Checklist (from ROADMAP Phase 0)

| Deliverable | Status | Where |
|---|---|---|
| Device baseline (firmware, Magisk, Zygisk, LSPosed pins) | ⏳ device-gated | `device-tests/baseline/device-baseline.sh` |
| My UX SystemUI hook-point survey (top-right swipe) | ⏳ device-gated | `research-log.md` → ADR-0019 |
| Control Center overlay spike (gesture→event→panel round-trip) | ⏳ device-gated | `research-log.md` → ADR-0005/0019 |
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
