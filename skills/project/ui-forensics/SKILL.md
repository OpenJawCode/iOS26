---
name: project-ui-forensics
description: "PROJECT-SPECIFIC (iOS26). Visual-fidelity methodology without vision tools: how this project measures reference fidelity (Apple UI) and its own surfaces — pixel sampling, uiautomator hierarchy dumps, token diffing against GLASS_ENGINE_V2/MOTION specs, screenshot baselines. Use for design reviews, fidelity checks, and BEFORE/AFTER visual validation."
license: GPL-3.0
project: iOS26
source: project-specific (VISUAL_FIDELITY_REPORT.md, MATERIAL_RESEARCH.md, AUDIT.md)
---

# UI Forensics (no-vision-tool methodology)

## When no image analysis is available

1. **Hierarchy first**: `uiautomator dump` → text/semantics/bounds. Layout structure,
   spacing (bounds math), labels, state — all readable without pixels.
2. **Pixel sampling with PIL** for color/contrast: dominant-color histograms per screen
   region (top-right quadrant etc.), scrim dimming checks (25% black ≈ 0.75× channel),
   accent-vs-background deltas. Store baselines in `device-tests/` (see gallery baselines).
3. **Window/state proofs**: `dumpsys window windows` (window present, flags, blur radius),
   `dumpsys SurfaceFlinger --list`, `dumpsys gfxinfo` frames — proves what's actually on
   screen and its cost.
4. **Token diffing**: any visual claim must map to tokens (GLASS_ENGINE_V2.md, MOTION.md,
   tokens.json) — if it isn't a token, it isn't the design system (ADR-0011/0031).

## Reference-side analysis (Apple)

Same toolkit against reference screenshots: sample regions, measure grid ratios, edge bands
(rim ~1px, specular 8-14% alpha), blur/saturation evidence — cross-check against
MATERIAL_RESEARCH.md §9 (the four-pass recipe) and the vendored HIG corpus
(skills/vendored/apple-skills-hig/materials.md, color.md, motion.md).

## Deliverable shape

Every fidelity claim: `metric → measured value → token/spec reference → pass/fail`. Log
failures in bugs-log.md; wrong approaches in mistake-log.md.
