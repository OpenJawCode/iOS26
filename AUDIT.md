# AUDIT — Phase 2 Implementation vs. Apple's Modern UI Language

> Verdict format: **keep** (architecture sound) / **modify** (sound core, needs refinement) /
> **replace** (wrong approach). Every verdict carries a reason. Ground truth: the forensic
> research docs (MATERIAL_RESEARCH.md, MOTION_RESEARCH.md, APPLE_UX_ANALYSIS.md).

## 1. Per-system verdicts

| System | Verdict | Why |
|---|---|---|
| Token pipeline (JSON → generated Kotlin) | **keep** | Proven architecture; single source of truth; nothing to change |
| Token content (213 values) | **modify** | Missing the lighting/material vocabulary: specular, vibrancy, tint-bias, DoF, shadow-key (see specs) |
| ThemeEngine (TokenSet, mode, glass intensity) | **keep + modify** | Sound; add lighting tokens to TokenSet surface |
| GlassEngine | **modify** | Flat fill + 1dp stroke is *flat* — needs specular, rim, tint-bias, vibrancy layers (GLASS_ENGINE_SPEC) |
| BlurEngine/`Modifier.blur` | **keep** | Correct mechanism; missing the post-blur vibrancy pass |
| ShadowEngine | **modify** | Single shadow per elevation; needs ambient+key split + DoF backdrop for modals (LIGHTING_MODEL_SPEC) |
| MotionEngine | **keep + modify** | Curves/springs correct; needs transition presets (entrance/exit) + velocity-inheritance recipes |
| HapticEngine | **keep** | Mapping sound; verify against research on timing |
| Components (20) | **modify** | Structure correct; interactivity depth v1-lean: wire micro-interactions (press → state → motion), add transitions |
| Gallery | **keep** | The canary; extend with colored backdrops + lighting demo surfaces |
| DynamicColorEngine | **keep + modify** | Palette accent works; extend to tint-bias (backdrop color blend) |
| **Replace** | — | Nothing wholesale — the architecture survives research; this is refinement, not rebuild |

## 2. Honest self-assessment (the questions asked)

| Question | Current answer | Gap |
|---|---|---|
| Are glass effects realistic? | **No** — flat fill + stroke reads as "translucent card", not glass | specular sheen, rim, refraction hint, tint-bias missing |
| Is blur physically convincing? | **Partially** — real blur, but desaturated/muddy (no vibrancy lift) | saturation/luminance pass over blur |
| Is there enough depth? | **Weak** — shadows minimal, no DoF, material strength flat | lighting model + layered material strength |
| Are lighting effects missing? | **Yes** — no specular/rim/ambient-key model at all | LIGHTING_MODEL_SPEC |
| Are animations premium? | **Functional** — press scale + switch track only; no transitions, no entrance/exit vocabulary | transition presets + micro-interaction recipes |
| Modern OS or themed app? | **Closer to themed app today** — the language is defined but not yet *rendered* with conviction | this phase's refinements |

## 3. Gap analysis: Current → Target → Required changes

| Dimension | Current | Target | Required changes |
|---|---|---|---|
| Glass fill | flat alpha | tint-bias + vibrancy + specular + rim | GlassEngine v2 layers; tokens `specular*`, `tintBias`, `vibrancy*` |
| Blur | plain gaussian | blur + saturation/luminance lift | vibrancy compositing (BlendMode), token-capped |
| Depth | token shadows only | material strength + ambient/key + DoF modals | lighting tokens; ShadowEngine v2; modal backdrop pass |
| Motion | springs for press | full transition vocabulary + velocity inheritance | MotionEngine transition presets; component wiring |
| Micro-interactions | press scale | state-driven: press → color/scale → release → haptic → settle | interaction recipes (documented + wired) |
| Accessibility | tokens exist | reduce-transparency scheme (iOS analog) | `glassIntensity=Subtle` auto on reduce-transparency |
| Measurement | gallery baseline | per-surface baselines | extend gallery; record in PERFORMANCE.md |

## 4. Execution note

All changes are token-first (tokens.json additions validated by schema), engine-second,
component-wiring third. Nothing in the Phase 3 hook plan changes; this phase is purely the
visual/interaction refinement layer.
