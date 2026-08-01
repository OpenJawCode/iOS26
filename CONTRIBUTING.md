# CONTRIBUTING.md

Welcome. This project is open-source (GPL-3.0), decision-gated, and designed for humans and AI agents to collaborate on. This guide is the contract for both.

## 1. Current state

We are in **Phase 0 — Discovery** (see ROADMAP.md). There is no buildable code yet. The Gradle toolchain bootstraps in Phase 1. Until then, contributions are limited to:

- Phase 0 research (device hook-point survey, iOS 27 design delta, toolchain version pinning)
- Architecture documentation and ADRs
- Repository structure and conventions

## 2. Getting started (when the toolchain lands)

```bash
# Phase 1: bootstrap
./gradlew --version                # after wrapper is added
./gradlew :libs:config:test        # Tier 1
./gradlew testDebugUnitTest        # all unit tests
./gradlew :launcher:app:assembleDebug
```

Prerequisites: JDK 17+, Android SDK (API 33), adb + a wired, rooted Edge 20 for Tier 3. Node 20+ for `webui/`.

## 3. Development workflow

1. **Check the decision gate.** Is your change architectural (new surface, new channel, new contract, dependency change)? If yes: it needs an ADR (or a documented ADR amendment) *before* code. If you're unsure, ask — one question at a time.
2. **Claim your directory.** Work inside the directory you own (AGENTS.md §2). Cross-directory changes require the owner's awareness (note it in the PR).
3. **TDD where it matters.** New behavior in `libs/*` or `hooks/*`: test-first. UI: screenshot tests (Roborazzi) alongside implementation.
4. **Run the tier gate** (AGENTS.md §8) before committing.
5. **Commit conventionally** — `feat:`, `fix:`, `refactor:`, `chore:`, `docs:`, `test:`, `perf:`, `build:`.
6. **Docs in the same PR** — if you changed a term, a decision, or an API, update CONTEXT.md and/or the ADR in the same change.

## 4. Testing strategy (summary)

Full detail in `docs/testing.md`. The non-negotiable commitments:

- Tier 1 (unit) is exhaustive for logic: config store, schema validation, domain, hooks-common.
- Tier 2 (AVD UI + Playwright) runs in CI on every PR: Roborazzi screenshot baselines for every launcher/companion screen, Compose UI tests for critical flows, WebUI E2E against the embedded server.
- Tier 3 (device) is scripted, not ad-hoc: `device-tests/` harness with idempotent flash/verify scripts and pass/fail output. CI-integration via a self-hosted runner is a Phase 9 goal.

**What gets a test:** every bug fix ships with a regression test (Tier 1 or 2). Every new surface ships with screenshot baselines. No exceptions for "it's just hooks" — hooks get Tier 3 scripts.

## 5. Documentation standards

| Doc | Standard |
|---|---|
| README.md | Top-level identity + index. Rarely changes. |
| ARCHITECTURE.md | Structural truth. Changes only via architecture discussion + ADR. |
| CONTEXT.md | Glossary + decision log. Changes whenever a term/decision changes. Append-only for history. |
| ROADMAP.md | Phase status. Updated when a phase opens/closes. |
| docs/adr/ | One file per decision. Template in `docs/adr/README.md`. Status lifecycle: Proposed → Accepted → Superseded. |
| docs/conventions.md | Rules for code shape. Amend by PR, flag in review. |
| docs/testing.md / docs/ci.md | Strategy. Amend by PR. |
| Per-directory README.md | Purpose, ownership, local conventions, test command. Owned by the directory owner. |
| docs/logs/ (gitignored) | Bugs, mistakes, handoffs — append-only operational truth. |

Markdown: ATX headings, no H1 in sub-docs, tables for structured data, fenced code for commands/paths, absolute repo-relative paths (`docs/adr/0001-...md`).

## 6. Review process

- Every PR is reviewed on two axes: **Standards** (conventions, tests, docs) and **Spec** (does it match the decision/ADR it implements).
- Reviewers check the decision gate: no un-ADR'd architecture change lands.
- AI agents review as strictly as humans — same checklist.
- Direct pushes to `main` are allowed during solo development but every commit must still pass the gate. Tag-based releases are protected (see docs/ci.md).

## 7. Agent collaboration rules

- Agents: read AGENTS.md fully before work; respect directory ownership; stop-and-ask on architectural uncertainty; follow skill SOPs literally.
- Humans: review agent output against §8 gate; the agent that wrote the code also writes/updates its tests and docs.
- Handoffs between sessions: `docs/logs/handoffs/` note, per AGENTS.md §7.

## 8. Quality gate (mandatory before any commit)

1. Tier 1 + 2 green for the change (Tier 3 scripts provided where relevant).
2. Lint clean: ktlint + detekt (Kotlin), eslint + tsc (TS).
3. Docs updated in the same change where required (§5).
4. No secrets, no Apple IP, no unrelated reformatting.
5. Conventional commit message.

## 9. Legal

- The repo is GPL-3.0 (ADR-0014). By contributing you license your contributions under it.
- **No Apple intellectual property** in the repo — no Apple icons, wallpapers, sounds, fonts, or names beyond factual reference (ADR-0012). Original artwork only.
- Secrets stay out: pre-commit hook scans for them; `.env` is never committed.
