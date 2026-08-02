# MOTION.md — Motion & Haptics

> Doctrine sources: `apple-design` skill (fluid interfaces, WWDC 2018 distilled), `emil-design-eng`
> skill (unseen details compound, press response), Apple HIG. **All motion is token-driven
> (motion group); no animation definition is duplicated or hardcoded.**

## 1. Principles (applied continuously)

1. **Respond on press, not release.** Feedback begins the instant the finger lands (apple-design §1).
   All interactive components scale 0.97 on press via `pressFeedback` + `spring("snappy")`.
2. **Interruptibility is the single most important principle.** Animations start from the CURRENT
   presentation value and are redirectable at any instant — Compose springs provide this natively;
   tweens are used only where tokens define them (e.g., color cross-fades).
3. **1:1 direct manipulation.** Drag targets track the finger exactly (offset included); velocity is
   inherited at release. (Gesture language, §4.)
4. **Nothing appears from nothing.** Surfaces fade/scale in (emil doctrine); popovers scale from
   their trigger, modals stay centered.
5. **Reduced motion is a first-class scheme.** Durations scale by `Tokens.Motion.reducedMultiplier`
   (0.5) when the system animator scale is 0; springs degrade to instant state changes.

## 2. Curves (cubic-bezier tokens)

| Token | Bezier | Use |
|---|---|---|
| `standard` | (0.4, 0, 0.2, 1) | default UI motion |
| `emphasized` | (0.2, 0, 0, 1) | hero/large transitions |
| `decelerate` | (0, 0, 0.2, 1) | entrances (feedback-first) |
| `accelerate` | (0.4, 0, 1, 1) | exits |
| `easeInOut` | (0.42, 0, 0.58, 1) | continuous loops |

## 3. Springs (mapped from iOS UISpringTimingParameters)

| Token | damping | stiffness | Use |
|---|---|---|---|
| `standard` | 0.7 | 300 | generic UI springs |
| `snappy` | 0.5 | 500 | press feedback, switches |
| `gentle` | 0.9 | 150 | sheets, large surfaces |

## 4. Gesture language (spec)

- **Swipe-down from top-right** → Control Center (Phase 3 hook; survey R5 target `onInterceptTouchEvent`).
- **Swipe-down center** → Spotlight/Search (iOS 27 model, research R3).
- **Swipe-up on dock** → App Library; **swipe right on last page** → App Library (iOS convention).
- **Long-press** → context menu (jiggle entry); **drag** → 1:1 tracking with velocity inheritance.
- **Edge swipes** belong to Moto's gesture layer (Phase 0 survey) — the launcher never fights them.

## 5. Durations

`fast 100 · standard 200 · comfortable 350 · slow 500 · entrance 400 · exit 250` ms.
Entrances are longer than exits (perception doctrine: exits snap, entrances bloom).

## 6. Haptics (token → VibrationEffect mapping, API 30+)

| Token | Effect | Typical use |
|---|---|---|
| selection | EFFECT_CLICK | switches, toggles, list selection |
| light | EFFECT_TICK | drag thresholds |
| medium | EFFECT_CLICK | button press |
| heavy | EFFECT_HEAVY_CLICK | destructive confirmations |
| success | EFFECT_DOUBLE_CLICK | completed actions |
| warning/error | EFFECT_DOUBLE_CLICK / EFFECT_HEAVY_CLICK | alerts |

Haptics are fire-and-forget, never on the critical path; disabled when system haptics are off.
