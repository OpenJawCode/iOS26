# ADR-0035: State synchronization

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0006/0021

## Context
Multiple processes (SystemUI hooks, launcher host) share state.

## Decision
The schema-validated config store (shared zone, ADR-0021) is the SINGLE state channel.
No binder, no sockets (ADR-0019). Flags, toggles, events all file-based; atomic writes;
poll watcher (spike-proven) until FileObserver policy lands (Phase 4).

## Consequences
Zero process coupling; hand-debuggable; crash-safe by construction.
