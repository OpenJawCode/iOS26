# ADR-0006: Schema-validated file config store

- Status: Accepted
- Date: 2026-08-01
- Decision: D7

## Context

Every component consumes configuration; the hard constraint is the SystemUI process — hooks executing there must never depend on another process being alive. Host-app databases (DataStore/Room + binder) couple a critical process to an app's lifecycle. Hand-edited unvalidated JSON invites corruption. The root-mod ecosystem standardizes on file-based config.

## Decision

All configuration and shared state live in a **schema-validated file store** under `/data/adb/ios26/` (provisioned by the Magisk module).

- **Single source of truth:** JSON Schema definitions in `libs/schema`; Kotlin models generated (kotlinx.serialization) and TS zod types generated — never hand-maintained (committed, CI-verified fresh).
- **One shared library owns all of it** (`libs/config`): read, parse, validate, atomic writes (temp+rename), FileObserver live-reload, event files, versioned migrations. Zero process coupling; safe to call from any process including SystemUI.
- Write discipline: single-writer via atomic rename; readers are always safe.
- Debuggability is a feature: every value is a hand-editable JSON file.

## Consequences

- SystemUI-safe by construction; no lifecycle coupling anywhere.
- The `libs/config` module is the **deep module** of the system: small interface, all behavior hidden behind it; its API is sacred (AGENTS.md).
- No transactions — mitigated by schema validation, atomic rename, and single-writer discipline.
- Migrations must be versioned from day one (store root carries a schema version).
