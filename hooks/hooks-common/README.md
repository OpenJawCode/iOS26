# hooks/hooks-common

Shared hook infrastructure (ADR-0007): vendored **modern libxposed API**
(`io.github.libxposed.api`, Apache-2.0 — vendored per the fork's supported path, ADR-0032),
seam contracts (ADR-0019/0033), feature flags (ADR-0035). Compiled into each surface module
at build time (LSPosed classloader isolation).

Owners: hooks-owner. No Compose. No UI. Events only.
