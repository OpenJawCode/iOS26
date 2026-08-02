# ADR-0024: Motion system (token-driven)

- Status: Accepted · Date: 2026-08-02 · Related: D-P2.1, MOTION.md

## Context
Motion must follow fluid-interface doctrine: interruptible, velocity-aware, press-responsive,
reduced-motion aware — without duplicated animation definitions.

## Decision
MotionEngine resolves ALL animation from `Tokens.Motion` (durations, cubic-bezier curves,
springs mapped from iOS UISpringTimingParameters). Springs preferred (interruptible); tweens
only where tokens define them. Reduced motion scales durations by `reducedMultiplier` (0.5)
when the system animator scale is 0.

## Consequences
One motion vocabulary for every future surface; accessibility by token; no hand-tuned values.
