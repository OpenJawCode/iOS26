# ADR-0022: Token architecture & pipeline

- Status: Accepted · Date: 2026-08-02 · Related: D-P2.4, ADR-0006/0011

## Context
"Nothing hardcoded; everything from tokens" requires a single source of truth with drift-proof
enforcement. Compose Styles API rejected (compileSdk 37 requirement vs ADR-0017 freeze).

## Decision
`libs/design/tokens/tokens.json` is the source of truth (schema-validated via `libs/schema`);
`:generateDesignTokens` emits typed `Tokens.kt` (213 values, committed, CI freshness-checked).
Three tiers: core palette → semantic roles (mode-resolved) → component mapping via `TokenSet`.

## Consequences
Adding a token = JSON + regen; theming = TokenSet swap (ADR-0011); TS contracts reuse the JSON
in Phase 6. The generator is deliberately narrow (tokens only).
