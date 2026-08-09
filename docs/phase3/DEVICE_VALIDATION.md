# DEVICE_VALIDATION.md — Phase 3.2 (Motorola Edge 20, Android 13, T1RGS33.135-109-9-29)

> Session: 2026-08-03 (post-reboot). Evidence-first: every claim has a method. No image
> analysis tool available — pixels verified by sampling, structure by a11y trees, state by
> dumpsys. The device is the user's daily driver; validation ran in bursts.

## Verified PASS (on-device, with evidence)

| Item | Evidence |
|---|---|
| Reboot persistence (registration) | Daemon DB post-reboot: module enabled=1 (users 0/10), scoped to com.android.systemui; daemon alive; module.prop/java_init.list intact in installed APK |
| Reboot persistence (hook live) | SystemUI restarted → `IOS26_CC_HOOK: onQsIntercept` fires on top-right swipe (x=950,y=60) |
| Event chain (hook → host) | `cc-open event` consumed by host after swipe AND after file touch; media refresh follows |
| Overlay window attach | `ty=APPLICATION_OVERLAY blurBehindRadius=30` present in WM; window takes input focus |
| Open gesture | Overlay attaches on top-right swipe (hook path) and on in-app event |
| Close: tap-outside | Screenshot-independent verify: window count 1 → 0 after tap at (100,1500) |
| Close: slow drag (settle) | Drag 300px slowly → window stays (settled back) |
| Close: velocity fling | 1100px/80ms fling → window removed (dismissed) |
| Interruptibility | Fling 60ms after raise → dismissed (fling wins over entrance) |
| Haptics wiring | Settle haptic on entrance (crashed pre-VIBRATE fix — now guarded); selection haptics on tiles (guarded) |
| Accessibility tree | uiautomator dump: 12 nodes — Wi-Fi/Bluetooth/Airplane/Cellular tiles, 2 sliders (Brightness/Volume), media buttons (Play/Previous/Next), Focus card, Flashlight/Rotate/Hotspot — with bounds matching tokens (64dp tiles, 88dp sliders) |
| Focus (DND) toggle | Filter 2→1 (INTERRUPTION_FILTER_ALL→NONE) after tap — real DND change |
| Bluetooth toggle (off) | BLE_ON → OFF after tap |
| Slider state read | Volume 17% in tree; brightness reads from Settings |
| Crash containment | Fallback-intent crash (NEW_TASK) found & fixed; VIBRATE crash found & fixed; both guarded |
| SELinux event writes | platform_app → shell_data_file re-granted after reboot (live policies are reboot-volatile); untrusted_app grant added for host cleanup |

## Verified FAIL / BLOCKED

| Item | Evidence | Status |
|---|---|---|
| **Overlay pixel rendering (root cause found 2026-08-09)** | This Moto firmware only PRESENTS an app's windows while the app has a RESUMED activity — otherwise the window stays in starting-reveal with 0×0 SF buffers (input + a11y + rendering all work, pixels never present). With the launcher foreground: **CC panel fully renders** (accent-filled tiles pixel-verified at token-exact bounds). Over other apps: fixed by the transparent host activity (CcHostActivity) — **fix pending device re-verification** | **ROOT-CAUSED + FIXED, re-verification pending** |
| Wi-Fi toggle (live) | Not executed — would sever the wireless adb link (no recovery path) | Pending (user-present session) |
| Airplane toggle (live) | Not executed — same reason | Pending |
| Bluetooth re-enable | OFF→ON failed twice during the session (enable() path) — needs investigation with the NEW_TASK fix in place | Pending re-test |
| Media card (playing state) | Empty state verified ("No media playing"); active-session path needs a media app | Pending |
| Brightness/volume slider writes | Sliders present + state read; write verification inconclusive (screen-state fights) | Pending re-test |

## Session findings (code fixed during validation)

0. (2026-08-09) Render root cause: activity-visible dependency on this firmware; fix =
   transparent host activity + FLAG_SHOW_WHEN_LOCKED + retained FGS. Also: the notification
   shade + keyguard (no PIN, swipe-only) can hijack taps and block rendering when the
   activity is stopped — validation must run with the launcher foreground.


1. Fallback intents crashed (app-context startActivity, no NEW_TASK) → fixed + guarded.
2. Airplane broadcast denied (signature perm) → removed; setting write alone drives the observer.
3. Module package `enabled=0` + stale apk_path after reinstall → re-enabled + DB path fixed.
4. Live SELinux grants are reboot-volatile → documented; production path = Phase-4 sepolicy.
5. Host process visibility: FGS added (procState 4) — improved layer creation but did not
   fix presentation; FLAG_BLUR_BEHIND removed as a suspect (untested hypothesis, re-add via
   RenderEffect when rendering is verified).
