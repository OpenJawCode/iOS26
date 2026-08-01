# ADR-0013: Layered testing — three tiers

- Status: Accepted
- Date: 2026-08-01
- Decision: D14

## Context

The stack has three testability tiers with different physics: pure logic (fast, any CI), UI (AVD-runnable), and system hooks against Moto's My UX SystemUI / overlays / Magisk flashing (cannot run on an AVD — AVDs run AOSP SystemUI, and flashing needs real hardware). "Production quality on a root-mod project" means Tiers 1–2 exhaustive and Tier 3 scripted and reproducible, not manual poking.

## Decision

- **Tier 1 (unit):** exhaustive for config/schema/domain/hooks-common logic; ≥90% coverage on those, ≥80% on new code elsewhere.
- **Tier 2 (AVD + browser):** Roborazzi screenshot tests for every screen; Compose UI tests for critical flows; Playwright E2E for WebUI against the companion server. Runs in CI on API 33 AVD.
- **Tier 3 (device-gated):** `device-tests/` harness — instrumented tests + idempotent flash/verify scripts with PASS/FAIL output; version-locked manifest (device, firmware, Magisk, LSPosed). CI integration via self-hosted runner is a Phase 9 goal; scripts must be runner-ready.
- Every bug fix ships a regression test; every surface ships screenshot baselines. No exceptions.

## Consequences

- CI gives real regression protection without hardware; device remains the arbiter of truth for hooks.
- Tier 3 discipline prevents "works on my device" rot — scripts are repeatable and versioned.
- Testing is a first-class cost in every phase (budgeted in ROADMAP).
- AVD tests verify the host side of the seam only — hooks themselves are never CI-verified until Phase 9.
