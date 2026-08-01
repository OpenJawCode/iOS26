# ADR-0012: Original assets + on-device personal import

- Status: Accepted
- Date: 2026-08-01
- Decision: D13

## Context

An open-source repo cannot ship Apple IP (icons, wallpapers, sounds, SF Pro font) — it would be legally poisoned. But the experience is its look. Options: original assets, import-only, licensed third-party sets.

## Decision

- **Ship original, permissively-licensed defaults:** original squircle icon pack (core system icons + style-guide-driven app icons, grown by community), original wallpapers, original UI sounds, and an open font (Inter-class) standing in for SF Pro.
- **On-device personal import:** companion feature to import the user's own assets (e.g., ripped from their own devices). These stay on-device; **never in the repo**.
- No Apple-derived files anywhere in the tree, including generated or test fixtures.

## Consequences

- Legal safety for a public GPL-3.0 repo; identity comes from our own artwork direction.
- Asset production is real scope: an assets pipeline (`assets/`) with sources + generation, not just binaries.
- User customization is a first-class feature (personal import), not an afterthought.
- Icon-pack runtime (`libs/icons`) must support both bundled and user-supplied packs.
