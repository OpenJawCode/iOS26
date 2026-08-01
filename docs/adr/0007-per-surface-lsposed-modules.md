# ADR-0007: Per-surface LSPosed modules

- Status: Accepted
- Date: 2026-08-01
- Decision: D8

## Context

The LSPosed layer will absorb future surfaces (status bar, lock screen, live activities). LSPosed's module manager toggles per module. Consolidated modules (AOSPMods-style) share code trivially but are one big toggle with coarse failure isolation. LSPosed modules cannot cleanly depend on each other at runtime (classloader isolation), so shared hook code is embedded at build time.

## Decision

- **One Gradle module per hook surface:** `hooks/control-center` now; `hooks/statusbar`, `hooks/lockscreen` etc. later.
- **`hooks/hooks-common`:** shared hook infrastructure (reflection/dex access helpers, event emission, config access) compiled into each module at build time.
- **`hooks/hooks-api`:** the seam contract — pure interfaces for hook↔host communication (ADR-0019), depended on by launcher and hooks.
- Single release train (ADR-0004) prevents version skew.

## Consequences

- Per-feature toggles in LSPosed Manager; one crashing hook cannot disable others.
- Independent agent ownership per module; parallel work without merge storms.
- N installs to manage — solved by Magisk auto-provisioning (ADR-0008).
- Build-time code duplication of `hooks-common` is intentional and standard for the ecosystem.
