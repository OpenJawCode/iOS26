# CONTROL_CENTER_PERFORMANCE.md — Budgets, Measurements, Plan

## Budgets (ADR-0030)

| Metric | Budget | Notes |
|---|---|---|
| Frame time @120Hz | ≤ 8.33ms | target: device is 144Hz-capable; budget tracked at 120 |
| Real blurs per frame | ≤ 1 | honored: ONE window blur (FLAG_BLUR_BEHIND 30), tiles = compositing only |
| Event → surface latency | ≤ 100ms | spike baseline 68ms; PollWatcher 200ms + spring entrance |
| SystemUI idle cost | ~0 | hook = one region check per touch-down; no timers/threads (3.1-proven) |

## Measured so far (device, 3.2 bring-up)

- Overlay window attaches with blur-behind radius 30; surface present in SurfaceFlinger.
- Process stability: 3 crash classes found & fixed during bring-up (media permission,
  settle haptic without VIBRATE, overlay owner plumbing) — all now guarded; final build
  survives open/close cycles.
- Launcher baseline (3.1 gallery): 25ms median → CC surface adds compositing-only passes;
  full frame-time pass pending device-available session.

## Interaction cost design (by construction)

- Entrance/dismiss: GPU-only `graphicsLayer` translation + alpha on ONE panel node; content
  never re-lays out during motion (no constraint changes in the gesture path).
- Drag-to-close: `Animatable.snapTo` per pointer event (no physics ticking while tracking);
  spring settle only on release — one animation at a time, interruptible.
- Toggles: single state read/write; no allocations retained; async re-read debounced.
- Media: metadata read on show only (no polling loop).

## Measurement plan (next device session)

1. `dumpsys gfxinfo dev.ios26.launcher` during 50 open/close cycles → frame histogram,
   jank %, 50/90/95th.
2. `dumpsys meminfo` before/after 20 open/close cycles (leak check — no retained state by
   construction; verify PSS returns to baseline).
3. Startup cost: process start → overlay usable (first event consumed) wall-clock.
4. SystemUI stability: crash buffer + boot-loop check after the cycle battery.
5. Reboot persistence test (user-approved reboot).
6. Baseline-vs-active comparison against the 3.1 gallery baseline (25ms median).

## Notes / risks

- The 200ms PollWatcher adds latency only to the OPEN trigger (masked by the spring
  entrance); the close gesture is fully in-process (zero added latency).
- FileObserver (inotify) is the Phase-4 production path (ADR-0035) — polling stays the
  documented fallback; measurement 1 includes the polling interval in e2e feel.
- SELinux: event-file cleanup (delete) from `untrusted_app` is denied on this firmware —
  needs the Phase-4 sepolicy or a live magiskpolicy grant (same class as 3.1's
  platform_app grant); does not block event processing.
