# GLASS_ENGINE_V2.md — Implementation

Layered pipeline (ADR-0027/0028), back to front, one blur per surface:

1. **Depth** — `Modifier.adaptiveShadow(level, contentLuma)`: elevation tokens, alpha scaled by
   content luminance approximation (research: content-aware shadows).
2. **Material** — `Modifier.glassMaterial(shape, intensity)`: adaptive tint fill (glassFill
   biased toward accent by `tintBias`), vibrancy pass (BlendMode.Saturation lift +
   BlendMode.Plus luminance lift — the 140–160% saturation recipe).
3. **Content** — composables on top; contrast via semantic label tokens.
4. **Lighting** — `Modifier.glassLighting(shape)`: two opposite-angle Fresnel-style specular
   sheens (token alpha ramps) + 1px cut-glass rim (`glassStroke`).

`GlassPanel` composes the pipeline; `backdropBlur=false` by default — window-level blur
(`applyGlassBlur`, API 31+) carries material blur at the host; in-window blur is opt-in
(budget rule). Every surface uses this pipeline — no component-local effects (ADR-0031).
