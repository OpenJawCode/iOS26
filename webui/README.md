# webui/

The layout editor + presets SPA (ADR-0010). React 19 + Vite + TypeScript strict. Types are **generated** from `libs/schema` (zod) into `webui/src/generated/` — never hand-written. Served by the companion's Ktor server; tested with vitest + Playwright.

Owner: webui-owner. Never imports Android-side code; talks HTTP + generated contracts only.
