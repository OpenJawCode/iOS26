# PHASE1.md — Architecture & Toolchain (Phase 1)

> Status: **in progress** (opened 2026-08-02). Phase 0 closed. Phase 1 builds the production foundation: no features, only the build system, shared infrastructure, and the deep module skeleton.

## 1. Phase mandate

Per ROADMAP Phase 1: a green, bootstrapped monorepo with the deep modules in place. The deliverables and requirements in this file are the contract; deviations require a decision note (AGENTS.md §0).

## 2. Architecture review (opening)

See ARCHITECTURE.md for the standing design. Phase 1 adds four execution decisions (proposed + accepted at phase opening):

- **D-P1.1 — Codegen deferral:** JSON Schema files in `libs/schema` are the source of truth; Kotlin models are hand-authored but *proven equivalent* by runtime schema-validation tests (networknt, pure JVM) + a CI freshness check. A full schema→Kotlin generator is deferred to the WebUI phase (Phase 6), where the TS side creates the demand. Enforcement replaces generation for now; ADR-0006's anti-drift intent is preserved by tests.
- **D-P1.2 — Uniform AGP modules:** all modules are `com.android.library`/`com.android.application` with AGP 9 built-in Kotlin (no `kotlin-android` plugin anywhere). KGP `jvm` plugin only for pure-JVM tooling if the bootstrap spike proves coexistence.
- **D-P1.3 — Empty `launcher/app` shell:** hosts release/debug variants, baseline-profile scaffolding, and the macrobenchmark target from day one. Zero feature code; UI lands in Phase 2.
- **D-P1.4 — Bootstrap spike gates tooling:** a throwaway module validates the whole toolchain matrix (built-in Kotlin + kotlinx-serialization plugin + detekt + ktlint + config cache + dependency locking) before the real foundation is built. Verdicts are recorded below.

## 3. Tooling verdicts (from bootstrap spike, T1)

| Tool | Verdict (2026-08-02 spike) | Fallback if failed |
|---|---|---|
| AGP 9.3.1 built-in Kotlin | ✅ compiles Kotlin, no kotlin-android plugin | — |
| kotlinx-serialization plugin | ✅ **works with built-in Kotlin** (2.4.10) | Moshi (not needed) |
| detekt | ✅ plugin applies | CLI-only |
| ktlint | ✅ plugin applies | CLI-only |
| binary-compatibility-validator | ⏳ untested in spike — try in T3; fallback custom API-dump | Custom javap task |
| Dokka / doc generation | ⏳ untested — MODULES.md via Gradle task regardless | Gradle doc task |
| KGP jvm coexistence | ⏳ not needed in Phase 1 (uniform AGP modules) | Codegen in build-logic |
| Configuration cache | ✅ engaged (spike builds cacheable) | Keep on |
| Dependency locking | ✅ Gradle-native | — |
| **aapt2 on ARM64 (T9)** | ✅ **SOLVED — qemu-user binfmt + amd64 loader/libs** (aapt2 2.20 runs natively via emulation) | CI-only (not needed) |

## 4. Module map (Phase 1 footprint)

```
build-logic/        convention plugins (library, application, quality, testing, architecture)
libs/core           primitives: clock, paths, Result helpers, no-op logging   (D1 responsibility)
libs/schema         JSON Schema sources (source of truth) + validation
libs/config         THE deep module: store zones (ADR-0021), read/parse/validate/
                    atomic-write/watch, migrations scaffold
libs/testing        shared test fixtures (JUnit5, coroutines-test, temp store)
launcher/app        EMPTY shell — variants, baseline-prof scaffold, benchmark target
benchmarks/macrobenchmark   scaffold (compiles, runs nothing until Phase 8)
tools/scripts       dev scripts mirroring CI
```

Dependency rules (ARCHITECTURE.md §3.1 enforced by `:architecture:validate`):
`libs/config` → `libs/core` + `libs/schema` only. `libs/schema` → nothing. Everything else in later phases.

## 5. Execution tasks

| # | Task | Owner | Exit criterion |
|---|---|---|---|
| T1 | Bootstrap spike (throwaway, /tmp) | build-owner | §3 verdicts recorded |
| T2 | Build foundation: settings, catalog, wrapper 9.6.1, gradle.properties (config cache + caching + reproducible flags) | build-owner | `./gradlew help` green, config cache active |
| T3 | build-logic convention plugins | build-owner | plugins apply cleanly |
| T4 | libs/core + libs/testing + Tier-1 tests | config-owner | tests green |
| T5 | libs/schema (JSON Schema + networknt validation) + tests | config-owner | models validate against schema |
| T6 | libs/config skeleton + Tier-1 tests | config-owner | deep-module API green |
| T7 | launcher/app shell + baseline-prof + benchmarks scaffold | build-owner | assembleDebug/Release green |
| T8 | Architecture validation + MODULES.md generation | qa-owner | `:architecture:validate` green |
| T9 | Local aapt2 (timeboxed; fallbacks) | build-owner | local assemble runs |
| T10 | CI armed (unit/lint/quality/architecture/schema/build + caching) | qa-owner | CI green on main |
| T11 | Docs: PHASE1/BUILD/DEVELOPMENT/MODULES + update README/ARCHITECTURE/ROADMAP/AGENTS | doc-owner | docs parity |
| T12 | Phase end: review, debt report, risks, build metrics, file tree, recommendation | all | explicit Phase 2 recommendation |

## 6. Quality gates (Phase 1)

- Tier-1 tests for every new module (docs/testing.md).
- CI fails on: lint, unit failures, schema freshness, architecture violations.
- Dependency locking committed (`gradle.lockfile` per module) + verification where practical.
- Reproducible-build flags: config cache on, build cache on (local + CI), stable packaging order.
- No circular dependencies (Gradle + architecture task).
- Public APIs intentional: `internal` by default; API dump committed (tooling verdict pending).

## 7. Known Phase 1 risks

- **R-A** tooling compatibility with built-in Kotlin (spike-gated)
- **R-B** ARM64 aapt2 → local build loop (T9, timeboxed)
- **R-C** config cache friction with AGP 9.3.1 (kept on from day one)
- **R-D** CI minutes (cached aggressively)

## 8. Phase-end artifacts

Architecture review · technical debt report · remaining risks · benchmark numbers (build times cold/warm, config-cache hits, where measurable) · complete file tree · phase summary · **explicit Phase 2 recommendation** (never automatic).
