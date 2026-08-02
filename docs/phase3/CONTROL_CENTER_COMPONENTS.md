# CONTROL_CENTER_COMPONENTS.md — Glass Control Center Components

> The milestone-1 component set. Rule: every component is GlassEngine v2 + MotionEngine v2 +
> design tokens only (ADR-0031) — no component-local styling, no magic numbers.

## Components

### Surface (ControlCenterSurface.kt)
The one continuous glass field (research: "one material layer"). Full-screen scrim (25% black,
alpha-follows-entrance) + top-right panel (92% width fraction token, full height). Panel =
`glassMaterial(panelRadius 28)` + `glassLighting` + `adaptiveShadow(high)`. Scrollable-free —
milestone 1 fits one screen (quality over quantity).

### Connectivity cluster (CcTiles.kt)
2×2 glass grid (iOS 17 structure — research doc §4): **Wi-Fi, Bluetooth, Airplane, Cellular**.
Each = `CcTile`: 64dp (token), radius 22 (token), glass + lighting + low shadow,
`pressFeedback` (80ms/spring up), accent fill when active, haptics on toggle, semantics
(Role.Button + contentDescription + stateDescription On/Off). State from `ToggleControl`
wrappers: WifiManager / BluetoothAdapter / Settings.Global (airplane, mobile-data) with
settings-intent fallbacks.

### Brightness + Volume slider cards (CcTiles.kt)
Full-width glass cards, title + % readout, custom `CcSlider` (track/fill/thumb, 88dp touch
column per token, drag starts at tap point, interruptible). Brightness → Settings.System
(WRITE_SETTINGS appop; auto-mode → manual on interaction). Volume → AudioManager
STREAM_MUSIC. Semantics: progressBarRangeInfo + stateDescription percent.

### Media card (CcCards.kt + MediaButton.kt)
Active session metadata only (MediaSessionManager — no artwork capture, ADR-0037): title /
artist / "No media playing" empty state; transport row (Previous / Play·Pause / Next) as
circular glass buttons with haptics + semantics. Graceful: `getActiveSessions` guarded —
missing MEDIA_CONTENT_CONTROL degrades to the empty state.

### Focus card (CcCards.kt)
Android DND wrapped, presented as a focus-style toggle card: label + status line + switch
knob (glass circle sliding between track ends, token 51×31). Semantics Role.Switch +
stateDescription. Interruption filter via NotificationManager (ACCESS_NOTIFICATION_POLICY).

### Quick actions (ControlCenterSurface.kt)
Row of three tiles: **Flashlight** (CameraManager torch, desired-state tracking),
**Rotate** (Settings.System ACCELEROMETER_ROTATION), **Hotspot** (honest wrapper — opens the
tethering settings surface; toggling needs a signature permission, documented in code).

## Interaction contract (all components)

- Press feedback starts on DOWN (research timings), springs up.
- Every toggle: haptic (selection token) + async state re-read at +400ms (radios settle).
- Every action is runCatching-wrapped; failure → settings intent fallback, never a crash.
- Accessibility: roles, content descriptions, state descriptions, ≥48dp targets,
  reduced-motion cross-fade on the surface.
- Icons: text labels this milestone (the icon pipeline is a later phase, ADR-0012);
  no Apple assets anywhere.
