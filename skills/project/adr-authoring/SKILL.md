---
name: project-adr-authoring
description: "PROJECT-SPECIFIC (iOS26). Authoring Architecture Decision Records per this repo's conventions (docs/adr/README.md template, sequential numbering, statuses Proposed→Accepted→Superseded, never renumber, never delete). Use whenever a decision changes an architectural contract. Reference: adr/madr and npryce/adr-tools conventions (link-only, not vendored)."
license: GPL-3.0
project: iOS26
source: project-specific (docs/adr/README.md, CONTEXT.md §2)
---

# ADR Authoring (repo conventions)

1. Number sequentially from `docs/adr/`; never renumber or delete superseded ADRs — they
   point to their successors.
2. Template: Status · Date · Context (problem + constraints) · Decision (one paragraph,
   imperative) · Consequences (trade-offs). Related ADRs linked inline.
3. A decision that alters a term/API updates CONTEXT.md and/or an ADR in the SAME commit.
4. Keep the index (`docs/adr/README.md`) in sync — the architecture gate reads edges from
   ARCHITECTURE.md, not ADRs; keep both truthful.
5. Reference external ADR practice: MADR (adr/madr) and adr-tools (npryce/adr-tools) — we
   follow a lighter single-file template; cite them for structure only.
