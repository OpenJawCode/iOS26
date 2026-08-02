# PHASE2.md — Design System, Motion System & Springboard Foundation (Phase 2)

> Status: **plan approved** (opened 2026-08-02). Goal per mandate: build the visual language every future module inherits — NOT a launcher. Design primitives before features. Design tokens are the single source of truth.

## 1. Architecture review (Phase 0/1 artifacts + ADRs)

All 21 ADRs + Phase 0/1 findings reviewed. Standing design intact. Phase 2 introduces androidx/Compose (the Phase 1 "no androidx" posture was explicitly temporary). Four execution decisions below; two are forced by existing ADRs, two are proposed.

### 1.1 Forced decisions (ADR-grounded, no ambiguity)

- **D-P2.1 — Custom token system, NOT the Compose Styles API.** The Styles API requires compileSdk 37 + alpha Compose; ADR-0017 freezes the platform at API 33. Verdict from the `styles` skill: philosophy adopted (token-driven, style objects, state styling), API rejected. Fallback: CompositionLocal theme + typed token objects + per-component `style` parameters where they add value.
- **D-P2.2 — Glass reality (ADR-0005/0011 grounded).** True live-behind-window blur on API 33 = `Window.setBackgroundBlurRadius` (API 31+) for overlay windows + `RenderEffect` for in-window content. Backdrop *sampling* for wallpaper-adaptive tinting = wallpaper bitmap → palette → scrim overlays (no MediaProjection, no capture hacks). Capability matrix documented in the Glass Engine.

### 1.2 Proposed decisions (reviewed + recommended)

- **D-P2.3 — androidx + Compose BOM 2026.06.01 enter the graph; NO material3.** Scope: compose-ui, foundation, runtime, animation, ui-tooling, ui-test, palette. Rationale: M3's token/theme system would fight the custom iOS-semantics model; its components are the wrong shape; foundation provides layout/a11y/animation primitives. Trade-off: we build a11y scaffolding ourselves (roles, focus, contrast) — deliberate, token-backed.
- **D-P2.4 — Token pipeline: `tokens.json` (schema-validated via `libs/schema`) → generated Kotlin** via a narrow generator in build-logic. This is the "nothing hardcoded" guarantee with a real source of truth (upgrades D-P1.1's test-enforcement for tokens specifically; general schema→Kotlin codegen still lands with the WebUI).

## 2. Design doctrine (sources that influence every decision)

| Source | Applied as | Influences |
|---|---|---|
| Apple HIG + Liquid Glass | Visual reference; glass material language; gesture vocabulary | Glass Engine, Motion, Gesture Language, Springboard spec |
| Emil Kowalski design philosophy | Restraint, hierarchy, one focal point per surface, spacing discipline, tasteful motion | Token scales, component density, Motion Engine |
| `frontend-design` skill | Anti-generic aesthetics, intentionality, memorable direction (exception: font choice per ADR-0012) | Color direction, typography pairing, layout primitives |
| `styles` skill | Token-driven styling philosophy (API rejected, D-P2.1) | Component architecture, style objects |
| `adaptive` skill | Grid systems, screenshot-testing workflow, form-factor discipline | Grid system, component testing, Springboard grid math |
| Accessibility (WCAG 2.2 AA) | Contrast pairs as tokens, focus, touch targets, reduced motion, text scaling | Color tokens, interaction states, Motion tokens |
| Android performance engineering (perfetto skills) | Budgets-first; measure before optimizing | PERFORMANCE.md, 120Hz budgets, blur caps |

## 3. Deliverable map (everything token-driven; nothing hardcoded)

```
libs/design/                     NEW module (D-P2.3 convention: ios26.library + compose)
├── tokens/                      generated from tokens.json (D-P2.4)
│   ├── color/                   core palette → semantic roles → component mappings
│   ├── type/                    type scale (SF-stand-in family, token sizes/weights)
│   ├── spacing/ radius/ elevation/ grid/ z-index/ blur/ motion/ haptics/
├── theme/                       ThemeEngine: TokenSet switching (light/dark/glass intensity),
│                                CompositionLocal plumbing, dynamic color (wallpaper → palette)
├── engines/
│   ├── glass/                   GlassEngine (window blur + RenderEffect + scrims, capability matrix)
│   ├── blur/                    BlurEngine (radius tokens, budget caps)
│   ├── vibrancy/                VibrancyEngine (tint/saturation compositing over glass)
│   ├── material/                MaterialEngine (surfaces, layers, tint hierarchy)
│   ├── shadow/                  ShadowEngine (elevation tokens → shadows, 120Hz-cheap)
│   ├── motion/                  MotionEngine (curves, durations, springs — token-driven)
│   ├── haptic/                  HapticEngine (profile tokens → VibratorManager)
│   ├── animation/               AnimationEngine (token → Animatable/AnimSpec wiring)
│   └── theme/                   (see theme/)
├── components/                  Component library v1 — ALL token-derived, auto-theming
│   Button, Card, List, Switch, Slider, Toggle, NavigationBar, Dock, QuickSettingsTile,
│   ControlCenterCard, Notification, Widget, Search, Folder, AppIcon, ContextMenu,
│   Sheet, Popover, LockScreenComponent
└── springboard/                 SpringboardSpec: grid math, icon spec, dock, folders, pages
```

**Accessibility:** contrast pairs as tokens (AA), focus indicators token-driven, 48dp targets, text scaling (sp everywhere), reduced-motion scheme (tokens swap), haptics-off support.

## 4. Performance (designing for 120Hz from day one)

- Frame budget: **8.33 ms/frame @ 120Hz**; display modes 60/90/120/144 (device: Edge 20 "berlin" — verified in T10).
- **Blur budgets**: window-blur radius capped by token (GlassEngine enforces); full-screen blur = single layer; never stack blur.
- **Overdraw**: single-layer glass composition; no overlapping translucent surfaces; GPU overdraw debug pass in T10.
- **Jank/frame pacing**: perfetto trace + `FrameMetrics` on the gallery; budgets documented in PERFORMANCE.md BEFORE features.
- No premature optimization: budgets defined, measured at T10, enforced from Phase 3.

## 5. Execution plan

| # | Task | Exit criterion |
|---|---|---|
| T1 | Design doctrine + artifact review (this document) | plan approved |
| T2 | androidx + Compose BOM entry; token pipeline (tokens.json + schema + generator) | generated tokens compile |
| T3 | Core token sets + ThemeEngine (light/dark/glass-intensity) | TokenSet switching green |
| T4 | Glass/Blur/Vibrancy/Material/Shadow engines + capability matrix + budgets | engine tests green |
| T5 | Motion + Haptic + Gesture engines (all token-driven) | motion tests green |
| T6 | Component library v1 (20 components, token-derived) + screenshot tests | all components render token-driven |
| T7 | Springboard specification (grid math, icon spec, dock/folders/pages) + prototype | spec doc + working grid prototype |
| T8 | Dynamic color + wallpaper adaptation engine | palette→tokens pipeline green |
| T9 | Component Gallery (dev-only app surface) + interactive prototypes | gallery runs on device |
| T10 | Performance: 120Hz budgets, overdraw pass, perfetto smoke on device | budgets documented + measured |
| T11 | Docs: COMPONENTS/MOTION/TOKENS/PERFORMANCE/DESIGN_REVIEW + ADRs 0022-0026 + CONTEXT/ROADMAP/AGENTS | docs parity |
| T12 | Phase-end: design review, motion review, risks, debt, Phase 3 recommendation | explicit recommendation |

## 6. ADRs anticipated

- ADR-0022: Token architecture & pipeline (generated, schema-first)
- ADR-0023: Glass engine & blur strategy on API 33 (window blur + RenderEffect, budget caps)
- ADR-0024: Motion system (token-driven curves/springs, reduced-motion)
- ADR-0025: Component library architecture (token-derived, no M3, auto-theming)
- ADR-0026: Springboard grid spec (if it diverges from iOS reference beyond tokens)

## 7. Known risks

- R-A: behind-window blur cost on 778G (budgeted; fallback: scrim-only glass mode)
- R-B: Compose BOM 2026.06.01 alpha/beta API churn (pinned; verify at T2)
- R-C: 20 components is a large surface — each is a lean primitive (depth over breadth, per component)
- R-D: screenshot-test baseline volume (component gallery screenshots; Roborazzi or Compose Preview testing — decide at T6)

## 8. Phase-end deliverables

Updated ADRs · Design Review · Motion Review · Token documentation · Performance considerations · Remaining risks · Technical debt · Phase summary · **Explicit Phase 3 recommendation** (never automatic).
