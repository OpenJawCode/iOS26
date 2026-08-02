# libs/design

**The visual language every future module inherits** (Phase 2 mandate, PHASE2.md).

- `tokens/tokens.json` — single source of truth (D-P2.4, schema-validated) → generated `Tokens.kt`
- `theme/` — ThemeEngine: TokenSet switching, light/dark, glass intensity, dynamic color (wallpaper → palette)
- `engines/` — glass, blur, vibrancy, material, shadow, motion, haptic, animation
- `components/` — token-derived component library (no material3, D-P2.3)
- `springboard/` — SpringboardSpec: grid math, icon spec, dock, folders, pages

Owners: design-owner (tokens/theme/engines), springboard-owner (springboard/), doc-owner (docs).
Regenerate tokens after any tokens.json change: `./gradlew :libs:design:generateDesignTokens` (CI checks freshness).
