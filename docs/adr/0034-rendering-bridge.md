# ADR-0034: Rendering bridge

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0005/0019

## Context
SystemUI must not grow our UI stack. Phase 0 spike proved the file-event bus + overlay host
(68ms e2e).

## Decision
Hooks write typed events to the shared store; the launcher overlay host owns ALL rendering
(GlassEngine v2 surfaces). In-process SystemUI compositing only if 3.2 measurements demand it
(separate ADR then).

## Consequences
Testable UI in a normal process; SystemUI stays thin; latency budget 68ms baseline.
