# CONTROL_CENTER_RESEARCH.md — Apple Control Center: Visual & Interaction Analysis

> Phase 3.2 research. Compiled from primary Apple sources (HIG Materials/Motion, WWDC18 "Designing
> Fluid Interfaces", WWDC23 "Animate with springs", WWDC25 Liquid Glass sessions 219/220/284/323/310 —
> all via the project digests in MATERIAL_RESEARCH.md / MOTION_RESEARCH.md / APPLE_UX_ANALYSIS.md),
> official Apple pages (apple.com/ios), and deep reviews of the iOS 18 Control Center redesign
> (MacStories review — Federico Viticci; Android Authority hands-on — Dhruv Bhutani; MacRumors guide).
> **Principles only — no Apple assets, fonts, sounds, or code.** Design decisions below are OURS,
> constrained by the Motorola Edge 20 + our design system (GlassEngine v2, MotionEngine v2, tokens).
>
> **Tooling note:** no image-analysis tool was available in this environment, so screenshots were
> not analyzed pixel-by-pixel; analysis is text-source based (Apple docs + professional reviews).

---

## 1. What Control Center is (functionally)

Control Center is a **layer of glass floating above any app**, reachable from anywhere with one
gesture (top-right swipe), holding the OS-level controls users need most: radios, brightness,
volume, media, focus. Two eras matter:

- **iOS 11–17:** single page, tile-based controls, connectivity cluster top-left (2×2: airplane,
  Wi-Fi, Bluetooth, cellular), large vertical brightness + volume sliders, now-playing card, focus
  tile, quick toggles; long-press expands a control into a larger panel. *(MacStories, iOS 17-era
  layout)*
- **iOS 18+:** a 4×8 grid of **resizable** controls on **multiple vertically-stacked pages** with a
  page-symbol rail on the right edge; controls are "more rounded than before"; editable via a
  controls gallery + jiggle mode; dedicated music and network panes exist by default. Apple calls
  it "a slimmed-down, monochrome Home Screen" *(MacStories)*. A power button sits top-right
  *(MacStories screenshot)*.

## 2. Visual analysis (extracted principles)

| Principle | Evidence | Our translation |
|---|---|---|
| **One continuous glass field** | CC = a single material layer floating over content; individual controls read as one cluster, not a set of app cards | The panel is ONE window with window-level backdrop blur; tiles share one material treatment (tint/vibrancy/specular/rim via `glassMaterial` + `glassLighting`); no per-tile blur |
| **Grid structure** | iOS 18: 4 columns; controls 1×1 / 2×1 / 1×2 / 2×2 (and larger) *(Android Authority: "4×8 grid"; MacStories)* | 4-column grid for connectivity cluster (2×2), sliders span 2 columns; token-driven (`ControlCenter.*` grid tokens) |
| **Rounded geometry** | "Controls in the new Control Center are also more rounded than before" *(MacStories)* | Tile radius = `Tokens.Radius.largeCard`-ish (18–24dp scale); panel radius = sheet-tier; tokens only |
| **Monochrome, low-chroma chrome** | "slimmed-down, monochrome Home Screen" *(MacStories)*; Liquid Glass content is white/neutral chrome with adaptive tint *(HIG)* | Tiles are neutral glass with accent color ONLY for state (on-tint); labels secondary; active toggles get accent fill per our tokens |
| **Glass responds to content behind** | Liquid Glass: blurs and adjusts luminosity continuously; tint shifts hue/brightness with backdrop; adaptive flip of small elements *(WWDC25 219, HIG)* | Backdrop blur = window `FLAG_BLUR_BEHIND` (real-time); `adaptiveTint()` (tintBias token) on tiles; content-aware shadow via `adaptiveShadow(contentLuma)` |
| **Light & depth** | Specular sheen, Fresnel rim, on-touch illumination, dynamic shadow opacity as depth governor *(WWDC25 219; MATERIAL_RESEARCH §4–6)* | `glassLighting()` (two-pass sheen + cut-glass rim) + `adaptiveShadow()` per elevation token |
| **Vibrancy** | Content on glass auto-vibrants (brightness/saturation lift ~140–160% sat, ~5% lum) *(MATERIAL_RESEARCH §7)* | Already in `glassMaterial` (Saturation blend + Plus lift per tokens) |
| **Information hierarchy** | Radios first (top-left cluster), then brightness, then volume, media prominent, focus/quick actions below; pages for overflow *(iOS 17/18)* | Top: connectivity cluster; then brightness + volume sliders; media card; focus card + quick actions row; panel scrolls only if needed (first milestone = one page, quality over quantity) |
| **Spacing/rhythm** | Controls sit tight in a grid with small gaps; the field has breathing room from screen edges; sliders are tall with generous touch targets | Token gaps (grid gutter ~10–12dp), panel margin 16–20dp, ≥48dp touch targets |
| **Corner presentation** | Panel emerges from the top-right, following the screen's corner language | Panel anchored top-right with rounded top corners; matches status-bar-region trigger |

## 3. Interaction analysis (extracted principles)

| Principle | Evidence | Our translation |
|---|---|---|
| **Trigger: top-right swipe, from anywhere** | "swipe down from the top right corner" *(Android Authority)*; consistent since iPhone X *(MacStories)* | Hook consumes ACTION_DOWN in the top-right region (x > 66% width, y < 400px — validated 3.1), writes `cc-open`, returns `true` so Moto QS never tracks it |
| **Open = follow + spring settle** | Fluid Interfaces: presentation follows the finger; on release, springs settle from the LIVE value (interruptible) *(WWDC18)* | Overlay animates in with MotionEngine spring from the live offset; the trigger event arrives via file bus (~68ms spike baseline) — entrance is spring-animated, NOT finger-tracked cross-process |
| **Close = drag-down, velocity-aware** | Panel is dismissed by dragging it down; dismissal flings on velocity *(WWDC18; iOS CC behavior)* | In-process interactive drag on the overlay (zero latency): drag follows finger, release settles with spring; fling-down (velocity > threshold) or drag past fraction → dismiss |
| **Interruptibility** | "animations must be interruptible" *(WWDC18)*; springs retarget from current value | Every animation uses `Animatable`/springs — redirectable at any instant; no forced durations for interactive motion |
| **Single continuous gesture across pages** | "keep swiping after activating CC with one seamless continuous gesture to highlight a subpage" *(MacStories)* | Not in milestone 1 (no sub-pages yet); documented as the pattern for future pages |
| **Long-press expands** | "long-press default controls like Now Playing or brightness to expand them" *(MacStories)* | Milestone 1: sliders are always expanded (iOS 17 style); long-press expansion deferred with the pattern documented |
| **Haptics** | Toggles give subtle haptic confirmation (observed iOS behavior) | `HapticEngine.perform` on toggle press (selection), panel settle (light), slider milestones (selection) — per our haptics tokens |
| **Press feedback on down** | Fluid Interfaces: "motion starts on press, not release"; 80ms down / 160ms up asymmetry (research) | `pressFeedback`/`pressScale` on every tile (MotionEngine v2 timings) |
| **Accessibility** | HIG: reduce-motion cross-fade; Increase Contrast → B/W glass + contrasting border *(WWDC25 219)* | MotionEngine reduced-motion multiplier; tokens; semantics: roles/state/descriptions on all tiles, focusable, ≥48dp targets, screen-reader state announcements |
| **Reduced motion** | Cross-fade instead of slide *(HIG Motion)* | When reduced-motion is on, open/close cross-fades (MotionEngine handles via duration multiplier + we use fade) |

## 4. Implementation decisions (ours)

1. **Surface hosting:** the CC renders in a root-granted **overlay window hosted by the launcher**
   (ADR-0005/0034 — validated in 3.1; SystemUI stays a thin hook). "Replaces the SystemUI surface"
   in the sense the user never sees Moto QS for the CC gesture.
2. **Rendering architecture:** one window (`TYPE_APPLICATION_OVERLAY`, `FLAG_LAYOUT_IN_SCREEN`)
   with **`FLAG_BLUR_BEHIND`** as the ONLY real blur (ADR-0030: one blur/surface). Tiles use the
   v2 compositing approximation (tint + vibrancy + specular + rim) — no per-tile RenderEffect.
3. **Motion:** open/close via MotionEngine springs (damping 1.0/0.8, stiffness per token);
   interactive close drag in-process; haptics on settle/toggle; press feedback on down.
4. **State:** host-local state drives Android system APIs directly (ConnectivityManager,
   WifiManager, BluetoothAdapter, AudioManager, MediaSessionManager, CameraManager,
   Settings.System, NotificationManager DND). No Android functionality is rebuilt — we wrap it.
5. **Events:** `cc-open` (SystemUI → host) and `cc-close` (host → SystemUI, informational) via the
   config-store event bus (ADR-0035). Toggle state does NOT round-trip through SystemUI.
6. **Graceful degradation:** overlay-permission missing or module disabled → in-app CC sheet
   fallback (ADR-0005). Rollback = flag/module disable, unchanged from 3.1.
7. **Component set (milestone 1):** connectivity cluster (Wi-Fi/Bluetooth/Airplane/Mobile data),
   brightness slider, volume slider, media card (active session), focus card (DND), quick actions
   (flashlight, rotation lock, hotspot).

## 5. Differences from stock Android Quick Settings (what we deliberately change)

| Aspect | Stock Android QS | Our CC |
|---|---|---|
| Gesture | Center swipe → notifications; right swipe → QS | Top-right region swipe (x>66%, y<400px) → CC; center/left region keeps stock behavior; QS preserved underneath for other gestures |
| Material | Opaque/Material You surfaces, per-tile tints | One continuous glass field, adaptive tint, specular/rim, window blur |
| Layout | 2-column vertical tile list + huge panel | 4-column grid, connectivity 2×2 cluster, horizontal slider cards, media card |
| Sliders | Horizontal, small, in panel | Tall horizontal cards, slider spans card width, big touch targets |
| Media | Small control in panel | Full media card with artwork placeholder, title/artist, transport |
| Toggle feedback | Ripple + color | Press-scale + glass lighting + haptics (80/160ms asymmetry) |
| Close | Swipe up / back | Drag panel down (velocity-aware), tap outside |
| Customization | Limited reorder | Deferred (grid customization is a later milestone, iOS-18-style) |

## 6. Design tradeoffs (honest)

1. **Event-triggered open, not finger-followed.** Cross-process finger tracking via the file bus
   would jank (~ms latency × per-frame writes). We trade "the panel catches your finger" for a
   spring entrance + full in-process interactive close. This is the single biggest fidelity gap
   and the correct engineering call (ADR-0034's 68ms baseline).
2. **Window blur, not per-tile blur.** Real per-surface refraction is GPU-shader work (Phase 8);
   window blur + compositing approximation is the credible 120Hz path now (ADR-0030/0028).
3. **iOS 17 layout DNA, iOS 26/27 material.** A single fixed page (iOS 17 structure) is the right
   milestone-1 scope; resizable multi-page grids (iOS 18) need a layout engine we don't need yet.
4. **"Monochrome" chrome vs. Moto's colorful QS.** Our tiles are neutral glass with accent-state
   fills; we accept that Moto's QS (still reachable elsewhere) shows a different visual language.
5. **No live media artwork extraction** (media projection/notification interception is out of
   scope and privacy-hostile); the media card shows session metadata via MediaSessionManager
   (title/artist/state) with a placeholder artwork surface.
6. **Accessibility parity is best-effort** on a rooted, modified device: we implement standard
   semantics/focus/reduced-motion; TalkBack works because it's a normal Compose window.

## 7. Sources

- Apple HIG — Materials, Color, Motion, Accessibility (via MATERIAL_RESEARCH.md digest)
- WWDC18 "Designing Fluid Interfaces"; WWDC23 "Animate with springs"; WWDC25 Liquid Glass
  sessions 219/220/284/323/310 (via MOTION_RESEARCH.md / MATERIAL_RESEARCH.md digests)
- apple.com/ios — iOS 27 Liquid Glass refinements (uniform refraction, contrast, ultraclear→tinted)
- MacStories — "iOS and iPadOS 18: The MacStories Review" §4 (Control Center), F. Viticci
- Android Authority — "iOS 18 Control Center hands-on", D. Bhutani
- MacRumors — "Everything New With the iOS 18 Control Center" (guide)
- Project: ADR-0005/0019/0021/0028/0030/0032–0035; GLASS_ENGINE_V2.md; MOTION.md
