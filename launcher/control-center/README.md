# launcher/control-center — Glass Control Center (overlay host)

Phase 3.2 surface. The Control Center renders here as a root-granted overlay window in the
launcher process (ADR-0005/0036): a thin SystemUI hook emits `cc-open`; this module watches the
event bus (ADR-0037), raises the glass panel, and drives Android system capabilities directly.

## Ownership

- Owner: control-center-owner · Domain: overlay host, panel (AGENTS.md §2)
- Depends on: `libs/design` (tokens/engines/components), `libs/config` (event bus, ADR-0019)
- Everything SystemUI-facing stays in `hooks/control-center` — nothing here touches hooks code.

## Layout

- `CcHost.kt` — orchestrator: flag gate, event watcher, raise/dismiss (graceful degradation)
- `window/CcOverlayWindow.kt` — one overlay window, window-level blur (ADR-0030 budget)
- `state/` — `CcUiState` + capability wrappers (Android APIs are the source of truth)
- `ui/` — `ControlCenterSurface` + tiles/cards (GlassEngine v2 + MotionEngine v2 + tokens only)

## Conventions

- No component-local styling: tokens only (ADR-0011/0031).
- Rollback contract unchanged from 3.1: flag off / module disable / forced failure → stock.
- Tier 1 tests: pure logic in `state/`; UI validated on the physical device (Tier 3).
