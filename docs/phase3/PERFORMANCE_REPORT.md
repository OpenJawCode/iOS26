# PERFORMANCE_REPORT.md — Phase 3.2 (measured where the device allowed)

## The honest headline

**End-to-end performance is NOT measurable yet**: the overlay has not presented pixels on
the device (DEVICE_VALIDATION.md §FAIL), so frame-time/jank/overdraw of the real surface are
unverifiable. What follows is what WAS measured, plus the cost model that IS verifiable by
construction.

## Measured

| Metric | Value | Method / caveat |
|---|---|---|
| Process startup | FGS + host start < 4s to event-consumption (raise within PollWatcher tick) | wall clock via logs |
| Event → host | 200ms poll + write/consume; log latency ~2ms observed after poll tick | PollWatcher 200ms (spike-proven ~68ms e2e at 3.1) |
| Overlay attach | < 1s from event (addView + first composition) | window-list timing |
| Entrance animation | 102 frames rendered in ~1s (spring, stiffness 320) — renders, but frames never presented | gfxinfo counters (not screen-visible) |
| App PSS | 62,920 KB (PSS) / 141,252 KB (RSS) — includes FGS + overlay host | dumpsys meminfo |
| SystemUI stability | 0 boot loops; SystemUI survived restarts; hook adds one region check per touch | crash buffer + dumpsys |
| Launcher baseline (Phase 2.6) | gallery 25ms median (pre-CC reference) | device-tests/gallery-baselines |

## Cost model (verifiable by construction — ADR-0030)

- **One real blur**: blur-behind removed (device suspect); backdrop blur will be a single
  RenderEffect in the surface when rendering is fixed (budget: ≤1 blur/frame).
- **GPU-only motion**: panel motion = `graphicsLayer` translation/alpha on one node; no
  layout passes during entrance/drag.
- **In-process close**: drag-to-close is pointer-event `snapTo` (no physics ticking while
  tracking); spring settle only on release.
- **Inactive cost**: hook = one region check per touch-down; no timers/threads (3.1-proven);
  host idle = PollWatcher 200ms `lastModified()` (negligible; battery impact ≈ FGS baseline).
- **Toggles**: single state read + async re-read at 400ms; no retained allocations.

## Plan (next device session, once rendering is verified)

1. `dumpsys gfxinfo` histograms (50/90/95th) over 50 open/close cycles vs 8.33ms budget.
2. Overdraw: `dumpsys gfxinfo` overdraw counts for the CC window.
3. Memory leak check: PSS before/after 20 open/close cycles (expect return to baseline).
4. Battery: idle drain with FGS vs without (10-min dumpsys battery stats).
5. Startup: cold start → overlay usable (first presented frame).
6. Baseline-profiles + Macrobenchmark (benchmarks/macrobenchmark exists; device-only).
