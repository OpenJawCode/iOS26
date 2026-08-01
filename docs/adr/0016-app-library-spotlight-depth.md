# ADR-0016: App Library auto-categories + rich Spotlight

- Status: Accepted
- Date: 2026-08-01
- Decision: D17

## Context

D1 commits App Library and Spotlight to v1; depth was open. iOS's App Library auto-categorizes apps; Android has no category engine. Spotlight on iOS searches broadly; Android has no universal index API.

## Decision

- **App Library:** auto-categories backed by a **bundled category-mapping data layer** — localized labels, community-extensible mapping (package → category), per-app user overrides. No on-device ML.
- **Spotlight:** apps + contacts + app actions (deep links). **Fully local, offline.** No web suggestions, no device-content search (files/settings/SAF providers) in v1.

## Consequences

- The category data layer is a real, curatable artifact (i18n + coverage strategy, Phase 2); users can override anything, so wrong defaults are survivable.
- Spotlight is bounded and offline-first; content search is explicitly cut (provider-by-provider integration is the most fragile, least rewarding work available).
- Both are launcher-internal contexts (no hook surface) — testable at Tier 1/2.
