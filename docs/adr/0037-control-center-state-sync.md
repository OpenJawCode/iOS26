# ADR-0037: Control Center state synchronization

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0019/0021/0035

## Context

The CC surface spans two processes: SystemUI (hook: swipe detection) and the launcher overlay
host (all UI). The surface must share open/close intent and stay crash-safe with zero process
coupling (ADR-0035 forbids binder/sockets).

## Decision

- **Events** (`cc-open`, `cc-close`) travel the config-store event bus (ADR-0019/0021):
  the hook writes `cc-open` on a consumed top-right swipe; the host writes `cc-close` after
  dismissal. Host observes via the existing `PollWatcher` (polling until FileObserver policy,
  ADR-0035). Events are ephemeral (cleanup on read), writes atomic (tmp+rename).
- **Toggle state is host-local, not synced.** The overlay drives Android system APIs directly
  (ConnectivityManager/WifiManager/BluetoothAdapter/AudioManager/Settings.System/CameraManager/
  NotificationManager/MediaSessionManager). Nothing round-trips through SystemUI — Android's
  own state is the source of truth, so the surface can never desync from the OS.
- **Media state** is observed via `MediaSessionManager.getActiveSessions` + `Callback` (active
  session metadata only — title/artist/state; no artwork capture, no notification interception).
- **Persistent user config** (layout, preferences) rides the schema-validated shared config
  store when customization lands (later milestone); milestone 1 has no persistent config.

## Consequences

- SystemUI stays stateless (writes one event file); the host is the only reader/writer of UI
  state — no cross-process races.
- Toggles always reflect the real OS state (single source of truth = Android).
- Crash-safe by construction: a dead host is invisible; a dead hook leaves the overlay unusable
  but the system stock.
