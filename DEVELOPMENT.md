# DEVELOPMENT.md — Development workflow

## The decision gate (read first)

AGENTS.md §0: architectural uncertainty → stop and ask (one question at a time).
Feature-level details: decide, document. Every architectural change lands as an ADR
before code (`docs/adr/README.md`).

## Repo map (short)

```
build-logic/   convention plugins (ios26.library/application/quality/testing/architecture)
libs/core      Clock, Log — depends on nothing
libs/schema    JSON Schema source of truth + networknt validation
libs/config    THE deep module (store facade, atomic writes, poll watcher)
libs/testing   Tier-1 fixtures (TempStore)
launcher/app   empty shell (variants, benchmark target)
benchmarks/    macrobenchmark scaffold (Phase 8)
MODULES.md     generated module index (do not edit)
```

## Adding a module

1. Pick the convention: `plugins { id("ios26.library") }` (or `ios26.application`).
2. `settings.gradle.kts`: `include(":libs:foo")`.
3. README.md with purpose + ownership (AGENTS.md §2).
4. Add its allowed dependency edges to `ArchitectureValidateTask` (ARCHITECTURE.md §3.1).
5. Regenerate: `./gradlew generateModulesDoc`.
6. Tier-1 tests alongside the first feature code.

## Testing tiers

| Tier | Where | Command |
|---|---|---|
| 1 — unit | this machine / CI | `./gradlew testDebugUnitTest` |
| 2 — AVD + browser | CI (Phase 2+) | `connectedDebugAndroidTest`, Roborazzi, Playwright |
| 3 — device | wired Edge 20 (Tailscale lab) | `device-tests/` harness |

Phase 1 covers Tier 1 only. New Tier-2/3 surfaces arrive with their phases; every
module must keep its Tier-1 suite green from day one.

## Daily loop

```bash
./gradlew testDebugUnitTest ktlintCheck detekt   # fast (< 1 min, cached)
./gradlew :libs:config:testDebugUnitTest --rerun # single module with rerun
```

## Toolchain conventions (enforced)

- AGP 9 **built-in Kotlin** — never apply `org.jetbrains.kotlin.android` (PHASE1.md verdicts).
- All versions in `gradle/libs.versions.toml`; never inline.
- `internal` by default; public API is intentional (architecture gate + API review).
- Dependencies through the version catalog; lockfiles committed.
- Compose/AndroidX: not yet in the graph (lands Phase 2); the build stays dependency-free until then.

## Device lab (Tier 3)

`docs/phase0/device-setup.md`: Tailscale mesh, `persist.adb.tcp.port=5555`, Always-on VPN.
Baseline: `device-tests/baseline/device-baseline.sh` (4/4 PASS, firmware pinned
`T1RGS33.135-109-9-29`).

## Mistakes & bugs

Append to `docs/logs/mistakes.md` / `docs/logs/bugs.md` (gitignored) — never rewrite.
Handoffs: `docs/logs/handoffs/` when a session must stop mid-task (AGENTS.md §7).
