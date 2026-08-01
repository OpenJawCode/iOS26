# ADR-0010: WebUI — on-device server + React SPA

- Status: Accepted
- Date: 2026-08-01
- Decision: D11

## Context

The WebUI needs a server. Hosting realities: on-device embedded server (companion), static SPA with file round-trip, or hosted cloud (accounts/infra/privacy — wrong for this project). Layout editing needs live apply, which favors a served UI over a file round-trip.

## Decision

- The companion app embeds a **lightweight Ktor HTTP server** bound to LAN, serving a **React SPA (Vite)**.
- **Auth:** one-time token, generated on-device and shown in companion; LAN-only binding.
- **Surfaces:** springboard layout editor (drag-drop), theme/token presets, wallpaper gallery, config import/export, diagnostics readout.
- Contracts: TS zod types generated from `libs/schema` (ADR-0006); the HTTP API is a thin read/write facade over `libs/config`.

## Consequences

- No cloud, no accounts, no sync infra — everything local (privacy posture, ADR-0018).
- Embedded server is real code to maintain — small, testable (Playwright, Tier 2).
- LAN-only reachability is a known constraint (hotspot for travel).
- Security surface is bounded: token auth + LAN bind + no persistent sessions.
