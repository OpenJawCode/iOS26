# MATERIAL_RESEARCH.md — Liquid Glass: Material System Forensics

> Research phase 2.5. Compiled from primary Apple sources (WWDC25 sessions 219/220/284/323/310,
> HIG, developer docs) and reverse-engineering analyses (ShatteredGlass, liquidass, decant,
> Sorrell, Windcraft, Flutter liquid-glass guide). **Principles only — no Apple assets or code.**
> Full citations in the source digests; key sources inline.

## 1. Glass hierarchy & material levels

- Liquid Glass is a **functional layer** that floats above content (navigation, interactive
  chrome). Apple forbids glass in the content layer and glass-on-glass stacking. *(WWDC25 219)*
- Two variants: **Regular** (fully adaptive — flips light/dark, casts dynamic shadows, refracts;
  ~95% of surfaces) and **Clear** (permanently more transparent, needs a dimming layer; media
  backdrops only). Never mix. *(HIG Materials)*
- **Thickness is a size-dependent simulation**: larger surfaces simulate thicker glass — deeper
  shadows, stronger lensing, softer light scattering. *(WWDC25 219)*
- RE tier map: Thin ~16–20px, Regular ~28–34px, Thick ~40–48px (nav/tab), Ultra-thick ~60–70px
  (modal). Each modal layer goes one tier thicker. *(Flutter guide; genjutsu)*
- **Glass can't sample glass**: adjacent glass shares a sampling region (container effect) — one
  sampling pass, uniform adaptation. *(WWDC25 323/284)*

## 2. Translucency & blur

- Regular glass "**blurs and adjusts the luminosity** of background content" — continuous
  adaptation (tint, shadows, dynamic range shift in real time). *(HIG Materials)*
- **Blur is not plain Gaussian** — reads as a lens effect concentrated near edges, calm in the
  center ("bends, shapes, concentrates light"). *(WWDC25 219; Windcraft)*
- **RE measured recipe:** heavy blur (~30–40px for bars) + **saturation boost ~140–160%** +
  **brightness lift ~105%** + tint overlay white 8–14% (light) / black 20–28% (dark). The
  saturation/luminance boost is what separates "glass" from "glassmorphism". *(Flutter guide)*
- **Adaptive flip:** small elements flip light↔dark by backdrop brightness; large elements adapt
  without flipping. Dark Mode retains more transparency. *(WWDC25 219; HIG Color)*

## 3. Refraction (the defining behavior)

- Lensing: material bends/concentrates light by surface curvature; elements **materialize by
  modulating light-bending**, not fading. *(WWDC25 219)*
- **RE — SDF edge refraction:** the shape's signed-distance-field gradient is the surface normal;
  a sloped-edge bevel drives refraction so warping is **edge-banded** (center stays calm);
  Snell's law governs bend (IOR ≈ 1.5–1.52; displacement ∝ slope × (1 − 1/n) × bevel).
  *(Sorrell; Rahmoune; Windcraft)*
- **Chromatic aberration**: per-channel IOR offsets produce color fringing at edges and
  selection bubbles. *(Sorrell; MacStories)*
- Observable cues: text under glass visibly distorts; spherical aberration at edges. *(MacStories)*

## 4. Light scattering / specular highlights

- Glass lives in a **lighting environment**; highlights respond to geometry and **device motion**
  (gyro) — tilting moves glints; lights travel on lock/unlock. *(WWDC25 219; Newsroom)*
- **RE — two opposite-angle highlight passes** (SDF edge band, Fresnel-style grazing reflection),
  each toned by a `vibrantColorMatrix`. *(ShatteredGlass)*
- **RE Fresnel:** specular ≈ pow(dot(N,L), specPower) × edgeFactor + rim pow(edgeFactor, 2.5) —
  bright where the surface curves toward the light. *(Sorrell)*
- **On-touch illumination:** controls "illuminate from within" and reflect onto nearby glass;
  HDR gleams on taps. *(WWDC25 219)*

## 5. Rim lighting / edge contrast

- **Inner edge highlight ("cut glass" rim):** ~1px inner border that catches light — RE ≈ white
  20% (light) / 8% (dark). *(Flutter guide)*
- **Scroll edge effects replace dividers:** content blurs+fades under floating glass (soft =
  progressive; hard = opaque backing). Adapts to dark content under it. *(WWDC25 219/310; HIG)*
- Increase Contrast accessibility → B/W glass + contrasting border. *(WWDC25 219)*

## 6. Depth separation

- **Dynamic shadow opacity is the depth governor:** shadows strengthen over text, weaken over
  light/solid backdrops. *(WWDC25 219; LiquidGlassReference)*
- Size ⇒ depth; **ambient light spill** onto large surfaces; layered z-reads via lensing +
  highlights + adaptive shadows — "the least flat iOS UI in over a decade" without heavy
  shadows. *(WWDC25 219; Newsroom; MacStories)*

## 7. Vibrancy

- Content on glass auto-vibrants: text/symbols **flip light↔dark**; system applies brightness/
  saturation/hue treatments for legibility. *(HIG Materials)*
- RE: `vibrantColorMatrix` (4×5) tones layers. *(ShatteredGlass)*
- Precedent: UIVibrancyEffect "amplifies and adjusts the color of content layered behind."
  *(Apple docs)*

## 8. Dynamic adaptation to wallpaper/content

- **Tint = "colored glass" algorithm**: a tint generates a RANGE of tones mapped to backdrop
  brightness, shifting hue/brightness/saturation continuously — a solid fill reads as opaque
  and breaks the material. *(WWDC25 219; HIG Color)*
- Wallpaper tinting: folders, App Library, dock, search, notifications take wallpaper-derived
  tints; Lock Screen clock adapts "behind the subject." *(MacRumors; Newsroom)*
- **RE tint blend paths:** flat-film (pulls every pixel toward tint's color AND brightness —
  for dimming) vs luminosity-preserving (chroma-only shift, structure stays crisp — "real
  glass"). Colorful tints preserve luminosity; white/black use flat film. *(liquid_glass_widgets)*
- Reduce Transparency → frosted/solid; user transparency control exists (iOS 26.1). *(Apple;
  HN)*

## 9. The rendering recipe (for our engine)

Four composited optical layers (RE consensus):
1. **SDF edge-banded refraction** (Snell, IOR ≈ 1.5)
2. **Chromatic aberration** (per-channel IOR offsets)
3. **Frosted multi-tap blur** over the refracted result + **saturation 140–160% / luminance
   ~105%**
4. **Fresnel/specular rim + opposite-angle edge highlights**, adaptive content-aware shadow,
   optional tone-mapped tint.

**Android implication:** a plain `Modifier.blur` reads as 2018 glassmorphism. Our GlassEngine v2
must add the saturation/luminance lift, an SDF-style edge band (achievable with `Brush` gradients
+ shape distance), Fresnel rim, and adaptive tint to be credible.

## 10. Open items (honest)

- Apple publishes no numeric spec (blur sigma/tint curves); all numbers are third-party
  measurements to be tuned on-device.
- SDF refraction + chromatic aberration are GPU-shader work — Phase 8 territory; v2 gets the
  compositing approximations (saturation lift, specular, rim, adaptive tint).
