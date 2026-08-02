# ADR-0032: SystemUI injection strategy

- Status: Accepted · Date: 2026-08-02 · Related: ADR-0007/0019, R7

## Context
R7 proved the fork's legacy Xposed API is broken for new modules (AbstractMethodError). The
modern libxposed API (io.github.libxposed.api, targetApiVersion>=101) is the supported path.

## Decision
All hooks use the **modern libxposed API** only. SystemUI hooks emit EVENTS; SystemUI never
renders our UI in 3.1-3.2 (ADR-0034). Injection targets one class per surface, flag-gated.

## Consequences
Legacy compatibility debt avoided; the fork's native path is used; rollback = flag off.
