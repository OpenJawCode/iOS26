# ADR-0023: Glass engine & blur strategy on API 33

- Status: Accepted · Date: 2026-08-02 · Related: D-P2.2, ADR-0005/0011, spike R7

## Context
Liquid Glass needs real behind-content blur; on API 33 only window-level
(`Window.setBackgroundBlurRadius`, API 31+) and in-window (`Modifier.blur`/RenderEffect) exist.

## Decision
GlassEngine centralizes three paths: window blur (host-owned, overlay panels), in-window blur
(backdrops), scrim+tint fallback. Radius caps tokenized (`Blur.maxWindow` 30, `maxRenderEffect`
50); one blur layer per surface; a token flips any surface to scrim mode under load.

## Consequences
CC overlay (Phase 3) gets real behind-blur; budgets enforced by tokens; graceful degradation
is one token away.
