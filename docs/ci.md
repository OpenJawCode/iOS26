# docs/ci.md — CI/CD Strategy

> GitHub Actions, per ADR-0020. Workflows live in `.github/workflows/`. They arm in Phase 1 when the Gradle wrapper lands; until then they are inert scaffolding.

## Workflows

### 1. `ci.yml` — pull requests and pushes to `main`

| Job | Runs | Enforces |
|---|---|---|
| `unit` | `./gradlew testDebugUnitTest` | Tier 1 |
| `lint` | `./gradlew ktlintCheck detekt` + `eslint tsc` (webui) | Conventions (docs/conventions.md) |
| `schema-check` | codegen freshness (diff check) | Schema is source of truth (§3.1) |
| `avd-ui` | Roborazzi diffs + Compose UI tests on API 33 AVD (cached system image) | Tier 2 screenshots/flows |
| `webui` | vitest + Playwright against the Ktor server | Tier 2 WebUI |

Fail-fast off; all jobs must pass for merge. AVD job uses `reactivecircus/android-emulator-runner` with the AOSP API 33 image; hardware acceleration on `ubuntu-latest` runners.

### 2. `release.yml` — tag push (`v*`)

1. Verify decision gate: changelog + version catalog bump present in the tag (conventional commits parsed).
2. Build: all APKs (launcher, companion, hooks) + assemble `ios26-stack` Magisk module zip (`magisk/ios26-stack/release/`).
3. Attach artifacts to the GitHub Release draft; draft notes from the changelog.
4. Tier 3 device verification is a **manual gate** — release is only finalized when the device run record (docs/testing.md Tier 3) is linked.

### 3. `device.yml` — optional self-hosted runner (Phase 9)

Trigger: `workflow_dispatch` + label `edge20` runner. Runs `device-tests/` harness scripts; uploads PASS/FAIL manifest + Perfetto traces as artifacts.

## Secrets & signing

- Release signing keys: never in the repo. Stored as GitHub secrets (`ANDROID_SIGNING_KEYSTORE_B64`, `ANDROID_SIGNING_KEYSTORE_PASSWORD`, `ANDROID_SIGNING_KEY_ALIAS`, `ANDROID_SIGNING_KEY_PASSWORD`); CI decrypts at build time in `release.yml` only.
- No other secrets expected (no cloud, no analytics — ADR-0018). The pre-commit hook blocks `.env` and secret patterns locally.

## Versioning & releases (ADR-0004)

- Per-component semver in the version catalog; the **stack version** (release train) is the tag (`v1.0.0`).
- Changelog auto-drafted from conventional commits (`github-changelog-generator` style), curated by doc-owner before finalize.

## Gates summary

| Gate | Where |
|---|---|
| Lint + unit + schema freshness | `ci.yml` every PR |
| Screenshots + flows + webui E2E | `ci.yml` every PR |
| Device Tier 3 | manual + `device.yml` (Phase 9) |
| Release artifacts + signing | `release.yml` on tag |
| Perf budgets | Phase 8: macrobenchmark job on AVD + device runner |

## Maintenance

- Workflow changes go through review like code; every job must be reproducible locally (`tools/scripts/` mirrors the CI commands).
- CI time budget: AVD job cached aggressively; aim for < 15 min full CI.
