# PHASE1.md — Architecture & Toolchain (Phase 1)

> Status: **complete — awaiting Phase 2 approval** (opened + closed 2026-08-02). Phase 0 closed. Phase 1 builds the production foundation: no features, only the build system, shared infrastructure, and the deep module skeleton.

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
| T1 | Bootstrap spike (throwaway, /tmp) | build-owner | ✅ verdicts recorded (2026-08-02) |
| T2 | Build foundation: settings, catalog, wrapper 9.6.1, gradle.properties | build-owner | ✅ green; config cache on |
| T3 | build-logic convention plugins | build-owner | ✅ apply cleanly |
| T4 | libs/core + libs/testing + Tier-1 tests | config-owner | ✅ 2+2 tests green |
| T5 | libs/schema (JSON Schema + networknt 3.x validation) + tests | config-owner | ✅ 4 tests green |
| T6 | libs/config skeleton + Tier-1 tests | config-owner | ✅ 10 tests green |
| T7 | launcher/app shell + baseline-prof + benchmarks scaffold | build-owner | ✅ assembleDebug/Release green |
| T8 | Architecture validation + MODULES.md generation | qa-owner | ✅ config-cache safe |
| T9 | Local aapt2 (timeboxed; fallbacks) | build-owner | ✅ SOLVED — qemu binfmt |
| T10 | CI armed (unit/quality/architecture/build + caching) | qa-owner | ✅ pushed; verified locally |
| T11 | Docs: BUILD/DEVELOPMENT/MODULES + README/ROADMAP/AGENTS updates | doc-owner | ✅ parity check at phase end |
| T12 | Phase end: review, debt report, risks, metrics, tree, recommendation | all | ✅ this document |

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

---

## 9. Phase-end report (2026-08-02)

### Architecture review (phase exit)

All 21 ADRs + Phase 1 decisions verified consistent with repository state. The dependency graph
matches ARCHITECTURE.md §3.1 (enforced by `architectureValidate`, green). AGP 9 built-in Kotlin
verified against the `agp-9-upgrade` skill's guidance. No architectural reversal was required
during the phase; two execution decisions (D-P1.1 codegen deferral, D-P1.2 uniform AGP modules)
were proposed and accepted at phase opening and hold.

### Technical debt report

| ID | Debt | Severity | Owner |
|---|---|---|---|
| TD-1 | `lint` is CI-only (local `build` includes it; dev gate excludes it) — documented in BUILD.md; revisit when Compose lands | low | build-owner |
| TD-2 | networknt 3.x API (Jackson 3) pinned at 3.0.6 — upgrade path untested | low | config-owner |
| TD-3 | Spike scratch repo (`OpenJawCode/cc-spike`) holds the working CC panel proto — must be migrated into Phase 2 rather than rewritten | medium | control-center-owner |
| TD-4 | PollWatcher (polling) is the only watch mechanism; FileObserver behind Phase-4 policy is TODO | medium | config-owner |
| TD-5 | dependency verification (SHA-256) not yet enabled — locking only | low | build-owner |
| TD-6 | `benchmarks/macrobenchmark` + `launcher/baseline-prof` are empty scaffolds (androidx wired in Phase 8) | low | build-owner |

### Remaining risks

| Risk | Phase | Mitigation |
|---|---|---|
| R-A: fork module API for hooks (legacy broken) | 3 | modern libxposed path known; spike source cloned + R7 |
| R-B: ARM64 lab depends on qemu binfmt | ongoing | documented in BUILD.md; aapt2 source-build as fallback |
| R-C: config cache edge cases (task closures) | ongoing | tasks use real task classes + string inputs (learned twice this phase) |
| R-D: disk capacity on the lab box (45G, repeatedly hit 100%) | ongoing | cache hygiene documented; monitor before Phase 2 |

### Benchmark numbers (measurable at phase end, this ARM64 lab)

| Metric | Value |
|---|---|
| Full verification gate (tests+ktlint+detekt+assemble), cached | **6 s** |
| First `assembleDebug` (app shells), warm | **48 s** |
| `:libs:config` Tier-1 suite | 10 tests / **10 s** |
| Config cache | active, entry reused |
| Tier-1 totals | 16 tests green (core 2, schema 4, config 10) |
| Kotlin LOC (build-logic + libs) | 787 |

### Phase summary

The production foundation is built and verified: convention plugins, version catalog, deep-module
skeleton (`libs/config` with ADR-0021 zones, atomic writes, poll watcher), schema-first validation,
architecture gate, MODULES.md generation, CI (unit/quality/architecture/build), dependency locking,
config cache + build cache, and the ARM64 aapt2 problem solved. 16 Tier-1 tests green; the full gate
runs in 6 s cached. All Phase 1 exit criteria met.

### Recommendation

**READY FOR PHASE 2.** The foundation is green, the toolchain matrix is verified (including the two
highest-risk unknowns: built-in Kotlin + serialization, and ARM64 aapt2), and the architecture gate
will enforce the module rules as the launcher grows. Phase 2 starts with the design system
(`libs/design` tokens per ADR-0011) and the Springboard module, carrying over the spike's CC panel
proto (TD-3). **Phase 2 must not begin without explicit approval.**
