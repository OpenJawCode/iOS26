# iOS26 — Project Reference

Facts about the Android/iOS26 reverse-engineering project. Read this instead of guessing.
Last reviewed: 2026-08-07.

## Identity

- Path: `Android/iOS26/` — Motorola Edge 20 (Moto) SystemUI reverse-engineering + hooking lab
- Hardware: ARM64 lab + rooted Edge 20

## Skill architecture (project-scoped)

- Project skills live in `iOS26/.opencode/skills/` (13 links): `project-` prefixed frontmatter names (e.g. `project-adr-authoring`, `project-ui-forensics`, `project-magisk-lab`, `project-systemui-hook-injection`, `project-android-re-toolchain`, `project-baseline-profiles`) + vendored packs (v-awesome-android, v-chrisbanes-compose, v-skydoves-compose-perf, v-sanyuan-code-review, v-apple-hig-facts, v-apple-liquid-glass, v-hig-ios-design) linking into `iOS26/skills/{project,vendored}/`
- Source of truth for pack provenance: `iOS26/skills/SKILL_INVENTORY.md`
- ADR conventions: `docs/adr/` (sequential numbering, statuses Proposed→Accepted→Superseded, never renumber)

## Key specs & constraints

- GLASS_ENGINE_V2 / MOTION specs drive UI fidelity work (token diffing in `project-ui-forensics`)
- Perf budget: 8.33ms frame budget (ADR-0030), measured via Baseline Profiles + Macrobenchmark
- Hooking: LSPosed (JingMatrix fork), modern libxposed API only (legacy Xposed is broken)
- Magisk lab: magiskpolicy --live grants; SELinux on ARM64 lab; boot-loop protection
