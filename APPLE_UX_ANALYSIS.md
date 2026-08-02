# APPLE_UX_ANALYSIS.md — Synthesis: What Makes Modern iOS Feel Modern

> The 2.5 research synthesis. Reads the forensic digests (MATERIAL_RESEARCH.md, MOTION_RESEARCH.md)
> and high-quality analysis sources into one answer: **what behavior set a modern mobile OS
> exhibits that a "themed Android app" does not** — and what our Android-native system must do.

## 1. The through-line

Apple's modern UI is **one continuous physical system**: material and motion obey the same laws,
and every state change is an *object* responding to light and touch — never a panel being styled.
The four things a themed app lacks, and iOS has:

1. **Material that reacts to content** (tint/luminosity/shadow adapt to what's behind it).
2. **Light that reacts to geometry and device** (speculars, rim, motion-driven glints).
3. **Motion that inherits physical momentum** (velocity → spring, no forced durations).
4. **Interruption without friction** (animate from the live value, redirectable mid-flight).

## 2. Reading the evidence

| Source | What it tells us | Implication for us |
|---|---|---|
| WWDC18 "Designing Fluid Interfaces" | Interruptibility + velocity + presentation-value animation | Compose springs + Animatable retargeting; no forced durations |
| WWDC25 "Meet Liquid Glass" | Functional-layer glass, refraction, adaptive tint/shadow, materialization | GlassEngine v2 (edge-banded compositing, adaptive tint, content-aware shadow) |
| WWDC23 "Animate with springs" | Duration+bounce; bounce 0 default; "don't bounce to be great" | Retune our springs to damping 1.0/0.8 (researched) |
| HIG (Materials/Motion/Accessibility) | Multi-modal feedback, press-on-down, cross-fade reduced-motion | Wire press 80/160ms; cross-fade scheme; reduce-transparency path |
| Kim Spencer "Inside the Box" | iOS = strict 4-layer z-stack (Shadow/Material/Content/Highlights); state = z-movement | Our z-index tokens + material-strength-as-depth align; document the 4-layer model |
| Jagatap "This SwiftUI Trick Feels Illegal" | Shared-element identity, stagger 40–80ms, haptics lead 10–20ms, mass-object buttons | Continuity + stagger + haptic lead in interaction recipes |
| Sorrell / Windcraft / ShatteredGlass | The optical recipe (SDF refraction, aberration, saturation lift, Fresnel rim) | v2 compositing; shader-grade work deferred to Phase 8 with honesty |

## 3. The z-stack model (adopted)

iOS states communicate by moving a 4-layer stack, not by adding layers:
**Shadow / Material / Content / Highlights**. Our design system maps 1:1:

| Apple layer | Our system |
|---|---|
| Shadow | ShadowEngine (ambient+key, adaptive opacity) + z-index tokens |
| Material | GlassEngine v2 (blur + tint + vibrancy + refraction approximation) |
| Content | Content layer (labels/icons, vibrancy auto-flip) |
| Highlights | Specular + rim layers (LIGHTING_MODEL_SPEC) |

State = moving on the z-axis (compress+spring on press, dim+expand on present). This is the
mental model every future surface follows.

## 4. Honest conclusion (answers the phase question)

**Currently our implementation is closer to a themed Android app** — the *language* is defined
and tokenized, but the *behaviors* that read as "modern OS" are not yet rendered:
- no content-adaptive tint or luminosity lift (glass reads flat),
- no specular/rim lighting (depth reads weak),
- no saturation lift (blur reads muddy),
- springs were mis-tuned (0.7 damping ≠ Apple's 1.0/0.8),
- micro-interactions exist but lack the 80/160ms asymmetry + haptic lead.

**Gap → required changes** are in AUDIT.md (keep/modify/replace) and the two engine specs.
The refinements are compositing + parameter work — no architecture change, no Phase 3 impact.

## 5. What we deliberately do NOT copy

- No Apple assets, icons, sounds, fonts, code, or proprietary shaders.
- No MediaProjection/capture hacks; window-blur + compositing only.
- No literal SDF shader in v2 (Phase 8 GPU work, budgeted); v2 = the compositing approximation
  that delivers the *read* (edge emphasis, saturation lift, specular, rim) within 120Hz budget.
