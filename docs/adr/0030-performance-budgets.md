# ADR-0030: Performance budgets (binding)

- Status: Accepted · Date: 2026-08-02 · Related: PERFORMANCE.md

## Context
120Hz target; measured baseline (gallery 22ms median worst-case). Budgets must bind.

## Decision
Budgets from PERFORMANCE.md are binding for all future surfaces: 8.33ms/frame @120Hz; one blur
layer per surface; window blur ≤ maxWindow; RenderEffect ≤ maxRenderEffect; ≤2× overdraw;
gallery median ≤ 2× baseline after v2. Measured at every phase gate (gfxinfo + meminfo + startup).

## Consequences
Features that exceed budgets must degrade (intensity tokens) before merging; measurement is
part of the definition of done.
