# ADR-0028: Material compositing pipeline

- Status: Accepted · Date: 2026-08-02

## Context
Four-layer z-stack model (APPLE_UX_ANALYSIS): Shadow / Material / Content / Highlights. All
future surfaces must composite through one pipeline.

## Decision
`GlassPanel` composites strictly in order: shadow → material (blur+vibrancy+tint) → content →
highlights (specular+rim). The pipeline is the ONLY way surfaces get material; components never
implement their own visual effects (component rendering contract, ADR-0031).

## Consequences
Consistent depth reading; single place for perf tuning; new materials = new token sets only.
