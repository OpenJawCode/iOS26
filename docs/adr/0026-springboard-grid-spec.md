# ADR-0026: Springboard grid specification

- Status: Accepted · Date: 2026-08-02 · Related: COMPONENTS.md §2

## Context
The springboard layout must be a pure function of tokens (density/rotation safe) and match the
reference device (1080×2400 @ ~446dpi, 20:9).

## Decision
Grid constants are tokens: 6 columns, 60dp icons, squircleFactor 0.2237, 24dp gutter, 20dp
margin, 88dp dock, 3×3 folders, page dots 7/8dp, app-library 5 columns. Rows derived from
screen height. Layout implementation lands Phase 3; the spec + math are defined now.

## Consequences
Phase 3 implements against a frozen spec; density changes recompute automatically.
