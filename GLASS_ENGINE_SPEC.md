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
  Apple glass lifts saturation and brightens luminance. Research recipe (MATERIAL_RESEARCH §2):
  **saturation boost 140–160% + luminance lift ~105%** over the blurred backdrop, via
  `BlendMode.SATURATION`/`LUMINOSITY` compositing. Without this, blur reads as 2018 glassmorphism.
- **Blur tier map (research):** Thin ~16–20px · Regular ~28–34px · Thick ~40–48px (bars) ·
  Ultra-thick ~60–70px (modal). Our `Blur` tokens map: subtle≈Thin, standard≈Regular,
  prominent≈Thick, heavy≈Ultra-thick. Each modal layer = one tier thicker.
- **Edge-banded refraction (approximation):** refraction concentrates near edges, center calm.
  v2 approximation: an edge-band highlight/offset brush (SDF-driven in Phase 8).
- Perf: the vibrancy pass must be GPU-blended, single draw; budget unchanged (8.33ms).

## 3. Glass tint & dynamic adaptation

- Tint comes from `glassFill`/`glassStroke` tokens per mode (exists).
- **Add: content-adaptive tint** (research §8) — a tint generates a RANGE of tones mapped to
  backdrop brightness (hue/brightness/saturation shift, luminosity-preserving blend for
  colorful tints; flat-film only for white/black dimming). v2: `tintBias` (0.15) toward the
  wallpaper accent + luminosity-preserving chroma shift.
- **Adaptive shadow** (research §6): shadow opacity increases over text, decreases over light
  backdrops — content-aware, the depth governor.
- Intensity scales both blur radius AND fill alpha + highlight alpha (single token pair).

## 4. Highlights, rim & edge contrast

- **Specular sheen:** TWO opposite-angle edge highlights (Fresnel-style, research §4): primary
  top-left gradient white 10–14% (light) / 14% (dark) fading by ~40% height; counter-pass at the
  opposite angle at ~60% intensity. Rim term = edgeFactor²·⁵.
- **Rim light / "cut glass" edge:** 1px inner border catching light — white ~20% (light) / 8%
  (dark) (research §5); scroll edge effects (content blurs+fades under floating glass) replace
  hard dividers.
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
