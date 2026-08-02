# TOKENS.md — Design Token Documentation

> Source of truth: `libs/design/tokens/tokens.json` (schema-validated via `libs/schema` — D-P2.4).
> Generated: `Tokens.kt` via `:libs:design:generateDesignTokens` (250+ values (material/specular/vibrancy/tintBias/refraction/transition/state); CI checks freshness).
> **Nothing is hardcoded. Every visual value in the project derives from these tokens.**

## 1. Architecture

```
tokens.json ──(tokens.schema.json validation)──> generateDesignTokens ──> Tokens.kt (generated)
     │                                                                    │
     └── single source of truth                          consumed by ThemeEngine, engines, components
```

Three tiers (Kowalski doctrine: hierarchy, one source):
1. **Core** — raw palette (iOS system colors), type families, base scales. Never referenced directly by components.
2. **Semantic** — mode-resolved roles (light/dark): `background`, `labelPrimary`, `glassFill`, `accent`, `scrim`…
3. **Component mapping** — components read semantic tokens only (via `TokenSet`), so future theming is automatic.

## 2. Groups (tokens.json)

| Group | Contents | Notes |
|---|---|---|
| `color.core` | iOS 26 system palette (12 colors + grays 1–6, black/white, accents per mode) | Liquid Glass palette anchor (ADR-0011) |
| `semantic.light/dark` | 19 roles per mode: backgrounds, labels 1–4, separators, accent, glass fills/strokes/highlights, scrims, widget tint, sheet | ThemeEngine resolves mode |
| `type` | iOS type scale: largeTitle→caption2 (size/lineHeight/weight/letterSpacing) | SF-stand-in open font (ADR-0012) |
| `spacing` | 4pt-based scale 0–64 + `touchTarget` 48dp (a11y) | Kowalski spacing discipline |
| `radius` | small→pill + `squircleFactor` (0.2237 ≈ iOS icon radius ratio) | |
| `elevation` | none→floating: y/radius/alpha triples | ShadowEngine maps to Compose shadows |
| `blur` | none/subtle/standard/prominent/heavy + **caps** (`maxWindow` 30, `maxRenderEffect` 50) | budget enforcement (PERFORMANCE.md) |
| `zIndex` | background→toast layering model | |
| `grid` | springboard: 6 columns, icon 60dp, gutters, dock, folders 3×3, page dots, app library 5 | Springboard spec (§ COMPONENTS.md) |
| `motion` | durations (0–500ms), curves (cubic-bezier), springs (damping/stiffness), `reducedMultiplier` 0.5 | MotionEngine; reduced-motion a11y |
| `haptics` | iOS feedback types (selection/light/medium/heavy/success/warning/error) | HapticEngine mapping |
| `state` | pressedOverlay (light/dark), disabledOpacity 0.4, focusRing, hoverOverlay | interaction states |

## 3. Adding a token

1. Edit `tokens.json` (+ extend `tokens.schema.json` if the shape changes).
2. `./gradlew :libs:design:generateDesignTokens` — commit the regenerated file.
3. Use it through `Tokens.*` — never inline values in code.

## 4. Conventions

- Hex `#RRGGBB` / `#AARRGGBB`; dp/sp units; bezier as 4-float arrays; springs as {damping, stiffness}.
- Generated code is committed (agents + CI rely on it) and CI verifies freshness.
- `TokenSet` (ThemeEngine) exposes semantic roles; components take values from `LocalTokenSet.current`.
