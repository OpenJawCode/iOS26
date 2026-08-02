# ADR-0038: Control Center — SystemUI integration decisions

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0032/0033/0036

## Context

The top-right swipe gesture belongs to Moto's Quick Settings (survey R5). Phase 3.2 must claim
that gesture for the CC without touching anything else in SystemUI, and must stay fully
rollback-safe (3.1 acceptance: three rollback modes verified on device).

## Decision

- **Gesture claim:** the existing hook consumes ACTION_DOWN in the top-right region
  (x > 66% width, y < 400px — validated in 3.1) and returns `true`, so the stock shade never
  tracks the gesture; it writes `cc-open` (ADR-0037). All other regions/swipes keep stock
  behavior — Moto QS remains fully functional and is the CC's degradation path.
- **No in-SystemUI rendering, no behavior cloning:** SystemUI is not asked to disable, hide, or
  patch QS state; the panel is purely additive (ADR-0036). We wrap Android capabilities from the
  host; SystemUI code is only ever hooked at the one touch seam (ADR-0033).
- **No live cross-process tracking:** the hook emits exactly one event per gesture; the host
  spring-animates the entrance (ADR-0036). File writes are debounced and bounded (ADR-0019).
- **All-or-nothing + flags unchanged from 3.1:** `control-center.flag` gates the surface;
  forced-failure flag, module-disable, and flag-off rollback stay byte-identical.
- **Host liveness:** the launcher is HOME + MAIN (kept warm as the default launcher); if the
  overlay host is dead, the hook's event has no reader — behavior is stock (graceful).

## Consequences

- The touch seam is the ONLY SystemUI modification (reviewable, per ADR-0033); QS preservation
  is testable by swiping center/left after acceptance.
- Hooking nothing else means zero SystemUI crash surface for 3.2 beyond the validated seam.
- Trade-off accepted: the entrance can't follow the finger (event latency), documented in
  CONTROL_CENTER_RESEARCH.md §6.
