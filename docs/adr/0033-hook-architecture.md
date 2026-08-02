# ADR-0033: Hook architecture

- Status: Accepted · Date: 2026-08-02

## Context
Survey (R5): Moto PrcPanel `NotificationPanelViewController#onInterceptTouchEvent` is the CC
target. Hooks must be isolated, reviewable, and rollback-safe.

## Decision
Per-surface hook classes behind the `hooks-api` seam: one adapter per surface, zero cross-surface
coupling; every hook applied inside a guarded block; any failure disables ALL hooks in the
process (all-or-nothing); feature flags gate each surface (default off).

## Consequences
One bad hook can't corrupt the system; each surface is independently shippable.
