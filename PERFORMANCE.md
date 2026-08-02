# PERFORMANCE.md — Rendering Budgets & Baseline

> Target: **120Hz rendering** (device supports 60/90/120/144; budget = 8.33ms/frame at 120Hz).
> No premature optimization — budgets defined here, enforced from Phase 3 (springboard) with
> macrobenchmark baselines (Phase 8). perfetto + gfxinfo are the measurement tools (skills loaded).

## 1. Budgets (defined BEFORE features)

| Surface | Budget | Notes |
|---|---|---|
| Frame (120Hz) | **8.33 ms** | enforced via frame pacing in Phase 3 |
| Scroll (list/grid) | ≤ 8.33 ms steady-state | lazy layouts only |
| Cold start (launcher) | ≤ 800 ms to first frame | measured from Phase 3 |
| Blur per surface | **1 blur layer max** | never stack blur |
| Window blur radius | ≤ `Blur.maxWindow` (30) | host-owned (CC overlay, sheets) |
| RenderEffect blur | ≤ `Blur.maxRenderEffect` (50) | in-window backdrops |
| Memory | ≤ 512MB RSS on 778G | device baseline: 6GB device |
| Overdraw | ≤ 2× average | debug pass per screen |

## 2. Glass cost model (API 33)

- **Window-level blur** (`Window.setBackgroundBlurRadius`, API 31+): GPU-composited behind the
  window; the correct tool for overlay panels (Control Center, sheets). Cost scales with radius —
  caps tokenized.
- **In-window blur** (`Modifier.blur`): samples within the layer; use for content backdrops only.
- **Scrim/tint fallback**: always available, nearly free; the glass mode when blur is capped.
- The GlassEngine centralizes these paths so a single token change can drop a surface to
  scrim-mode under load (graceful degradation, ADR-0023).

## 3. Baseline measurement (Phase 2, dev gallery on device)

`dumpsys gfxinfo dev.ios26.launcher` — first launch, all 20 components + window blur + wallpaper palette:

```
Total frames rendered: 140
Janky frames: 83 (59.29%)
50th percentile: 22ms   90th: 57ms   95th: 85ms
```

**Reading:** this is the worst-case surface (everything at once, first composition, synchronous
palette extraction). It validated the measurement pipeline and surfaced one real finding —
palette extraction now runs off the main thread. Per-screen budgets become enforceable when real
screens exist (Phase 3); the gallery remains the design-system canary.

## 4. Rules of the road (from day one)

1. Lazy layouts for any list/grid (already convention).
2. Single-layer glass composition; no overlapping translucent surfaces.
3. Blur radius from tokens only (caps enforced by GlassEngine).
4. No allocations in draw loops; stable lambdas; `@Stable` models (conventions §2).
5. Every Phase 3+ screen ships with a gfxinfo/perfetto baseline recorded here.
