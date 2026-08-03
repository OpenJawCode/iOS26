# SKILL_INVENTORY.md — Full inventory with provenance

> Vendored 2026-08-03 (knowledge bootstrap, Phase 3.2). "Auto" = should the skill activate
> automatically when its topic appears.

## Vendored community skills

| Skill | Source repo | Commit | License | Auto | Activates for |
|---|---|---|---|---|---|
| ios-design-guidelines | ehmo/platform-design-skills | dc2be82 | MIT | yes | any UI build/review (HIG rules) |
| ios-liquid-glass | Prisma-Labs-Dev/apple-skills | a76633b | MIT | yes (glass surfaces) | glass material/effects work |
| hig (fact lookup) | Prisma-Labs-Dev/apple-skills | a76633b | MIT | on demand | HIG facts, a11y minimums |
| compose-recomposition-performance | chrisbanes/skills | d8d925b | Apache-2.0 | yes | recomposition/compiler reports |
| compose-stability-diagnostics | chrisbanes/skills | d8d925b | Apache-2.0 | yes | stability, skippability |
| compose-state-hoisting | chrisbanes/skills | d8d925b | Apache-2.0 | yes | state architecture |
| compose-animations | chrisbanes/skills | d8d925b | Apache-2.0 | yes | animations |
| compose-modifier-and-layout-style | chrisbanes/skills | d8d925b | Apache-2.0 | yes | modifiers/layout |
| compose-state-authoring | chrisbanes/skills | d8d925b | Apache-2.0 | yes | state authoring |
| kotlin-coroutines-structured-concurrency | chrisbanes/skills | d8d925b | Apache-2.0 | yes | coroutine code |
| auditing-compose-performance (+24 focused) | skydoves/compose-performance-skills | 1b32f81 | Apache-2.0 | yes (perf) | perf sprints, audits, BP/Macrobenchmark |
| design-motion-principles | kylezantos/design-motion-principles | 4a9ca87 | MIT | yes (motion) | motion create/audit |
| android-accessibility | new-silvermoon/awesome-android-agent-skills | 82900ea | Apache-2.0 | yes | a11y work |
| compose-ui | new-silvermoon/awesome-android-agent-skills | 82900ea | Apache-2.0 | yes | Compose UI |
| android-architecture | new-silvermoon/awesome-android-agent-skills | 82900ea | Apache-2.0 | yes | architecture |
| android-testing | new-silvermoon/awesome-android-agent-skills | 82900ea | Apache-2.0 | yes | testing |
| android-gradle-logic | new-silvermoon/awesome-android-agent-skills | 82900ea | Apache-2.0 | yes | gradle |
| code-review (expert) | sanyuan0704/sanyuan-skills | 08b6572 | MIT | yes | post-write review |

## Project skills (GPL-3.0, marked PROJECT-SPECIFIC)

| Skill | Auto | Activates for |
|---|---|---|
| project-systemui-hook-injection | yes (hooks) | any LSPosed module work |
| project-magisk-lab | yes (root/lab) | provisioning, SELinux, perms, lab ops |
| project-android-re-toolchain | on demand | RE surveys |
| project-ui-forensics | yes (visual review) | fidelity checks, screenshots, baselines |
| project-adr-authoring | on demand | ADRs |
| project-baseline-profiles | yes (perf gates) | BP + Macrobenchmark runs |

## Pre-existing global store (unchanged; key ones)

frontend-design, taste-design/design-taste-skill-pack, canvas-design, brand-guidelines,
adaptive, styles, edge-to-edge, navigation-3, android-cli, android-intent-security,
agp-9-upgrade, r8-analyzer, testing-setup, migrate-xml-views-to-jetpack-compose,
perfetto-trace-analysis, perfetto-sql, web-perf, code-review, diagnosing-bugs, grilling,
wayfinder, to-spec, to-tickets, triage, doc-coauthoring, docx/pdf/pptx/xlsx, context7,
find-docs, know-me, self-healing, skill-creator, webapp-testing, remotion, lead-*,
printing-press, cloudflare, supabase, huggingface-*, langchain-*, deep-agents-*, etc.
(Full list: `~/.claude/skills`, `.agents/skills`.)
