# LIGHTING_MODEL_SPEC.md — Target Lighting Model (v2)

> Apple's UI lighting reads as: **a soft key light from above, ambient fill, and rim light
> catching surface edges** — shadow work is restrained; material strength and highlights carry
> depth. This spec defines the token-driven lighting model for the design system.

## 1. Light sources (the model)

1. **Key light — top-left, ~45°:** drives shadows (offset down-right) and the specular sheen
   on glass (diagonal gradient, GLASS_ENGINE_SPEC §4).
2. **Ambient:** uniform fill; sets base shadow alpha (very low) and surface tint.
3. **Rim/edge light:** counter-light along top edges of elevated surfaces (1px highlight),
   strongest on the layer ABOVE the backdrop — separates glass from glass.

## 2. Shadow language (elevation tokens, extended)

Current elevation tokens (y/radius/alpha) stay; add:

| Token | Meaning |
|---|---|
| `shadow.keyOffset` | key-light shadow offset (down-right), ~1.4× current y |
| `shadow.ambientAlpha` | ambient shadow alpha (≈ current alpha × 0.6) |
| `shadow.depthOfField` | modal backdrop blur radius (DoF behind sheets, token-capped) |

Rule: **two shadow passes max** (ambient + key), never stacked surfaces with independent
shadows at the same z (blends into mud). Sheets/modals: shadow + DoF backdrop, not double blur.

## 3. Material strength as depth

Depth = f(material strength): higher layers → more fill opacity, more edge contrast, more
specular, slightly larger radius (sheet > popover > card). Shadow is the *minimum* cue, not
the primary one (Apple behavior; keeps 120Hz budget).

| Layer (z-index token) | Material strength |
|---|---|
| background | none |
| surface (cards) | glassFillSecondary, hairline stroke, specular 0.6× |
| overlay (menus) | glassFill, 1px stroke, specular 0.8× |
| popover | glassFill, inner rim, specular 1.0× |
| sheet/modal | sheetBackground (more opaque), DoF backdrop, rim |

## 4. Specular & sheen details

- Gradient start anchor: top-left; angle token `specularAngle` (e.g., 35°).
- Alpha ramp: 0.10 → 0.00 across ~40% of height (light), 0.14 → 0.00 (dark).
- Specular sits INSIDE the surface bounds (masked by the shape), never over content.

## 5. Perf notes

- Lighting = gradients + strokes (GPU-cheap) — no blur for lighting.
- Only ONE blurred layer per surface total (blur budget from PERFORMANCE.md).
- Specular/rim computed once per surface state change, not per frame.

## 6. Acceptance

- Gallery surfaces exhibit visible depth ordering without shadow stacking.
- Token additions: `shadow.*`, `specularAngle`, layer strength values — all schema-validated.
- No per-frame allocations; gfxinfo median within 2× baseline.
