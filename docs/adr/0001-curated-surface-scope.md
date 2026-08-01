# ADR-0001: Curated surface scope

- Status: Accepted
- Date: 2026-08-01
- Decision: D1

## Context

The project recreates "an iOS 26/27-inspired Android experience." A full-shell recreation (springboard, control center, lock screen, status bar, notifications, live activities, dynamic island) on stock Android 13 is a decade of maintenance; a launcher-only skin is "just a theme," which the project explicitly rejects. The interview's central scope question: which surfaces, and at what depth.

Alternatives considered: full-shell parity (max ambition, max maintenance), launcher-centric v1 (robust but not "not a theme"), surface-by-surface ROI triage.

## Decision

v1 delivers a **curated subset**: Springboard (grid, dock, folders, pages, widgets), App Library, Spotlight, and Control Center — via the full module stack (launcher + LSPosed + Magisk + overlays), so the result is a system experience, not a skin.

**Deferred (explicitly out of v1, re-evaluated later):** lock screen, status bar, notifications, live activities, dynamic island.

## Consequences

- v1 scope is bounded and shippable; the module architecture (per-surface contexts, ADR-0007) absorbs deferred surfaces later without rework.
- Control Center is the hardest in-scope surface and drives the LSPosed architecture (ADR-0005).
- "Curated" must resist scope creep: new surfaces enter via the ADR process, not ad hoc.
