# ADR Log

Every significant architectural decision is recorded here. Numbering is sequential; never renumber. Statuses: **Proposed → Accepted → Superseded**. Superseded ADRs are never deleted — they point to their successor.

## Index

| ADR | Title |
|---|---|
| [0001](0001-curated-surface-scope.md) | Curated surface scope |
| [0002](0002-platform-rooted-android-13.md) | Platform: rooted Edge 20 on stock Android 13 |
| [0003](0003-compose-everywhere.md) | Compose everywhere |
| [0004](0004-monorepo-release-train.md) | Monorepo + single release train |
| [0005](0005-control-center-overlay-host-thin-hook.md) | Control Center: overlay host + thin hook |
| [0006](0006-schema-validated-config-store.md) | Schema-validated file config store |
| [0007](0007-per-surface-lsposed-modules.md) | Per-surface LSPosed modules |
| [0008](0008-hybrid-packaging.md) | Hybrid packaging (Magisk for system bits) |
| [0009](0009-companion-settings-hub.md) | Companion app = settings hub |
| [0010](0010-webui-on-device-server.md) | WebUI: on-device server + React SPA |
| [0011](0011-ios26-design-anchor-tokens.md) | Design anchor: iOS 26 + token abstraction |
| [0012](0012-original-assets-personal-import.md) | Original assets + on-device personal import |
| [0013](0013-layered-testing-tiers.md) | Layered testing (three tiers) |
| [0014](0014-gpl-3-license.md) | GPL-3.0 everywhere |
| [0015](0015-widget-hosting-v1.md) | Full widget hosting in v1 |
| [0016](0016-app-library-spotlight-depth.md) | App Library auto-categories + rich Spotlight |
| [0017](0017-api-level-policy.md) | API level policy (33) |
| [0018](0018-observability-no-third-party-sdks.md) | Observability: no third-party SDKs |
| [0019](0019-hook-seam-signaling.md) | Hook seam & file-event signaling |
| [0020](0020-ci-cd-model.md) | CI/CD model |
| [0021](0021-config-store-access-model.md) | Config store access model (amends ADR-0006) |
| [0022](0022-token-architecture.md) | Token architecture & pipeline |
| [0023](0023-glass-engine-blur-strategy.md) | Glass engine & blur strategy |
| [0024](0024-motion-system.md) | Motion system (token-driven) |
| [0025](0025-component-library-architecture.md) | Component library (no M3) |
| [0026](0026-springboard-grid-spec.md) | Springboard grid specification |
| [0027](0027-glass-engine-v2.md) | Glass Engine v2 architecture |
| [0028](0028-material-compositing-pipeline.md) | Material compositing pipeline |
| [0029](0029-motion-system-architecture.md) | Motion system architecture (v2) |
| [0030](0030-performance-budgets.md) | Performance budgets (binding) |
| [0031](0031-component-rendering-contracts.md) | Component rendering contracts |
| [0032](0032-systemui-injection-strategy.md) | SystemUI injection strategy |
| [0033](0033-hook-architecture.md) | Hook architecture |
| [0034](0034-rendering-bridge.md) | Rendering bridge |
| [0035](0035-state-synchronization.md) | State synchronization |

## Template

```markdown
# ADR-NNNN: Title

- Status: Proposed | Accepted | Superseded (by ADR-…)
- Date: YYYY-MM-DD

## Context
Why this decision exists — forces, constraints, alternatives considered.

## Decision
What we decided, precisely enough to implement.

## Consequences
What this decision makes easier, harder, and what it commits us to.
```

## Rules

1. A decision without an ADR is a guess. Architecture changes require an ADR *before* code.
2. Supersede, never delete. Edit the old ADR's status only.
3. New ADRs reference the interview decision (D#) in CONTEXT.md for traceability.
