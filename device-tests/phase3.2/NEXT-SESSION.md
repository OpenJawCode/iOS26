# Next device session — checklist (2026-08-09)

## Recovery (phone was left unreachable after a 22:20 reboot)

1. User presses-and-holds power ~10s if the phone is stuck (no wipe possible; Magisk
   bootloop protection auto-disables modules if it truly loops).
2. Wait for `tailscale status` to show motorola-edge-20 WITHOUT "offline".
3. `adb connect 100.113.213.67:5555`.
4. `svc power stayon true; settings put system screen_off_timeout 1800000; wm dismiss-keyguard`
   (no PIN — swipe-only). `cmd statusbar collapse` if the shade is stuck.
5. Re-grant (reinstall resets appops): appops write_settings + SYSTEM_ALERT_WINDOW;
   pm grant WRITE_SECURE_SETTINGS / CAMERA / BLUETOOTH_CONNECT.
6. If Magisk disabled modules: re-enable dev.ios26.hooks.controlcenter in the manager
   (or `pm enable`), verify the daemon DB (enabled=1 + apk_path current).

## Install the LATEST build (CcHostActivity + review fixes — NOT yet installed)

`adb install -r launcher/app/build/outputs/apk/debug/app-debug.apk` + grants.

## Critical re-verification (the 3.2 gate)

1. Launcher foreground → raise → panel pixels (accent tiles at (277,285)/(488,496)).
2. **Foreign app foreground (Chrome) → raise → panel pixels OVER Chrome** (the
   CcHostActivity fix — the core product case).
3. Full battery (validate.sh, calibrated coords) + perf snapshot.
4. Regression: flag off/on, module disable (DB edit or manager), crash recovery,
   reboot persistence re-check.
5. Deliverables → ACCEPTED or REJECTED. (Current: REJECTED — fix implemented, unverified.)
