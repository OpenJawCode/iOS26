# ADR-0027: Glass Engine v2 architecture

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0023, GLASS_ENGINE_SPEC.md

## Context
Audit (2.5): flat fill + stroke reads as "themed app". Research (MATERIAL_RESEARCH): glass =
blur + saturation lift + luminance lift + edge-banded light + adaptive tint.

## Decision
GlassEngine v2 = layered compositing pipeline (single surface, GPU-cheap):
1. Base material — window/Modifier blur (tier by token) + vibrancy pass (BlendMode saturation/
   luminosity, 140-160% / ~105%) + adaptive tint (luminosity-preserving chroma shift + tintBias)
2. Depth — adaptive shadow (alpha scales with token + content luminance approximation)
3. Lighting — Fresnel-style specular sheen (two opposite-angle gradients) + 1px cut-glass rim
4. Content — contrast via semantic tokens; vibrancy flip approximated by label token pair
One blur per surface (budget rule); shader-grade SDF refraction deferred to Phase 8 (GPU work).

## Consequences
All surfaces share the pipeline (no per-component effects); tokens control every layer;
degradation = intensity token (scrim mode); measurable within 120Hz budget.
