# ADR-0031: Component rendering contracts

- Status: Accepted · Date: 2026-08-02

## Context
Components must not reimplement visual effects; all material/motion from engines.

## Decision
Contract: (1) surfaces → GlassPanel pipeline only; (2) interaction → PressScale + MotionEngine
presets only; (3) values → semantic tokens only; (4) haptics → HapticEngine only; (5) no
component-local colors, animations, or effects. Enforced by review + architecture gate note.

## Consequences
Auto-theming; consistent feel; measurable perf behavior; new components are cheap to add.
