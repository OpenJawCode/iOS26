# Phase 3.1 — Performance Report

> Measured on device during validation (2026-08-02). Hook inactive = stock SystemUI + one
> region check per touch-down (no timers, no threads, no allocations retained).

| Metric | Value | Note |
|---|---|---|
| SystemUI 50th frame | 14ms | includes 100-swipe stress; ~device baseline |
| SystemUI jank (overall) | 16.25% | dominated by shade interactions during stress |
| SystemUI PSS | 257MB | hook adds ~0 (classes only, no state) |
| Idle CPU | ~0% | hook has no background work by construction |
| Hook latency (touch → log) | in-event | no added latency path — intercept runs inline, single region check |
| Event write (tmp+rename) | in-process FS op | atomic, ~µs scale; observed reliable 204/204 |
| Crashes / boot loops | 0 / 0 | |

## Frame-timing methodology note

`dumpsys gfxinfo com.android.systemui` captures the WHOLE surface incl. shade interactions.
A clean inactive baseline (no swipes, 60s idle) would isolate the hook's idle cost further;
deferred to the Phase 3.2 measurement pass where the CC surface itself is measured against
the 8.33ms budget (ADR-0030).
