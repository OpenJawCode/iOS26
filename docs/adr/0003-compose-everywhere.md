# ADR-0003: Compose everywhere

- Status: Accepted
- Date: 2026-08-01
- Decision: D4

## Context

The launcher is a performance-critical UI on a Snapdragon 778G; the Liquid Glass aesthetic (live blur, translucency, dynamic tinting) is a rendering problem. Alternatives: classic Views (battle-tested launcher perf playbook, verbose, fading ecosystem), hybrid (two paradigms forever), all-Compose.

## Decision

All Android UI (launcher, companion app, any future in-app surfaces) uses **Jetpack Compose**. Glass effects via Canvas + `RenderEffect` blur + custom drawing.

Performance is a first-class constraint: lazy layouts, stable lambdas, tokens-driven blur budgets, no off-screen blur layers. Enforced by Phase 8 budgets and macrobenchmark baselines — the toolkit choice buys velocity, not an excuse.

## Consequences

- Single paradigm, one mental model for humans and agents; best agent velocity and test tooling (Roborazzi, previews).
- Perf on the 778G requires deliberate discipline; baked into conventions (§2) and Phase 8.
- LSPosed hooks remain Compose-free (ADR-0007) — Compose applies to app processes, not injected SystemUI surfaces.
- XML migration not required (greenfield) but `migrate-xml-views-to-jetpack-compose` skill available for any legacy bits.
