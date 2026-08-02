# ADR-0036: Control Center rendering architecture

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0005/0028/0030/0034

## Context

Phase 3.2 renders the first real surface: a glass Control Center that must look and move like
the reference (CONTROL_CENTER_RESEARCH.md) while honoring the performance budgets (ADR-0030:
8.33ms @120Hz, ONE real blur/surface). ADR-0005/0034 already decided: SystemUI stays a thin
hook; the launcher overlay host owns all rendering.

## Decision

The Control Center is a **single overlay window hosted by the launcher process**:

- `TYPE_APPLICATION_OVERLAY` + `FLAG_LAYOUT_IN_SCREEN` (drawn over the status bar, like the
  reference), focusable while open (toggles + tap-outside-to-close).
- **The only real blur is window-level `FLAG_BLUR_BEHIND`** (API 31+ RenderEffect-based window
  blur, GPU-composited by SurfaceFlinger). Tiles never blur individually — they use the v2
  compositing approximation only (`glassMaterial` tint/vibrancy + `glassLighting` specular/rim +
  `adaptiveShadow`). This keeps the render budget at one blur per frame (ADR-0030).
- The panel is one Compose surface; entrance is **event-triggered spring animation** (the file
  bus is the trigger, not a finger tracker — ADR-0034's 68ms baseline), while the **close is an
  in-process interactive drag** (zero latency, interruptible, velocity-aware — WWDC18 doctrine).
- All geometry/motion come from design tokens (new `ControlCenter` section, ADR-0011); no
  component-local styling (ADR-0031).

## Consequences

- Real-time content-aware backdrop (window blur); tiles read as one continuous glass field.
- The overlay host (launcher) must stay alive; launcher is HOME + priv-app packaging (ADR-0008)
  and keeps the process warm.
- Overlay-window quirks (z-order vs system bars, input focus) are owned risks; validated on the
  physical device in 3.2 acceptance.
- Graceful degradation: no overlay permission → in-app sheet fallback (ADR-0005).
