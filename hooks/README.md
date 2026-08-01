# hooks/

The LSPosed layer — per-surface modules (ADR-0007). No Compose, no UI, no launcher/companion imports. Hooks emit events via the seam (ADR-0019); hosts render.

| Module | Purpose |
|---|---|
| `hooks-api` | Seam contract — typed capabilities + events (depended on by launcher + hooks) |
| `hooks-common` | Shared hook infra, embedded at build time per module |
| `control-center` | Moto SystemUI gesture adapter (top-right swipe intercept) |

Owner: hooks-owner. The Moto adapter is the only place My UX internals may be referenced (Phase 0 survey). Tests: hooks-common logic at Tier 1; adapter verified Tier 3 only.
