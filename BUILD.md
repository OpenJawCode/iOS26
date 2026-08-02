# BUILD.md — Building the project

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| JDK | 21 (Temurin) | AGP 9 requires ≥ 17; 21 is the pin (research-log R1) |
| Android SDK | platform 33, build-tools 36.0.0 | `sdk.dir` via `local.properties` (never committed) |
| Gradle | 9.6.1 (wrapper) | `./gradlew` — never the system gradle |

## ARM64 host note (this lab)

Google ships **no arm64 Linux aapt2**. The lab runs x86_64 binaries transparently via
**qemu-user binfmt** (kernel `binfmt_misc` + `libc6:amd64`/`libstdc++6:amd64` + the
`/lib64/ld-linux-x86-64.so.2` loader; verified 2026-08-02, PHASE1.md T9). This makes
AGP's aapt2 work natively. If the box ever loses binfmt (e.g., container rebuild):

```
sudo dpkg --add-architecture amd64
# add archive.ubuntu.com amd64 sources (ubuntu-ports carries no amd64)
sudo apt-get install -y libc6:amd64 libstdc++6:amd64
```

## First build

```bash
cp .env.example .env 2>/dev/null || true   # not needed yet — no secrets in the build
printf 'sdk.dir=/path/to/android-sdk\n' > local.properties
./gradlew build
```

## Standard tasks

| Task | Purpose |
|---|---|
| `./gradlew build` | Everything below + lint (CI-only in the dev loop; slow) |
| `./gradlew testDebugUnitTest` | Tier-1 unit tests |
| `./gradlew ktlintCheck detekt` | Static analysis (module-scoped) |
| `./gradlew assembleDebug assembleRelease` | All variants, all app modules |
| `./gradlew architectureValidate` | ARCHITECTURE.md §3.1 dependency rules + README anchors |
| `./gradlew generateModulesDoc` | Regenerate MODULES.md (CI diffs it) |
| `./gradlew dependencies --write-locks` | Refresh dependency lockfiles |

## Dependency locking

Lockfiles (`*.lockfile`) are committed per module. Bump versions in
`gradle/libs.versions.toml` only, then re-lock:

```bash
./gradlew dependencies --write-locks
```

## Configuration cache & build cache

Both are on (`gradle.properties`). Local build cache shares nothing with CI; CI caches
via `gradle/actions/setup-gradle`. If the config cache ever misbehaves:
`./gradlew --stop && rm -rf .gradle/configuration-cache`.

## Verification gate (before every commit)

```bash
./gradlew testDebugUnitTest ktlintCheck detekt architectureValidate assembleDebug
```

CI runs the same gates plus `assembleRelease` and the MODULES.md freshness diff.
