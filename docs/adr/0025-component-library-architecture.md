# ADR-0025: Component library — pure foundation, token-derived, no M3

- Status: Accepted · Date: 2026-08-02 · Related: D-P2.3 (interview), ADR-0011

## Context
20+ components needed; material3 would provide a11y scaffolding but introduces a second token
system fighting the custom model.

## Decision
All components built on compose foundation primitives with iOS semantics; every visual value
from `LocalTokenSet`/`Tokens.*`; roles/focus/touch-targets/reduced-motion handled token-backed.
Auto-theming by construction.

## Consequences
We own a11y scaffolding (documented, token-backed); zero M3 leakage; theming = TokenSet swap.
