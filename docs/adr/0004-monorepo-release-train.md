# ADR-0004: Monorepo + single release train

- Status: Accepted
- Date: 2026-08-01
- Decision: D5

## Context

Seven interlocking components (launcher, hooks, magisk, overlays, companion, webui, libs) share contracts and a config store. Cross-component change is the most expensive operation in the project. Multi-repo means version matrices and cross-repo PRs for every shared change — miserable for a solo maintainer with AI agents. The stack ships as one installable experience; independent cadence has no consumer.

## Decision

**Single git repository** at the project root. Android stack = one Gradle multi-module build (module coordinates mirror directory paths). `webui/` lives in the same repo with its own package.json toolchain and its own CI job — never mixed into Gradle.

Versioning: per-component semver aggregated into one **stack version**; release = one tag = one installable experience.

## Consequences

- Atomic cross-component changes (lib + consumers + tests in one commit); one CI pipeline verifies the whole graph.
- Repo grows large — fine at 100k+ LOC; agents navigate monorepos better than cross-repo.
- WebUI toolchain isolation must be respected (separate build tree, separate CI job) to avoid toolchain bleed.
- Release discipline: tags only from main; signing secrets in CI only (docs/ci.md).
