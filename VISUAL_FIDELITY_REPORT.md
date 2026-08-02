# VISUAL_FIDELITY_REPORT.md — Phase 2.6 results

## What changed
- GlassEngine v2: adaptive tint, vibrancy (saturation+luminance), specular sheen ×2, cut-glass
  rim, adaptive shadows. MotionEngine v2: press 80/160ms asymmetry, transition presets,
  haptic lead, springs retuned (damping 1.0/0.8). All 20 components rewired; none implement
  effects (ADR-0031). ADRs 0027–0031.

## Measured (device, gallery v2)

| Metric | v1 baseline | v2 (post blur-fix) | Note |
|---|---|---|---|
| 50th frame | 22ms | 25ms | +3ms = vibrancy/specular passes |
| 90th | 57ms | 46ms | improved |
| 95th | 85ms | 65ms | improved |
| Jank | 59% | 97% | first-launch scroll of all 20 surfaces dominates |
| PSS memory | — | 115MB | gallery worst-case |
| Startup (warm) | — | ~110ms | |

Blur-explosion regression caught by measurement (per-panel blur: 28/97/150ms) → fixed by
host-level blur default. Budgets binding (ADR-0030): per-screen budgets land with the
springboard; gallery stays the canary.

## Visual improvements
Glass reads as layered material (tint bias + vibrancy lift + specular + rim) vs flat fill;
surfaces separate by material strength + lighting, not shadow alone; press feedback is
80/160ms asymmetric (premium timing); dark mode toggle in gallery.

## Remaining gaps
1. SDF refraction + chromatic aberration (Phase 8 GPU work, specced).
2. True wallpaper-tint "colored glass" tone mapping (v2 = accent bias approximation).
3. Per-surface budgets: needs real screens (Phase 3).
4. Screenshot baselines captured (`device-tests/gallery-baselines/`); diff workflow +
   Roborazzi in the Phase 3 CI AVD job.
