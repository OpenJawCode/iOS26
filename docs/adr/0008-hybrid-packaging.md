# ADR-0008: Hybrid packaging — Magisk for system bits, APKs for dev

- Status: Accepted
- Date: 2026-08-01
- Decision: D9

## Context

Three things need system privileges: the launcher (priv-app status — survives battery management, overlay permission granted), system RRO overlays, and the config store directory (`/data/adb/...`). LSPosed modules merely need install + enable (Manager handles toggles). A flash-everything module gives the best end-user UX but makes development slow (re-flash per change); direct APK install gives a fast dev loop but leaves system bits unprovisioned.

## Decision

- **Magisk module (`magisk/ios26-stack`) installs only what needs system privileges:** launcher as systemless priv-app, system RROs, config-store bootstrap script, optional hook APK install + auto-enable.
- **Development:** `gradle install` direct APKs — fast loop.
- **Release:** the module is regenerated per release train, bundling everything for one-flash install.
- Phase 0 may use root-script provisioning as an interim, documented mechanism.

## Consequences

- Fast daily iteration on the actual device; reproducible one-flash install for release.
- Module regeneration is a release-time task with a Tier 3 script (flash from factory state).
- Priv-app status gives the launcher overlay reliability and battery immunity (needed by ADR-0005).
- Two install paths to keep in sync (dev vs release) — the release pipeline owns this.
