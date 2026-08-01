# ADR-0015: Full widget hosting in v1

- Status: Accepted
- Date: 2026-08-01
- Decision: D16

## Context

iOS 26 has widgets; Android users' home screens are full of them. A launcher that discards existing widgets is a non-starter as a daily driver. But Android widgets are RemoteViews-based, platform-sized, and often aggressively non-iOS. Options: full hosting, no widgets, or contained widgets area.

## Decision

**Full AppWidgetHost subsystem in v1:**

- Widgets hosted in iOS-style springboard grid slots.
- Platform tinting applied where Android 13 allows (simple widgets); glass framing where it doesn't.
- The add-widget flow rebuilt in iOS idiom (picker presented in experience language).
- Phase 0 reality-checks tinting behavior on the device (CONTEXT.md §3 open question 4).

## Consequences

- Largest single launcher subsystem after the springboard — real Phase 2 scope, budgeted.
- Aesthetic compromise is explicit and contained: some third-party widgets will look non-iOS; framing is the mitigation.
- Widget binding/lifecycle (AppWidgetHost) must be robust across launcher restarts — it's the most common source of launcher "losing widgets" bugs.
