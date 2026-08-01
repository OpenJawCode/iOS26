# ADR-0020: CI/CD model — GitHub Actions

- Status: Accepted
- Date: 2026-08-01

## Context

The repo is GitHub-hosted (OpenJawCode org), the stack is Android + TS, and testing is tiered (ADR-0013). The CI/CD model must enforce the decision gate (lints, tests, schema freshness), produce the release train (ADR-0004), and never touch secrets.

## Decision

- **GitHub Actions** with three workflows: `ci.yml` (unit, lint, schema-freshness, AVD UI, WebUI — every PR), `release.yml` (tag `v*` → signed APKs + Magisk module zip + draft release), `device.yml` (Phase 9, self-hosted `edge20` runner for Tier 3).
- Signing secrets live in GitHub secrets only; never in the repo; pre-commit hook blocks local secret leakage.
- Workflows are inert until Phase 1 arms the Gradle wrapper; docs/ci.md is the contract.
- All CI commands are reproducible locally via `tools/scripts/` mirrors.

## Consequences

- Tier 1+2 regression protection on every PR; the decision gate is machine-enforced where possible.
- Release is reproducible and auditable (tag → artifacts → draft release with device Tier 3 as manual final gate).
- Device integration is deferred but designed for (runner-ready harness from Phase 0 onward).
- CI budget target < 15 min; AVD caching is a standing concern.
