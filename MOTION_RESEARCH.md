# MOTION_RESEARCH.md — Apple Motion & Interaction: Forensics

> Research phase 2.5. Compiled from WWDC18 "Designing Fluid Interfaces", WWDC23 "Animate with
> springs", WWDC24 "Enhance your UI animations", WWDC25, HIG, and engineering analyses
> (Schnorr, flyosity, Gitter, Jagatap, Lobanov). Principles + concrete parameters only.

## 1. Springs — the parameter model

- Apple's spring = damped harmonic oscillator (mass, stiffness, damping); for design work:
  **damping ratio** (0 = oscillates forever, 1 = no overshoot) + **response** (≈ 1/frequency).
  Apple avoids "duration" for elastic behavior — "the spring is always moving and ready to move
  somewhere else." *(WWDC18 803)*
- Conversion (mass 1): stiffness = (2π/response)²; damping = 4π·dampingRatio/response.
  *(Gitter; Schnorr)*
- **Documented ranges:**
  | Context | Damping ratio | Notes |
  |---|---|---|
  | Tap-driven presentation | **1.0** | no overshoot |
  | Swipe-driven dismissal | **~0.8** | small bounce/squish (has momentum) |
  | Default UI spring | **1.0**, response 0.3–0.4s | |
  | Momentum/flick | **~0.8**, response 0.3–0.4s | |
- SwiftUI: duration + bounce; bounce 0 = critically damped (default), 0.3 = noticeable bounce,
  >0.4 = exaggerated. "Springs don't need to bounce to make a great animation." *(WWDC23)*
- **Rule:** never force a duration on a spring; physical parameters resolve settling. Forced
  durations are the #1 cause of unnatural springs. *(flyosity)*

## 2. Velocity — momentum carries

- **Release velocity feeds the following animation**, normalized:
  `normalizedVelocity = gestureVelocity / (target − current)`. *(Gauthier; WWDC24)*
- **Commit/cancel by velocity sign, not position**: dismiss if |v| > 300pt/s regardless of
  position; else require >50% travel. *(Gauthier)*
- Successive springs **inherit the predecessor's velocity** on retarget. *(Apple spring docs)*
- Scroll deceleration: exponential — normal 0.998 / fast 0.99 per ms; projection
  `s = current + v/1000 · d/(1−d)`; boundary handoff to a damping-1 spring at collision
  velocity (seamless joint). *(Lobanov; WWDC18)*

## 3. Acceleration/deceleration

- **Gesture-driven → springs** (survive interruption, preserve energy).
- **System-announced changes → easing** (ease-in/out cubic), fixed duration.
- Perceptual bands: press/hover 120–180ms; small state 180–260ms; large transitions ≤ ~300ms
  ("beyond 300ms reads intentional, not reactive"). Press-down **80ms**, release **160ms**.
  *(Salaja; Rork Lab)*
- "If an animation feels slow it's almost never the curve — it's the duration. Shorten it
  first." *(Salaja)*

## 4. Gesture interruption — the core rule

- **Fluid = responsive, interruptible, redirectable** — respond at touch instant, stop mid-
  flight, reverse mid-flight. *(WWDC18)*
- **Always animate from the PRESENTATION (live) value, never the model target** — model-value
  starts cause visible jumps. *(WWDC14 236; apple-design skill)*
- **Blend velocity on retarget (additive animations)**, not hard-cut. *(WWDC14)*
- Never lock out input during a transition. *(WWDC14)*
- Android: Compose `Animatable` retargeting (springs start from current value natively) —
  avoid view-framework keyframe transitions.

## 5. Continuity

- Shared-element identity: one object morphs, not two screens ("spatial continuity illusion").
  *(WWDC24; Jagatap)*
- Interactive retargeting: `interactiveSpring` every frame during drag; final spring inherits
  velocity. *(WWDC24)*
- Stagger sub-elements **40–80ms**; haptics lead visual by **10–20ms**; monospaced digits kill
  jitter. *(Jagatap)*

## 6. Micro-interactions & touch feedback

- **Press states mandatory** ("without a press state, a button feels unresponsive"); min
  **44×44pt**. *(HIG)*
- **Feedback starts at touch-DOWN**, continuous during the gesture. *(Rork; HIG)*
- Button press = 3 simultaneous changes (scale ~0.96 in 80ms, desaturation, shadow collapse),
  spring back at release (fraction ≈ 0.6). *(Jagatap)*
- Latency: iOS ≈ **1.5 frames** with coalescing + prediction; 1:1 tracking including grab
  offset. *(WWDC15)*
- Haptics: sparingly, at commitment moments, never the only channel. *(HIG)*

## 7. Interaction philosophy (HIG)

- **Direct manipulation**: 1:1 tracking is the defining test. *(MobileHIG; WWDC15)*
- **Predictability**: standard gestures behave identically; custom gestures are never the only
  path. *(MobileHIG; HIG Gestures)*
- **Discoverability**: standard gestures, primary legible path, visual cues, animation to hint.
  *(WWDC21 10126)*
- **Feedback**: multi-modal (visual + haptic + audio as one designed channel), acknowledges
  every action. *(HIG; WWDC19 223)*
- **Gesture roles**: tap=activate, drag=move, swipe=scroll/dismiss/reveal/edge-back,
  touch-and-hold=context menu, pinch=zoom. Discrete recognizers unsuitable for interactive
  transitions. *(HIG)*

## 8. Accessibility (the Android mapping)

| iOS | Behavior | Android |
|---|---|---|
| Reduce Motion | **cross-fade REPLACEMENT**, not removal; disable elastic properties | ANIMATOR_DURATION_SCALE=0; Compose reduce-motion; swap to cross-fade |
| Reduce Transparency | blurs → opaque solids; glass frostier | no standard toggle; our `GlassIntensity.Subtle` + scrim fallback |
| Increase Contrast | B/W + contrasting borders | our high-contrast token set |
| Bold Text | stroke weight ↑ | our type tokens |

Contrast: WCAG AA — 4.5:1 ≤17pt, 3:1 ≥18pt/bold. *(HIG Accessibility)*

## 9. The premium-feel recipe (Android-applicable)

1. Physical springs, no forced durations (damping 1.0 / response 0.3–0.4s as default).
2. Momentum handoff into every gesture-driven animation.
3. Always animate from the current live value with velocity blending.
4. Instant + continuous touch-down feedback (press 80ms, release spring 160ms).
5. Reduce-motion via replacement (cross-fade), reduce-transparency honored.
