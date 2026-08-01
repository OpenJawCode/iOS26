# ADR-0009: Companion app = on-device settings hub

- Status: Accepted
- Date: 2026-08-01
- Decision: D10

## Context

"Companion app" was underspecified; its boundary determines the WebUI's boundary and the launcher's settings surface. Scope-creep risk: the companion can silently become a second launcher. The launcher should not carry its own settings UI.

## Decision

The companion app is the **Settings app of the experience** — on-device, always available:

- Theme & design-token sets, icon packs, wallpaper, backup/restore, per-module toggles, diagnostics (hook health, config validation).
- **Personal asset import** pipeline (ADR-0012).
- **No layout editor** (deferred to WebUI — big screen is the right place for drag-drop arrangement).
- Launcher settings live here, not in the launcher.

## Consequences

- Launcher stays focused on the experience; settings have one home.
- Companion owns the human face of the config store — writes only through `libs/config` (ADR-0006).
- Boundary is explicit: companion edits config; it does not render the experience.
- Phase 5 scope is bounded; layout editing is a Phase 6 surface.
