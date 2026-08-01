# ADR-0011: Design anchor — iOS 26 Liquid Glass + full token abstraction

- Status: Accepted
- Date: 2026-08-01
- Decision: D12

## Context

The visual system (glass panels, translucency, blur, typography, dynamic tinting) descends from the design language choice. iOS 26's Liquid Glass is fully documented with a year of ecosystem knowledge; iOS 27 is announced but less documented. "27-inspired" could mean re-anchoring or layering refinements.

## Decision

- **Anchor: iOS 26 Liquid Glass** — the documented, stable reference.
- **Full token abstraction:** every visual renders through design tokens (color, blur, type, spacing, motion, luminance). Themes are token sets, switchable from companion/WebUI presets.
- **iOS 27 lands as a new token set + component polish — never a rewrite.** Phase 0 researches the 27 delta; the design system's token grammar is the interface that absorbs it.

## Consequences

- Design-system code is token-driven; hardcoded styles are a review failure (conventions §2).
- iOS 27 adoption is a low-risk additive change gated by Phase 0 research.
- Token grammar (naming, scale, fallbacks) is a load-bearing interface — designed in Phase 2 with care.
- The design system (`libs/design`) is a bounded context with no business logic (ARCHITECTURE.md §2).
