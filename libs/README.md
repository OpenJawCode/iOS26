# libs/

Shared libraries — the bottom of the dependency graph (ARCHITECTURE.md §3).

| Module | Purpose | Owner |
|---|---|---|
| `config` | **The deep module** (ADR-0006): schema-validated file store, atomic writes, live reload, events, migrations. API is sacred. | config-owner |
| `schema` | JSON Schema definitions — the single source of truth; codegen to Kotlin + TS | config-owner |
| `design` | Design tokens + Liquid Glass component kit (ADR-0011) | design-owner |
| `domain` | Pure models: apps, categories, layouts, theme (no Compose) | domain-owner |
| `icons` | Icon pack runtime: mapping, parsing, bundled + user packs | domain-owner |

Dependency rules: config → (nothing); everything → config. No cycles, enforced in CI from Phase 9.
