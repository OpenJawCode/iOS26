# iOS26 — An iOS 26/27-inspired Android experience for the Motorola Edge 20

**Status: Phase 0 — Discovery** · **License: GPL-3.0** · **Target: stock Motorola Edge 20, Android 13 (API 33), rooted (Magisk + Zygisk)**

iOS26 is an open-source, production-quality engineering project that recreates the iOS 26/27 experience on a stock rooted Motorola Edge 20. It is **not a theme** and it is **not a launcher skin** — it is a modular stack of cooperating components: a Compose launcher, LSPosed system hooks, Magisk provisioning, RRO overlays, a companion settings app, and a WebUI — coordinated by a single schema-validated configuration store.

The design language anchors on **iOS 26 Liquid Glass**, rendered through a fully tokenized design system so that iOS 27 refinements land as a token-set swap, never a rewrite.

## Why this project exists

- Stock My UX Android 13 is the Edge 20's **final firmware** — a permanently frozen hook target that never drifts under OTA patches.
- The device is rooted and the bootloader is unlocked: LSPosed, Magisk, and system-level overlays are all viable.
- The goal is a clean, modular, extensible, well-documented codebase capable of sustaining a production-quality experience **for years**, built to outlive its first release and to be maintained by humans and AI agents together.

## The stack

| Component | Path | Role |
|---|---|---|
| Launcher | `launcher/` | iOS-style springboard: grid, dock, folders, pages, widgets, App Library, Spotlight, Control Center panel host |
| LSPosed hooks | `hooks/` | Per-surface system hooks — currently one: the Control Center gesture intercept in SystemUI |
| Magisk module | `magisk/ios26-stack` | One-flash provisioning: systemless priv-app install, system RROs, config-store bootstrap |
| Overlays | `overlays/` | System resource overlays (SystemUI, framework) |
| Companion app | `companion/` | On-device settings hub: theme, icon packs, wallpaper, backup/restore, diagnostics, asset import, embedded WebUI server |
| WebUI | `webui/` | React SPA: springboard layout editor, theme presets, wallpaper gallery, import/export |
| Shared libraries | `libs/` | `config` (the deep module: schema-validated file store), `schema`, `design` (tokens), `domain`, `icons` |
| Assets | `assets/` | Original, permissively-licensed artwork pipeline — no Apple IP, ever |
| Device tests | `device-tests/` | Scripted Tier-3 verification harness for hooks/overlays/Magisk on real hardware |

## Quick start

> The repository is currently in **Phase 0 (Discovery)**. There is no buildable code yet — the Gradle toolchain is bootstrapped in Phase 1. See [ROADMAP.md](ROADMAP.md).

```
# Once Phase 1 lands:
./gradlew :launcher:app:installDebug
```

## Read first

| Doc | What it is |
|---|---|
| [ARCHITECTURE.md](ARCHITECTURE.md) | System architecture, bounded contexts, module boundaries, dependency rules |
| [ROADMAP.md](ROADMAP.md) | Milestone roadmap — Phases 0–10 with exit criteria |
| [CONTEXT.md](CONTEXT.md) | Ubiquitous language glossary + decision log (living document) |
| [CONTRIBUTING.md](CONTRIBUTING.md) | How to contribute: conventions, testing tiers, docs standards |
| `docs/adr/` | Architecture Decision Records — every significant decision, dated and reasoned |
| `docs/conventions.md` | Coding conventions (Kotlin/Compose, TS, schema, commits) |
| `docs/testing.md` | Testing strategy (three tiers) |
| `docs/ci.md` | CI/CD strategy |

## Principles

1. **Decisions before code.** Any significant architectural uncertainty stops implementation until it is resolved — one question at a time, documented as an ADR.
2. **The config store is the spine.** All components share state only through the schema-validated file store. No process may depend on another process being alive.
3. **Thin hooks.** The fragile SystemUI surface is minimized and isolated behind adapter seams.
4. **Tokens, not styles.** Every visual renders through design tokens; themes are token sets.
5. **No Apple IP.** All assets are original or user-supplied on-device.
6. **Single release train.** One version = one installable experience, per-component semver.

## License

GPL-3.0 — see [LICENSE](LICENSE). The root-mod ecosystem (LSPosed, Magisk, Xposed modules) is GPL-3.0; this project stays compatible with it.
