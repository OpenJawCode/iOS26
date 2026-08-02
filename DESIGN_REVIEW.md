# DESIGN_REVIEW.md — Phase 2 Design Review

> Every visual decision carries: rationale · alternatives considered · trade-offs · maintenance
> implications. Doctrine influence is cited per decision (skills: apple-design, emil-design-eng,
> frontend-design; Apple HIG; accessibility WCAG 2.2).

## 1. Design language: Liquid Glass, anchored iOS 26 (ADR-0011)

- **Rationale:** documented, stable reference; token abstraction absorbs iOS 27 later.
- **Alternatives:** iOS 27 anchor (less reference material); OS-agnostic language (no identity).
- **Trade-offs:** fidelity costs engineering (blur budgets); mitigated by tokenized glass modes.
- **Maintenance:** glass intensity is a runtime token (R3) — the iOS 27 translucency slider maps 1:1.

## 2. Token architecture (ADR-0022, D-P2.4): generated from JSON

- **Rationale (Kowalski doctrine — one source, hierarchy):** tokens.json → generated Kotlin;
  schema-validated; CI freshness. Nothing hardcoded, nothing duplicated.
- **Alternatives:** hand-written Kotlin tokens (drift risk); Compose Styles API (rejected —
  requires compileSdk 37, ADR-0017 freeze, D-P2.1).
- **Trade-offs:** generation adds a build step; the generator is deliberately narrow (tokens only).
- **Maintenance:** adding a token = JSON + regen; TS contracts later reuse the same JSON (Phase 6).

## 3. Component library (ADR-0025): pure foundation, no material3

- **Rationale (HIG semantics):** iOS-shaped components need iOS-shaped primitives; M3's token
  system would create a second source of truth.
- **Alternatives:** material3 as infrastructure (two token systems, permanent debt — rejected in
  the Phase 2 interview); fully hand-rolled everything (no a11y scaffolding — rejected).
- **Trade-offs:** we own a11y scaffolding (roles set, focus ring token, 48dp targets, reduced
  motion) — documented, token-backed.
- **Maintenance:** auto-theming by construction (semantic tokens only).

## 4. Glass engine (ADR-0023): window blur + RenderEffect + scrim fallback

- **Rationale:** the ONLY public APIs for real behind-window blur on API 33; verified in spike.
- **Alternatives:** capture-based sampling (MediaProjection — rejected: privacy + UX); no blur
  (rejected: the language IS glass).
- **Trade-offs:** window blur cost on the 778G → radius caps + scrim degradation mode.
- **Maintenance:** all blur paths behind GlassEngine; single token flips degradation.

## 5. Motion (ADR-0024): token-driven curves/springs, reduced-motion scheme

- **Rationale (apple-design doctrine):** interruptibility, press-response, velocity inheritance.
- **Alternatives:** tween-only (feels mechanical — rejected); hand-tuned values (duplication —
  rejected).
- **Trade-offs:** springs are more expensive than tweens (negligible at our surface counts).
- **Maintenance:** motion tokens are the single vocabulary for all future surfaces.

## 6. Dynamic color: wallpaper → Palette → token-shaped accent

- **Rationale:** iOS tinting behavior with zero capture APIs.
- **Alternatives:** M3 dynamic color (rejected with M3); fixed accent (rejected — wallpaper
  adaptation is a deliverable).
- **Trade-offs:** Palette sampling is approximate; extraction moved off-thread (measured finding).
- **Maintenance:** accent is just another TokenSet override — themes compose.

## 7. Accessibility (cross-cutting)

Contrast pairs as tokens (AA), focus ring token, 48dp touch targets, sp-only typography (text
scaling safe), reduced-motion multiplier, haptics-off support. Rationale: WCAG 2.2 AA is the
contract; tokens make compliance automatic per theme.

## 8. Performance posture

Budgets defined before features (PERFORMANCE.md); first baseline recorded (gallery: 22ms median
first-launch worst-case); one real finding fixed (palette off main thread). No premature
optimization: per-screen budgets are enforceable only when screens exist (Phase 3).

## 9. Remaining risks & debt

| Risk/Debt | Owner | Mitigation |
|---|---|---|
| Gallery baseline is worst-case; real budgets need screens | springboard-owner | Phase 3 measurement |
| Component depth is v1-lean (visual behavior per component) | design-owner | deepened per surface phase |
| Screenshot baselines deferred (Roborazzi/Preview tooling) | qa-owner | Phase 3 CI AVD job |
| Font is Inter-class stand-in (ADR-0012) — SF exactness off | design-owner | documented, acceptable |
| Square-corner sheet edge cases on API 33 (blur vs rounded corners) | glass-owner | scrim fallback mode |
