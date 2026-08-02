# ADR-0017: API level policy — minSdk = targetSdk = 33

- Status: Accepted
- Date: 2026-08-01

## Context

The reference platform is stock Android 13 (ADR-0002); the device cannot run newer platforms. targetSdk above the device's API adds nothing and can add restrictions (e.g., edge-to-edge enforcement, broadcast restrictions in 34+).

## Decision

**minSdk = targetSdk = compileSdk = 33** for all Android modules. The API level policy is frozen to the reference platform; it changes only with a new ADR.

## Consequences

- No post-13 platform APIs anywhere; the toolchain (AGP/Kotlin) still moves, pinned via the version catalog.
- Edge-to-edge behavior is ours to design (no 35 enforcement); insets handled via the `edge-to-edge` skill conventions.
- Simple mental model for all contributors and agents: the platform is 33, forever.
- Future multi-device support (if it ever arrives) revisits this ADR.

## Amendment (2026-08-02, Phase 2)

`compileSdk` rises to **36** (build-time only, required by androidx Compose BOM 2026.06.01; AGP 9.3.1 supports up to 36.1). **`minSdk` and `targetSdk` remain 33** — the runtime policy is unchanged: the reference platform is Android 13 and no post-13 runtime API is used. compileSdk is a build tool, not a runtime contract; the "frozen platform" intent (firmware `T1RGS33.135-109-9-29`, no API-34+ behavior) is preserved.
