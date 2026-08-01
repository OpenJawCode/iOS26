# ADR-0002: Platform — rooted Edge 20 on stock Android 13

- Status: Accepted
- Date: 2026-08-01
- Decision: D2, D3

## Context

LSPosed modules and Magisk modules are impossible without root; the "not a theme" layer lives or dies on it. The Edge 20's final firmware is Android 13 (API 33) — the user's device is rooted (bootloader unlocked, Magisk + Zygisk). Alternatives: unrooted architecture pivot (accessibility + overlay only), custom ROM base (AOSP SystemUI, newer API), or community-device targeting.

## Decision

- **Primary device:** the user's rooted Motorola Edge 20, stock My UX **Android 13 (API 33)**, Magisk + Zygisk.
- **Reference platform:** stock firmware, permanently frozen (no more OTAs). Personal-device-first; community/multi-device support comes later and earns its own abstraction funding.
- Device-abstraction seams stay **light** — no speculative multi-device layers.

## Consequences

- Hooks target a frozen My UX SystemUI: no drift, but Moto-specific internals (surveyed in Phase 0) — mitigated by the adapter seam (ADR-0019).
- API 33 ceiling: no post-13 platform APIs; Material You + dynamic color available; predictive back opt-in.
- Rooted EOL device is user-borne security risk (documented in README).
- LSPosed/Magisk version pins become part of provisioning reproducibility (Phase 0 deliverable).
