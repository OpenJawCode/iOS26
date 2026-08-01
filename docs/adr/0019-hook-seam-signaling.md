# ADR-0019: Hook seam & file-event signaling

- Status: Accepted
- Date: 2026-08-01

## Context

The LSPosed layer and app layer live in different processes with different privileges; hooks must never import launcher/companion code (ADR-0007). The Control Center flow (ADR-0005) needs the hook to signal the host: SystemUI intercepts the swipe → host raises the panel. Options: binder service (lifecycle coupling to a host process — rejected by ADR-0006), socket (stateful, fragile), file-event bus (consistent with the config store, zero coupling, inotify-fast).

## Decision

- **`hooks/hooks-api`** is the seam contract: typed hook capabilities + typed events, implemented by surface-specific adapters (Moto SystemUI adapter for `hooks/control-center`).
- **Signaling rides the config store's event-file mechanism** (ADR-0006): the hook writes a typed event (atomic temp+rename) under `/data/adb/ios26/events/`; hosts observe via FileObserver. No binder, no sockets.
- Hooks read config and write events only. Hosts subscribe to events and own all rendering.
- Adapter isolation: Moto-specific class/method references live in one adapter class per surface, per the Phase 0 hook-point survey.

## Consequences

- The SystemUI process depends on nothing but the filesystem — reliable by construction (ADR-0006).
- Event latency is inotify-scale (ms); validated as a Phase 0 acceptance criterion for the CC gesture feel.
- The seam is the contract both sides test against (host side on AVD, hook side Tier 3).
- File-event storms are bounded by design: events are ephemeral (cleanup on read), and hosts debounce.
