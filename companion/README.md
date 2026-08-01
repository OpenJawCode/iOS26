# companion/

The settings hub (ADR-0009) + embedded WebUI server (ADR-0010).

| Module | Purpose |
|---|---|
| `app` | Settings surfaces: theme/tokens, icon packs, wallpaper, module toggles, backup/restore, diagnostics |
| `server` | Ktor server: token auth, config read/write via `libs/config`, serves WebUI |
| `import` | On-device personal asset import (ADR-0012) — never committed to repo |

Owner: companion-owner. Writes config only through `libs/config`; never renders the experience. Tests: Tier 2 (Compose + Playwright).
