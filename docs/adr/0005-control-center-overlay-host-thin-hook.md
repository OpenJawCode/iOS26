# ADR-0005: Control Center — overlay host + thin hook

- Status: Accepted
- Date: 2026-08-01
- Decision: D6

## Context

iOS parity requires the Control Center to be reachable from any app via a top-right swipe — the same gesture Moto's Quick Settings occupies. Delivery options: full in-SystemUI integration (LSPosed renders Compose inside the SystemUI process, replacing QS — max fidelity, max fragile surface), overlay host + thin hook, or launcher-only (no global reach). Phase 0 will spike the chosen mechanism on the actual device.

## Decision

- The **panel renders as a root-granted overlay window hosted by the launcher** (normal, testable Compose code in a normal process).
- The **LSPosed surface is one thin hook**: intercept the top-right swipe in SystemUI, emit an event via the hook seam (ADR-0019), return control.
- **Graceful degradation is mandatory:** hook broken/disabled → launcher-only reachability still works.

## Consequences

- The fragile SystemUI surface is minimized and isolated behind the seam; most Control Center engineering is ordinary app code.
- Overlay-window quirks (animation overlap, input focus, z-order vs system bars) are owned risks, validated by the Phase 0 spike.
- Two-process coordination (hook → host) rides the file-event bus (ADR-0019); latency is a Phase 0 acceptance criterion.
- The launcher needs priv-app privileges for overlay reliability (ADR-0008).
