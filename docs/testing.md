# docs/testing.md — Testing Strategy

> Three tiers, per ADR-0013. The tier determines *where* a test runs, not whether it exists. Everything gets a test; device-only things get scripts.

## Tier 1 — Unit (logic)

**Scope:** `libs/config` (parse, validate, atomic-write, live-reload, migrations), `libs/domain`, `libs/schema` codegen, `hooks-common` logic, category mapping logic, spotlight ranking.

**Tools:** JUnit 5 + kotlinx-coroutines-test. 

**Coverage commitment:** exhaustive for config/schema/domain (line coverage ≥ 90%, enforced Phase 9); everything else ≥ 80% on new code.

**Where:** CI `unit` job; local `./gradlew testDebugUnitTest`.

## Tier 2 — UI & integration (AVD + browser)

**Scope:** launcher + companion Compose UI, WebUI.

**Tools:**
- Roborazzi screenshot tests — every screen, light+dark, per token set; baselines committed, CI diffs images.
- Compose UI tests (`createComposeRule`) for critical flows: springboard scroll/edit, folder open, App Library category switch, Spotlight search, CC panel open/close, widget add flow.
- Playwright (TS) for WebUI against the companion's embedded server on an emulator host.

**Where:** CI `avd-ui` job (AOSP Android 13 AVD, API 33) + `webui` job (node, headless browser).

**Note:** AVD runs AOSP SystemUI — hooks (Tier 3) are *never* verified here; the AVD verifies the seam's host side only.

## Tier 3 — Device-gated (real hardware)

**Scope:** LSPosed hooks against My UX SystemUI (gesture intercept, event emission), overlay behavior over real apps, RRO effectiveness, Magisk provisioning (fresh-flash path), privilege behavior of the priv-app launcher.

**Tools:** `device-tests/` harness:
- Instrumented tests (androidTest) runnable on the wired Edge 20.
- Idempotent shell scripts with explicit `PASS`/`FAIL` lines (flash, provision, hook-enable, gesture-verify, overlay-verify).
- A manifest/lock file records device + firmware + Magisk + LSPosed versions for reproducibility.

**Where:** locally on your device; CI integration via self-hosted runner (label `edge20`) is a Phase 9 goal — scripts must be runner-ready (no interactive prompts, exit codes meaningful).

## What gets tested when

| Event | Requirement |
|---|---|
| New feature | Tier 1 tests for logic, Tier 2 screenshot + flow tests for UI, Tier 3 script if hooks/Magisk/overlays involved |
| Bug fix | Regression test in the tier that caught it (usually Tier 1/2) — mandatory, no exceptions |
| Schema change | Codegen freshness + migration tests in `libs/config` |
| Perf change | Macrobenchmark baseline update (Phase 8) |

## Gates

- PR merge: Tier 1 + 2 green; Tier 3 scripts provided and passing where hardware is involved (run locally by the author).
- Release: full Tier 3 run on the device recorded with the release notes.
- Coverage regressions fail CI from Phase 9.

## Test ownership

- Directory owner owns its tests (AGENTS.md §2). `device-tests/` harness owned by qa-owner; every hook/module lands its Tier 3 script there.
