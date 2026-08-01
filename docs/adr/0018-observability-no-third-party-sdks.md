# ADR-0018: Observability — no third-party SDKs

- Status: Accepted
- Date: 2026-08-01

## Context

Production quality usually means crash reporting + analytics SDKs (Firebase etc.). This project: no Play services dependency by choice, GPL-3.0, privacy-first, personal-device-first. Third-party SDKs add permission surface, privacy surface, and license surface for value that a rooted single-user device doesn't need.

## Decision

- **No third-party crash/analytics/telemetry SDKs, ever.**
- Observability is: logcat + a custom crash handler writing structured crash records to the config store (`/data/adb/ios26/logs/`), plus the companion diagnostics surface (crash list, hook health, config validation) and Tier 3 verification.
- WebUI and companion expose the same records for on-device inspection; nothing leaves the device.

## Consequences

- Privacy posture is trivial to defend: everything local (README, ADR-0010).
- We own crash capture quality: the custom handler and log rotation are maintained code (small, Tier-1-tested).
- Debugging rooted-device issues stays on-device anyway — the SDK would have added little.
- Dependency policy in conventions §9 gains teeth: no analytics SDKs, no Play-services requirement.
