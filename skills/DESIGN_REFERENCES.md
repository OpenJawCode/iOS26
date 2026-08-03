# DESIGN_REFERENCES.md — Design references available to agents

> Everything below is either vendored in this repo, in the global store, or a canonical
> upstream source cited for reference. No Apple assets are copied (ADR-0012) — principles
> and documents only.

## Vendored (skills/vendored/)

- **Apple_HIG.pdf** — official Apple Human Interface Guidelines PDF (MIT-vendored with
  ehmo/platform-design-skills, dc2be82). The primary HIG reference.
- **apple-skills-hig/** — greppable HIG corpus as markdown: materials.md, motion.md, color.md,
  accessibility.md, layout.md, modality.md, designing-for-ios.md, lists-and-tables.md,
  app-icons.md, buttons.md, charts.md, feedback.md, menus.md, navigation-bars.md, + more.
- **ios-design-guidelines/SKILL.md** — 1,083-line rule set derived from HIG (touch targets,
  Dynamic Type, Dark Mode, a11y).
- **apple-skills-liquid-glass/SKILL.md** — Liquid Glass design principles (iOS 26+): glass
  hierarchy, morphing, accessibility adaptation, Icon Composer notes.

## In-repo (project research, always authoritative for OUR system)

- GLASS_ENGINE_V2.md / GLASS_ENGINE_SPEC.md — our glass engine spec.
- MATERIAL_RESEARCH.md — Liquid Glass forensics (WWDC25 sessions, RE measurements).
- MOTION_RESEARCH.md / MOTION.md — motion system (WWDC18/23 springs, timings).
- LIGHTING_MODEL_SPEC.md — speculars/rim/lighting.
- TOKENS.md + libs/design/tokens/tokens.json — the design token source of truth (ADR-0011).
- COMPONENTS.md, REFINED_DESIGN_SYSTEM.md, DESIGN_REVIEW.md, VISUAL_FIDELITY_REPORT.md,
  AUDIT.md — component contracts, review history, fidelity gaps.
- docs/phase3/CONTROL_CENTER_RESEARCH.md — Apple Control Center analysis (this phase).

## Canonical upstream (link-only, not vendored)

- developer.apple.com/design/human-interface-guidelines — HIG (web, current)
- developer.apple.com/design — Apple Design (specs, WWDC sessions)
- WWDC25 Liquid Glass sessions 219/220/284/323/310 (transcripts via Apple)
- developer.android.com/develop/ui/compose/performance — Compose perf docs
- developer.android.com/topic/performance/baselineprofiles — Baseline Profiles docs

## Global store

frontend-design, canvas-design, brand-guidelines, design-taste-skill-pack (taste-design),
algorithmic-art — general design craft; use for anything outside iOS/Android platform rules.
