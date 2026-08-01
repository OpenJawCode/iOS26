# SystemUI Hook-Point Survey — Control Center gesture

> **Target firmware:** `T1RGS33.135-109-9-29` (berlin_globale, patch 2024-09-01)
> **Artifacts:** `SystemUI.apk` (58 MB) pulled from `/system_ext/priv-app/SystemUI/`, decompiled with jadx 1.5.6 (9,109 classes; 23 jadx errors, none in surveyed classes). Decompiled tree kept out of repo (`/tmp/opencode/survey/out`); this document is the committed finding.
> **Date:** 2026-08-01 · **Owner:** hooks-owner · **Status:** draft — runtime validation required (spike)

## 1. Structural divergence from AOSP 13 (risk R02 confirmed)

This My UX SystemUI is AOSP-13-based but heavily restructured:

- ❌ **No `QuickSettingsController`, `QuickSettingsContainerImpl`, `StatusBarTouchImpl`** — the AOSP QS controller class does not exist in this build. Anything written from AOSP reference docs would not hook anything.
- 🧩 **"Cli" overlay layer:** `CliStatusBar`, `CliPhoneStatusBarView`, `CliNotificationShadeWindowView`, `CliQsContainer`, `CliStatusBarWindowController` — Moto's custom views/controllers interleaved with AOSP classes.
- 🧩 **"PrcPanel" naming:** `NotificationPanelViewController` logs as `PrcPanel` — Moto's replacement panel controller (AOSP 13.1+ structure with custom logic, `mIsPrcCustom` flags).
- 🧩 **Moto gesture framework:** `com.motorola.gesturetouch` (`EdgeTouchGestureDetector`, `SwipeDownDetector`, `GestureActionController`, `SystemGestureObservable`…) — edge-swipe gestures; separate from the status-bar touch path (surveyed: edge gestures, not top-edge shade drags).
- ✅ AOSP classes that survived: `NotificationShadeWindowView`, `NotificationPanelView`, `PhoneStatusBarViewController`, `QSFragment`, `QSContainerImpl`.

## 2. Touch flow (status bar → QS expansion)

```
status bar area
  → CliPhoneStatusBarView.onTouchEvent()            — proxy: forwards EVERYTHING to mPanelView.dispatchTouchEvent()
  → NotificationShadeWindowView.onInterceptTouchEvent() (line 454)
  → PhoneStatusBarView.TouchEventHandler seam
      (NotificationPanelViewController.mStatusBarViewTouchEventHandler,
       NotificationPanelViewController.java line ~630)
  → CentralSurfacesImpl.onTouchEvent() (line 2417)
      → mNotificationShadeWindowView.onTouchEvent() (line 1804)
  → NotificationPanelViewController.onInterceptTouchEvent() (line 4198)   ★ PRIMARY HOOK
      → shouldQuickSettingsIntercept(mDownX, mDownY, 0.0f) (line 4226)     ★ REGION PREDICATE
      → handleQsDown(MotionEvent) (line 2014, called at 1970)             ★ ALTERNATE HOOK
          → mQsTracking = true; onQsExpansionStarted()                    (QS drag begins)
```

## 3. Hook targets

| Priority | Target | Signature | Role |
|---|---|---|---|
| ★ Primary | `com.android.systemui.statusbar.phone.NotificationPanelViewController#onInterceptTouchEvent` | `boolean onInterceptTouchEvent(MotionEvent)` | Consume the top-right swipe **before** the shade/QF tracks it: if `ACTION_DOWN` in CC region → return `true` (never call original), emit CC-open event. Else → original. |
| 2 | `#shouldQuickSettingsIntercept` | `boolean shouldQuickSettingsIntercept(float x, float y, float velocity)` | Region predicate; reuse its logic (or bypass via x/y check) when deciding whether to consume. |
| 3 | `#handleQsDown` | `void handleQsDown(MotionEvent)` | Fallback seam if onInterceptTouchEvent interception proves insufficient (e.g., falsing interplay); also covers two-finger QS (`isOpenQsEvent`) which iOS-parity suppresses. |

All three live in **one class** → one Moto adapter (`MotoPrcPanelAdapter`) implementing the `hooks-api` seam (ADR-0019). No other SystemUI internals are touched.

## 4. Region parameters (seed values — tune in spike)

- Screen: 1080×2400 @ 446 dpi (override density; physical 400)
- App window: 1080×2243 (nav bar excluded)
- **CC region seed:** `x > 720` (right third) `&& y < 400` (upper zone covering status bar + drag-start reach). Exact bound tuned against feel + accidental-collision with notification shade drags (right-edge downward drags must still work — threshold calibrated in spike).
- Cutout hidden on this device (`HideDisplayCutout`).

## 5. Validation needed in the CC spike (device)

1. Confirm `onInterceptTouchEvent` is actually entered for status-bar-origin swipes on this build (logcat tag `NPVC` — the controller already logs `NPVC onInterceptTouchEvent (...)` at line 4201).
2. Confirm consuming with `true` prevents shade expansion AND doesn't wedge SystemUI touch state (falsing/HeadsUp downstream checks are skipped — acceptable).
3. Measure gesture→event→overlay latency end-to-end (target: inotify-scale, perceptually instant).
4. Verify two-finger QS suppression (iOS parity) via `handleQsDown`/`isOpenQsEvent`.

## 6. Risks & notes

- jadx output for these classes is clean (no decompilation errors) — signatures above are trustworthy.
- `mIsPrcCustom` flags suggest feature-gated custom paths; adapter must work for the default config (verify in spike).
- The seam (ADR-0019) keeps all of this behind `hooks-api` — the AVD side tests the host behavior; only this adapter is firmware-specific.
- If `onInterceptTouchEvent` proves unreliable, `handleQsDown` (line 2014) is the documented fallback — same class, no re-architecture.
