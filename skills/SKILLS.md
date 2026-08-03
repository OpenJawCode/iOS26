# SKILLS.md — Knowledge Bootstrap (2026-08-03)

Three layers of skills serve this project:

```
Layer 1  skills/vendored/   community skills, read-only copies, provenance in SOURCE.md
Layer 2  skills/project/    project-specific skills (GPL-3.0, marked PROJECT-SPECIFIC)
Layer 3  global store       ~/.claude/skills + .agents/skills (pre-existing install)
```

## Layer 1 — Vendored community skills (this bootstrap)

| Vendor root | Skills inside | License |
|---|---|---|
| `platform-design-skills-ios` | Apple HIG for iPhone (1,083-line ruleset + official Apple_HIG.pdf) | MIT |
| `apple-skills-liquid-glass` | Liquid Glass (iOS 26+) design/API reference | MIT |
| `apple-skills-hig` | Greppable HIG corpus (materials, motion, color, …) | MIT |
| `chrisbanes-skills` | Compose recomposition/stability/state/animations/modifiers + Kotlin coroutines (7 skills) | Apache-2.0 |
| `skydoves-compose-performance` | 25-skill Compose performance library incl. audit orchestrator (Baseline Profiles, Macrobenchmark, R8, stability) | Apache-2.0 |
| `design-motion-principles` | Motion create/audit (Emil Kowalski et al. techniques) | MIT |
| `awesome-android-skills` | Accessibility, Compose UI, architecture, testing, gradle logic | Apache-2.0 |
| `sanyuan-skills` | Expert code review (SOLID/security/perf/error-handling) | MIT |

## Layer 2 — Project skills (this bootstrap, GPL-3.0, marked PROJECT-SPECIFIC)

| Skill | Capability |
|---|---|
| `project-systemui-hook-injection` | Modern libxposed SystemUI hooking on the Edge 20 (validated chain) |
| `project-magisk-lab` | Magisk/SELinux/perms lab operations (ARM64 quirks included) |
| `project-android-re-toolchain` | JADX/APKTool/smali survey workflows |
| `project-ui-forensics` | Visual-fidelity methodology without vision tools |
| `project-adr-authoring` | Repo ADR conventions |
| `project-baseline-profiles` | Baseline Profiles + Macrobenchmark for this repo |

## Layer 3 — Global store (pre-existing, not from this bootstrap)

`~/.claude/skills` + `.agents/skills` already carry: `frontend-design`, `design-taste-skill-pack`
(+ taste-design), `canvas-design`, `brand-guidelines`, `algorithmic-art`, `adaptive`, `styles`,
`edge-to-edge`, `navigation-3`, `android-cli`, `android-intent-security`, `agp-9-upgrade`,
`r8-analyzer`, `testing-setup`, `migrate-xml-views-to-jetpack-compose`, `perfetto-trace-analysis`,
`perfetto-sql`, `web-perf`, `code-review`, `diagnosing-bugs`, `grilling`, `wayfinder`,
`to-spec`/`to-tickets`/`triage`, `doc-coauthoring`, `docx`/`pdf`/`pptx`/`xlsx`, and the
engineering agents (code-reviewer, security-reviewer, typescript-reviewer, architect, planner,
tdd-guide, e2e-runner, refactor-cleaner, doc-updater). See SKILL_INVENTORY.md for the mapping.

## Activation guidance

- Community skills: auto-activate on their trigger topics (see SKILL_INVENTORY.md).
- Project skills: load when working in their domain — they encode failures that cost
  debugging sessions (hook API trap, permission traps, SELinux, device night-state).
- Skills are SOPs: "will I literally follow this in the next 5 minutes?" YES → load.
