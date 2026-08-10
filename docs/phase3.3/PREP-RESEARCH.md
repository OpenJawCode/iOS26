# Phase 3.3 — Research-only prep (2026-08-09, no implementation)

> Status: RESEARCH ONLY. Phase 3.3 (notifications surface) must not start until 3.2 is
> accepted. This doc captures what the next session needs.

## Scope recall (D1)

Notifications / lock-screen / live activities / dynamic island: **deferred** in D1 — EXCEPT
this repo already hosts the survey seam (R02) and the hook chain that 3.3 would reuse.

## Apple reference material available (vendored)

- `skills/vendored/apple-skills-hig/notifications.md`, `alerts.md`, `action-sheets.md`,
  `feedback.md`, `motion.md` — HIG corpus for notification design (banners, alerts,
  interruption levels, presentation styles).
- `skills/vendored/apple-skills-liquid-glass/` — glass treatment for banners.
- `docs/phase0/survey/systemui-hook-points.md` — the Moto SystemUI touch seam (already
  mapped; the same NotificationPanelViewController hosts the shade).

## Knowns from 3.2 (transferable)

1. Hook chain validated end-to-end post-reboot (module → SystemUI → events → host).
2. Host rendering requires a resumed activity (CcHostActivity pattern — reuse).
3. The event bus + PollWatcher pattern scales to `notif-open`-type events.
4. SELinux grants are reboot-volatile; production = Phase-4 sepolicy.
5. Moto's shade is heavily OEM-customized (Cli* classes); any 3.3 surface must NOT try to
   replace the shade — reuse the CC overlay pattern for a banner/notification surface.

## Lock-screen CC research (background lane, 2026-08-10)

Synthesis of the delegated research (iOS 18/26/27 lock-screen CC behavior + HIG):
- iOS opens CC from the lock screen by default; per-feature toggle under "Allow Access
  When Locked"; radio toggles work locked, app-launching controls require auth (since 18).
- HIG: symbol-only presentation when locked; redact privacy-sensitive state; require
  auth for security-affecting actions (privacySensitive(_:), authenticationPolicy).
- Android 13: QS tiles can show on the lockscreen; isSecure() gates content;
  unlockAndRun() prompts for unsafe actions. Android 17 (2026) converging: unlock
  required for Wi-Fi/BT/mobile-data/airplane; flashlight/rotation/battery-saver stay free.
- Implication for our overlay: classify controls by risk (safe-while-locked:
  brightness/volume/flashlight/media vs security-affecting: airplane/mobile-data/Wi-Fi/BT),
  redact state when locked, icon-only presentation, per-feature "Allow when locked"
  policy. Lock-screen CC itself remains DEFERRED (D1) — this is the design contract for
  when it lands.

## Open questions for 3.3 (to resolve with the device)

- Which notification content is accessible to the host without notification-listener
  permission (privacy posture; ADR-0037 spirit: wrap, don't rebuild)?
- Banner interaction model (tap-through vs expandable) vs the 3.2 panel gestures.
- Whether 3.3 is a surface at all, or a Phase-4 item (the D1 decision deferred it — this
  phase may legitimately be skipped/merged).
