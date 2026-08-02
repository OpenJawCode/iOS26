# REFINED_DESIGN_SYSTEM.md — The Target Language (v2)

> This is the *contract* the Phase 2.5 research produces: what the design system must FEEL like,
> expressed as engineering targets. Evidence in MATERIAL_RESEARCH.md / MOTION_RESEARCH.md /
> APPLE_UX_ANALYSIS.md; engineering in GLASS_ENGINE_SPEC.md / LIGHTING_MODEL_SPEC.md;
> delta from today in AUDIT.md.

## 1. The thesis

Apple's modern UI feels alive because **material and motion are one system**: glass responds to
what's behind it and how the user touches it; depth is carried by light, not shadow; every
interaction is instant, interruptible, and continuous. A "modern OS" feel on Android requires
reproducing those *behaviors* — not the assets.

## 2. Material language (v2)

- **Glass is a reaction, not a texture.** It blurs, lifts saturation, tints toward the backdrop,
  catches light along its edges, and shifts content subtly (refraction). [GLASS_ENGINE_SPEC]
- **Depth is light + material strength.** Key-light shadows, ambient fill, rim edges, specular
  sheen; higher layers = stronger material. [LIGHTING_MODEL_SPEC]
- **One blur per surface; everything else is compositing.** Budget rule (PERFORMANCE.md) holds.

## 3. Motion language (v2)

- **Springs everywhere interactive; tweens only for passive state.** Press responds instantly
  (scale + tint), release settles with a snappy spring, drags track 1:1 with velocity carry.
- **Transitions are a vocabulary, not an afterthought:** entrance (decelerate, fade+scale in),
  exit (accelerate, faster), shared-element continuity where the OS allows (SpringBoard → app).
- **Reduced motion & reduce-transparency are schemes, not flags:** tokens swap (multiplier 0.5,
  glass → subtle/scrim). [MOTION_RESEARCH]

## 4. Interaction language (v2)

- Direct manipulation first; every control gives feedback on touch-DOWN.
- Predictable gestures (swipe corners per iOS 27 model, R3); haptics aligned to outcome.
- Discoverability via affordance, not decoration.

## 5. Token additions (all schema-validated)

`specularStart/End/Angle` · `tintBias` · `vibrancySaturation/Luminance` · `shadow.keyOffset/
ambientAlpha/depthOfField` · `transition.entrance/exit` presets · `reduceTransparency` mode map.

## 6. Component wiring (v2)

Every interactive component: press state (instant) → motion (spring) → haptic (outcome) →
settle. Every surface: material layers per lighting model. Gallery demonstrates over colored
backdrops.

## 7. Acceptance (how we know it worked)

1. Gallery over colored content reads as layered glass (visual review, DESIGN_REVIEW.md updated).
2. Frame budget holds: gfxinfo median ≤ 2× Phase-2 baseline with vibrancy+specular active.
3. Reduced-motion/reduce-transparency schemes verified on device (settings-driven).
4. No token bypasses (architecture gate + review).
5. The 100% honest question — "modern OS or themed app?" — answered by the review with the
   gallery as evidence, not assertion.
