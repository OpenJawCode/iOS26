# GLASS_ENGINE_SPEC.md — Target Glass Rendering (v2)

> Objective: reproduce the *principles* of Apple's modern glass (iOS 26 Liquid Glass era) with
> original Android-native rendering on API 33. This is the engineering target the GlassEngine
> evolves toward; every behavior is token-driven (extensions of TOKENS.md).

## 1. Rendering model (layers, back to front)

For a glass surface over arbitrary content, the target composite is:

```
  [background content]                     (app/wallpaper underneath)
→ [blur layer]        window blur / Modifier.blur   (radius per intensity, capped)
→ [vibrancy pass]     saturation lift + luminance shift (content feels luminous, not muddy)
→ [glass tint]        translucent fill (mode + intensity token)
→ [refraction shift]  subtle content offset/scale inside glass (parallax/refraction illusion)
→ [specular highlight] top-edge + diagonal sheen gradient (light from above-left)
→ [rim/edge stroke]   1px outer edge contrast + inner separator
→ [content]
```

## 2. Blur behavior

- Radius per `GlassIntensity` (existing tokens), capped (`maxWindow`/`maxRenderEffect`).
- **Missing today: the saturation/luminance pass** — Android blur alone darkens and desaturates;
  Apple glass *lifts* saturation and brightens luminance behind the panel. Required: a vibrancy
  layer drawn over the blurred backdrop using `BlendMode.SATURATION`/`LUMINOSITY` compositing
  (Android `Paint` blend modes are available in Compose `drawWithContent`/`blendMode`).
- Perf: the vibrancy pass must be GPU-blended, single draw; budget unchanged (8.33ms).

## 3. Glass tint & dynamic adaptation

- Tint comes from `glassFill`/`glassStroke` tokens per mode (exists).
- **Add: tint bias toward the dominant backdrop color** (wallpaper accent, DynamicColorEngine) —
  a fraction (`tintBias` token, e.g., 0.15) blended into the fill; this is the "glass adapts to
  content" behavior.
- Intensity scales both blur radius AND fill alpha + highlight alpha (single token pair).

## 4. Highlights, rim & edge contrast

- **Specular sheen:** linear gradient from top-left, white at ~8–14% alpha fading to 0 by
  ~40% height (token: `specularStart`, `specularEnd`, `specularAngle`).
- **Rim light:** 1dp inner edge stroke, light in dark mode (`glassHighlight`), dark in light
  mode — defines the surface against the backdrop (exists as `glassStroke`; add inner-shadow
  variant for depth).
- **Edge contrast rule:** adjacent glass surfaces separate via stroke alpha, never via blur
  stacking (budget rule).

## 5. Depth & separation

- Layered glass (popover over sheet over panel): each layer increases fill opacity + stroke
  contrast + specular intensity — z-depth communicated by material strength, not shadow alone
  (matches Apple's approach; shadow budget stays subtle).
- Modal/DoF: backgrounds behind sheets get an additional blur+tint scrim (`sheetBackground` +
  `scrim`), not a second blur layer.

## 6. Implementation delta (from current code)

| Aspect | Current | Target |
|---|---|---|
| Fill | flat `glassFill` | + tint bias toward backdrop accent |
| Stroke | 1dp `glassStroke` | + inner rim variant + edge contrast scale |
| Highlight | none | specular gradient layer (tokens) |
| Vibrancy | none | saturation/luminance lift over blur |
| Refraction | none | subtle content parallax offset (motion-tied) |
| Scrim fallback | exists | unchanged (degradation path) |

## 7. Acceptance

- Gallery shows glass over colored content (add colored cards behind panels in gallery).
- `dumpsys gfxinfo` on gallery stays within 2× Phase-2 baseline median after vibrancy layer.
- Every new value comes from tokens (tokens.json additions: `specular*`, `tintBias`, `vibrancy*`).
