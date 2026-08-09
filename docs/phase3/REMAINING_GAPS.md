# REMAINING_GAPS.md — Phase 3.2

## Blocker (root-caused 2026-08-09; fix implemented, device re-verification pending)

1. **Overlay pixel presentation.** ROOT CAUSE: this Moto firmware presents an app's
   windows only while the app has a resumed activity (starting-reveal never completes
   otherwise; SF buffers stay 0×0). With the launcher activity foreground the CC renders
   fully (pixel-verified). FIX (implemented, committed 74b6560): transparent host
   activity (CcHostActivity, raised on CC-open, finished on dismiss) + FLAG_SHOW_WHEN_LOCKED
   + retained FGS. **Next device session: verify CC over a foreign foreground app (e.g.,
   Chrome), then re-run the full battery.**

## Regression (re-verify after the fix; mechanisms unchanged from 3.1)

- Feature-flag rollback (delete flag + SystemUI restart → stock) — needs one SystemUI
  restart, user-approved.
- Module-disable rollback (manager toggle) — user action.
- Forced-failure rollback — flag-driven, no restart needed for the module, but the
  effect shows at next SystemUI start.
- Crash recovery: process death mid-overlay → next raise works (fresh process) ✓ observed
  implicitly; formalize.
- Reboot persistence: registration + hook-live VERIFIED post-reboot ✓ (this session);
  re-verify once rendering is fixed.

## Device-only tests (need a user-present window; safe-set only)

- Wi-Fi toggle (live) — would sever the wireless adb link; test with immediate restore.
- Airplane toggle (live) — same constraint; write path already proven via mobile-data
  settings write.
- Bluetooth re-enable path (failed twice; re-test with the NEW_TASK fix).
- Media card with an ACTIVE session (open a media app).
- Haptics feel (user's hand, not logs).
- Brightness/volume write verification in a stable screen state.

## Performance (post-render fix)

- Frame histograms (50/90/95th) over 50 open/close cycles vs 8.33ms (ADR-0030).
- Overdraw, PSS leak check, battery with/without FGS, startup-to-first-frame.
- Baseline Profiles + Macrobenchmark scenarios (repo modules exist).

## Cleanup items (non-blocking)

- Remove `dev.ios26.spike.hook` registration (legacy-API noise in daemon logs).
- Live SELinux grants → Phase-4 sepolicy (reboot-volatile documented).
- Blur-behind: re-introduce as a single RenderEffect in the surface (ADR-0030 budget)
  once rendering is verified.
