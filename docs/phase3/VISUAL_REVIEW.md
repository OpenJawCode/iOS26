# VISUAL_REVIEW.md — Phase 3.2 (design-level review with installed skills)

> Method (project-ui-forensics): structure via a11y trees, colors via pixel sampling,
> design claims mapped to tokens. Skills used: ios-design-guidelines (HIG),
> ios-liquid-glass, design-motion-principles, project-ui-forensics. Device rendering is
> pending (DEVICE_VALIDATION.md) — this review covers the implementation + the strongest
> available evidence; severity reflects impact on the shipped experience.

## Findings

### 1. [CRITICAL] Overlay never presents pixels on the device
- **Describe**: window attaches/inputs/semantics work; no pixels reach the screen.
- **Why**: host-window visibility on Moto firmware; AM overlay-UI tracking ("setHasOverlayUi
  unknown pid"); not fixed by FGS or blur removal.
- **Severity**: CRITICAL (acceptance blocker — "runs on the Motorola Edge 20").
- **Fix**: isolate activity-visible dependency (test with launcher foreground); if required,
  hold visibility via priv-app/persistent (Phase-4 packaging, ADR-0008) or a transparent
  host activity; verify pixel presentation first, then re-add blur via RenderEffect.

### 2. [HIGH] Slider drag vs panel drag contention (design review, unverified on device)
- **Describe**: the panel's drag handler uses `awaitFirstDown(requireUnconsumed = false)` —
  a drag starting on a slider also moves the panel.
- **Why**: child sliders don't consume the down; panel tracks any vertical dy.
- **Severity**: HIGH (interaction correctness).
- **Fix**: sliders consume the down (awaitSliderDrag consumes on first event); panel
  requires an unconsumed down. ~10-line change.

### 3. [MEDIUM] Volume slider writes target STREAM_MUSIC regardless of context
- **Describe**: CC volume always adjusts media volume (correct for iOS-parity on a phone,
  but the label is ambiguous with ringer volume).
- **Why**: AudioManager STREAM_MUSIC wrapper (ADR-0037 wrap-don't-rebuild).
- **Severity**: MEDIUM.
- **Fix**: label the card "Media volume"; ringer/multistream handling = later phase.

### 4. [MEDIUM] Media card lacks artwork entirely
- **Describe**: metadata-only card (title/artist/transport); empty artwork surface.
- **Why**: ADR-0037 deliberately excludes artwork capture (privacy + scope).
- **Severity**: MEDIUM (visual richness); HIGH for iOS-parity feel.
- **Fix**: keep the placeholder; artwork pipeline is a separate ADR (needs notification
  access or MediaProjection-free path — do not rush).

### 5. [LOW] Airplane/Cellular tiles in the SAME 2×2 cluster as Wi-Fi/BT is iOS-17 DNA;
   iOS-26/27 pairs them differently (Liquid Glass pill layout)
- **Describe**: cluster matches the research decision (iOS-17 structure) — documented
  trade-off (CONTROL_CENTER_RESEARCH.md §6.3).
- **Severity**: LOW (deliberate).
- **Fix**: none now; revisit with the iOS-18 grid engine (later milestone).

### 6. [LOW] Text labels instead of glyphs on tiles
- **Describe**: "Wi-Fi"/"Bluetooth" text; icon pipeline is a later phase (ADR-0012).
- **Severity**: LOW; readability is good (labelSecondary on glass) but glyphs read better
  at 64dp.
- **Fix**: icon phase (personal-import pipeline, ADR-0012).

### 7. [LOW] Entrance is event-triggered, not finger-followed
- **Describe**: documented trade-off (research §6.1): spring entrance, no cross-process
  finger tracking.
- **Severity**: LOW (accepted); feels slightly less "attached" than iOS.
- **Fix**: none in 3.2 scope; re-evaluate with in-process gesture streaming later.

## Design-system conformance (passes, by construction)

- Tokens-only styling (ADR-0011/0031): tiles/sliders/cards read `Tokens.ControlCenter.*`,
  `Tokens.Spacing.*`, `Tokens.Radius.*` — no component-local values.
- GlassEngine v2: tiles use glassMaterial+glassLighting+adaptiveShadow; panel is one glass
  field (single material, research §2.1).
- MotionEngine v2: spring standard (damping 1.0/stiffness 320), press 80ms/spring-up,
  interruptible Animatable, reduced-motion cross-fade.
- A11y: roles, state descriptions, slider progressBarRangeInfo, ≥48dp targets (64dp tiles,
  88dp slider columns — HIG 44pt minimum exceeded).
- HIG review: touch targets ✓; contrast — labelSecondary on prominent glass needs
  on-device verification once rendering works (pending item).
