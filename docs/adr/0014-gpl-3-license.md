# ADR-0014: GPL-3.0 everywhere

- Status: Accepted
- Date: 2026-08-01
- Decision: D15

## Context

The Xposed/LSPosed/Magisk ecosystem is overwhelmingly GPL-3.0. The system layer (hooks, magisk module) lives in that ecosystem; the app layer (launcher, companion, WebUI, libs) is clean-room code that could be permissive. Options: GPL everywhere, dual-license split, Apache everywhere.

## Decision

**The entire monorepo is GPL-3.0.** No file-level dual-licensing.

## Consequences

- Zero friction if we ever integrate ecosystem code; contributors license their work under GPL-3.0 (CONTRIBUTING §9).
- App-level code cannot be reused in closed-source projects — accepted for an OSS project whose identity is the system layer.
- Dependency policy: no GPL-incompatible dependencies (conventions §9).
- One license = no boundary management for humans or agents.
