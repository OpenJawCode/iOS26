# APPLE_REFERENCES.md — Apple references (principles only, ADR-0012)

> This project implements an iOS-26/27-inspired Android experience. We study Apple's
> published principles and publicly documented behavior; we NEVER copy assets, fonts,
> sounds, code, or proprietary shaders (ADR-0012, ground rule #5).

## Primary Apple sources (public, cited in repo research)

| Source | What it informs | Where digested |
|---|---|---|
| HIG — Materials / Color / Motion / Accessibility | glass hierarchy, adaptive tint, motion doctrine, a11y minimums | MATERIAL_RESEARCH.md, MOTION_RESEARCH.md, apple-skills-hig corpus |
| WWDC18 "Designing Fluid Interfaces" | interruptibility, velocity, presentation-value animation | MOTION_RESEARCH.md |
| WWDC23 "Animate with springs" | duration+bounce doctrine (damping 1.0/0.8) | MOTION_RESEARCH.md |
| WWDC25 Liquid Glass (219/220/284/323/310) | glass principles, refraction, adaptive shadows, vibrancy | MATERIAL_RESEARCH.md, GLASS_ENGINE_V2.md |
| apple.com/ios (iOS 27 page) | Liquid Glass refinements (uniform refraction, contrast, ultraclear→tinted) | CONTROL_CENTER_RESEARCH.md |
| Apple HIG PDF (vendored) | full HIG text | skills/vendored/platform-design-skills-ios/Apple_HIG.pdf |
| HIG markdown corpus (vendored) | greppable facts: materials, motion, color, accessibility | skills/vendored/apple-skills-hig/ |
| iOS 18 CC redesign (MacStories, Android Authority, MacRumors — third-party reviews) | Control Center structure, gestures, pages | docs/phase3/CONTROL_CENTER_RESEARCH.md |

## How we map Apple → Android (always explicitly)

- GlassEngine v2 implements the *optical recipe* (saturation lift, specular, rim, adaptive
  tint, content-aware shadow) within our budget — shader-grade SDF refraction is Phase 8
  work, budgeted, never faked.
- MotionEngine v2 implements spring physics and interruptibility via Compose Animatable.
- Tokens carry every Apple-derived value (no magic numbers) — ADR-0011/0031.
- Control Center: iOS-17 layout DNA + iOS-26/27 material, delivered through the LSPosed
  hook chain (ADR-0032-0038) — see CONTROL_CENTER_RESEARCH.md §6 for honest trade-offs.

## Attribution & licensing posture

- Community skill vendors: MIT/Apache-2.0, provenance in skills/vendored/*/SOURCE.md.
- Project skills: GPL-3.0 (repo license), marked PROJECT-SPECIFIC.
- Apple documents are copyright Apple Inc.; vendored copies are for internal research use
  (HIG PDF ships under its own terms with the MIT-licensed skill collection).
- Any design decision that claims "Apple does X" must cite one of the digests above or the
  vendored corpora — no invented references (ground rule: never invent).
