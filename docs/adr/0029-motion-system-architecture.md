# ADR-0029: Motion system architecture (v2)

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0024, MOTION_RESEARCH.md

## Context
Research: springs damping 1.0/0.8, response 0.3-0.4s, no forced durations; press 80/160ms;
velocity inheritance; haptic lead 10-20ms; cross-fade reduced motion.

## Decision
MotionEngine v2: (1) spring tokens retuned to research values; (2) transition presets
(entrance/exit fade+scale, curve-driven); (3) PressScale modifier with 80ms down / 160ms
spring up + desaturation + shadow collapse; (4) velocity-aware retargeting via Compose springs
(retarget from current value — native); (5) haptic lead helper (pre-fire 15ms); (6) reduced
motion = cross-fade replacement.

## Consequences
One motion vocabulary; every interactive component uses PressScale + transition presets;
accessibility by scheme, not flag.
