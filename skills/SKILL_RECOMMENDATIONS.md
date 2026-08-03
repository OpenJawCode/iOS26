# SKILL_RECOMMENDATIONS.md — Evaluation + per-phase stack

> Every candidate evaluated on: why chosen, strengths, weaknesses, auto-activation, phases.
> Rejected candidates are listed with reasons. Rules honored: license must exist; no invented
> skills; weakest duplicates dropped.

## Chosen (with rationale)

### Apple design / HIG / Liquid Glass
1. **ehmo/platform-design-skills → `ios`** (MIT, dc2be82) — *chosen over* justinwetch/
   HIGAgentSkills (140★, no license) and axiaoge2/Apple-Hig-Designer (137★, MIT, thinner).
   Strengths: 1,083-line rule set derived from HIG + the official Apple_HIG.pdf bundled;
   covers touch targets, Dynamic Type, Dark Mode, a11y. Weaknesses: SwiftUI examples (we map
   to Compose); generic iOS — not CC-specific. Auto: yes. Phases: 3.2-3.5 UI work, 4.
2. **Prisma-Labs-Dev/apple-skills → `ios-liquid-glass` + `hig`** (MIT, a76633b). Strengths:
   the only maintained Liquid Glass reference skill (26+ API design principles, glass
   hierarchy, accessibility adaptation) + a full greppable HIG corpus as markdown (materials,
   motion, color). Weaknesses: SwiftUI-oriented; the corpus is a fact lookup, not direction.
   Auto: yes (glass surfaces) / on demand (facts). Phases: all glass surfaces; fact-checking.
3. **kylezantos/design-motion-principles** (MIT, 4a9ca87) — *chosen over* LottieFiles/
   motion-design-skill (web/DOM-oriented) and Jakubantalik/transitions.dev (web transitions).
   Strengths: create+audit modes, Emil Kowalski / Krehel / Tompkins techniques, audit emits a
   report. Weaknesses: web-stack examples (Framer Motion/CSS) — principles transfer, code
   doesn't. Auto: yes (motion). Phases: MotionEngine tuning (3.2), springboard motion (3.5).

### Android / Compose / performance
4. **chrisbanes/skills (7 selected)** (Apache-2.0, d8d925b) — authored by Chris Banes (Google
   Android). Strengths: authoritative Compose diagnostics (stability reports, recomposition,
   state hoisting, animations) + coroutines. Weaknesses: no SystemUI-specific content. Auto:
   yes. Phases: 3.2 validation, 3.3-3.5 all Compose work, 4.
5. **skydoves/compose-performance-skills (audit + 24 focused)** (Apache-2.0, 1b32f81) —
   *chosen over* hamen/compose_skill (353★, MIT, single strict audit — narrower) and
   aldefy/compose-skill (551★, NOASSERTION license → rejected). Strengths: the only library
   covering Baseline Profiles + Macrobenchmark + R8 + stability as skills, with an audit
   orchestrator producing written reports. Weaknesses: heavy (25 skills) — load focused ones.
   Auto: yes (perf work). Phases: 3.2 jank gates, 3.3-3.5, 4.
6. **new-silvermoon/awesome-android-agent-skills (5 selected)** (Apache-2.0, 82900ea).
   Strengths: standardized Android skills incl. a11y and gradle logic. Weaknesses: shallow
   per-skill depth; complements rather than replaces the above. Auto: yes. Phases: all.

### Engineering
7. **sanyuan0704/sanyuan-skills → code review** (MIT, 08b6572) — *chosen over*
   awesome-skills/code-review-skill (React/TS-focused) and mhattingpete/skills-marketplace
   (generic). Strengths: expert review across SOLID/security/perf/error handling. Weaknesses:
   JS-flavored examples. Auto: yes (post-write). Phases: every commit gate (with repo's
   code-review + reviewers).

## Rejected (with reasons)

- wshobson/agents — no usable LICENSE found → rejected (license rule).
- plugin87/ux-ui-agent-skills — DTCG tokens/42 components (tempting for tokens.json) but no
  LICENSE → rejected; revisit if licensing clarified.
- Trystan-SA/claude-design-system-prompt — reverse-engineered system prompt, provenance
  unclear → rejected.
- dominikmartn/hue, aldefy/compose-skill (NOASSERTION), hamen/compose_skill, LottieFiles/
  motion-design-skill, Jakubantalik/transitions.dev, justinwetch/HIGAgentSkills,
  axiaoge2/Apple-Hig-Designer, Jonnycatx/apple-full-stack-genius-skill, haider-nawaz/
  liquid-glass-skill (36★, unlicensed) — superseded by the chosen set or unlicensed.
- SystemUI/AOSP/Launcher3/LSPosed/Magisk/JADX/APKTool/smali/SELinux: **no quality community
  SKILL.md exists** — canonical repos (LSPosed GPL-3.0, Magisk GPL-3.0, jadx Apache-2.0,
  Apktool Apache-2.0, SELinuxProject/selinux-notebook) are cited as references inside the
  project skills, which encode our validated practice instead.

## Recommended stack per phase

| Phase | Stack |
|---|---|
| **3.2 validation** | project-systemui-hook-injection + project-magisk-lab (perms/SELinux) + project-ui-forensics (no-vision validation) + project-baseline-profiles (jank gates) + skydoves audit + chrisbanes stability/recomposition + design-motion-principles (gesture feel) + ios-design-guidelines (visual review) |
| **3.3 notifications** | project-systemui-hook-injection + ios-liquid-glass + ios-design-guidelines + chrisbanes (state/animations) + android-accessibility + project-ui-forensics |
| **3.4 system panels** | project-systemui-hook-injection + project-magisk-lab + project-android-re-toolchain (surveys) + android-gradle-logic |
| **3.5 springboard** | chrisbanes (all) + skydoves + design-motion-principles + ios-design-guidelines + ios-liquid-glass + android-accessibility + project-baseline-profiles + project-ui-forensics |
| **Phase 4 launcher/production** | all of the above + project-adr-authoring + sanyuan code review + repo reviewers + perfetto skills (global) + project-magisk-lab (production sepolicy) |
