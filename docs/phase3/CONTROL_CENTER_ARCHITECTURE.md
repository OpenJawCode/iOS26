# CONTROL_CENTER_ARCHITECTURE.md — Glass Control Center

> Phase 3.2 architecture, as built. Read with CONTROL_CENTER_RESEARCH.md (why) and
> ADR-0036/0037/0038 (decisions).

## 1. Data flow (ADR-0038: SystemUI stays a thin hook)

```
Motorola SystemUI                          Launcher process (overlay host)
──────────────────────                     ────────────────────────────────
top-right swipe (x>66% w, y<400px)
   │  hook consumes ACTION_DOWN
   │  (Moto QS never tracks it)
   ▼
ControlCenterModule.onQsIntercept
   └─ writeEvent("cc-open")  ──▶  shared store events zone (tmp+rename, ADR-0019)
                                      │  PollWatcher (200ms, spike-proven)
                                      ▼
                                 CcHost.raise()
                                      ├─ CcUiState.refresh()   (Android APIs = truth)
                                      └─ CcOverlayWindow.show()
                                           └─ ControlCenterSurface (Compose)
                                                 ├─ entrance spring (event-triggered)
                                                 ├─ in-process drag-to-close (velocity-aware)
                                                 └─ toggles → Android system APIs
                                 CcHost.dismiss()
                                      └─ writeEvent("cc-close") (informational)
```

**Two-process rule (ADR-0037):** SystemUI writes exactly ONE event per gesture and reads
nothing. All UI state lives in the host and reflects Android's own state — the surface can
never desync from the OS because it never holds truth.

## 2. Module layout

| Module | Role |
|---|---|
| `hooks/control-center` | The touch seam (validated 3.1; unchanged except no changes needed) |
| `launcher/control-center` | NEW — overlay host, state, capability wrappers, surface |
| `libs/config` | Typed events evolved: `writeEvent(type)` → `$type.json`, `consumeEvent` |

`launcher/control-center`:
- `CcHost.kt` — flag gate, event watcher, raise/dismiss, graceful degradation
- `window/CcOverlayWindow.kt` — ONE overlay window (TYPE_APPLICATION_OVERLAY +
  FLAG_LAYOUT_IN_SCREEN + **FLAG_BLUR_BEHIND radius 30** — the only real blur, ADR-0030);
  lifecycle/saved-state owners attached by `OverlayOwners.java`
- `window/OverlayOwners.java` — Java owner attach (toolchain workaround, see §4)
- `state/CcControllers.kt` — capability wrappers (Wi-Fi, BT, airplane, mobile data,
  flashlight, rotation, hotspot, focus/DND, brightness, volume)
- `state/CcUiState.kt` — surface state holder (Android = source of truth)
- `state/MediaSessionState.kt` — active session metadata + transport (no artwork capture)
- `ui/ControlCenterSurface.kt` — the surface: scrim, panel, entrance/dismiss gestures
- `ui/CcTiles.kt` — CcTile (the only tile), connectivity cluster, slider cards
- `ui/CcCards.kt` — media + focus cards; `ui/MediaButton.kt`

## 3. Rendering (ADR-0036)

- **One window, one blur**: window-level blur-behind (SurfaceFlinger composited) is the ONLY
  real blur. Tiles use the v2 compositing approximation (`glassMaterial` tint/vibrancy +
  `glassLighting` specular/rim + `adaptiveShadow`) — no per-tile RenderEffect.
- **Entrance** = event-triggered spring (MotionEngine "standard": damping 1.0, stiffness 320)
  from off-screen; the file bus is the trigger, not a finger tracker (68ms baseline, research §6).
- **Close** = in-process interactive drag: finger-follow via `Animatable.snapTo` per event,
  release settles with the same spring; fling (velocity > 1200px/s token) or drag past 45%
  dismisses; spring drives the exit from the live offset — interruptible at any instant.
- **Reduced motion**: cross-fade entrance/exit when animator scale is 0 (HIG).
- **Haptics**: settle on entrance, selection on toggles (tokens), all guarded.
- Theme is provided by the overlay itself (the window hosts no Activity): `Ios26Theme` +
  `LocalGlassIntensity = Prominent` (research: CC is prominent glass).

## 4. Known toolchain workarounds (documented in code)

- The Kotlin compiler in this toolchain (AGP 9 built-in Kotlin 2.4.10) cannot resolve the
  KMP-published `ViewTreeLifecycleOwner` / `ViewTreeSavedStateRegistryOwner` /
  `SavedStateRegistryOwner` declarations (unresolved references — plain classes from the same
  aars resolve fine). The owner attach is therefore implemented in **Java** (`OverlayOwners`),
  which consumes the same JVM API without issue. The saved-state controller additionally
  requires the owner lifecycle at INITIALIZED during creation (raised to RESUMED after
  attach+restore) — see the comment in `OverlayOwners.java`.
- AGP 9 compiles Kotlin before Java, so Kotlin invokes `OverlayOwners` reflectively.

## 5. Safety (unchanged contract from 3.1, all verified on device)

- `control-center.flag` gates the whole surface (no flag = host idle = stock).
- Module disable in LSPosed Manager = stock (framework rejects the module).
- Forced-failure flag = all-or-nothing (nothing hooked).
- Host dead = hook writes land in the void; system unaffected (graceful).
- No overlay permission = in-app sheet fallback (ADR-0005) — `CcHost.isActive()` gates it.
- **Every toggle is `runCatching`-wrapped; every haptic is guarded** — a permission problem
  can never take the surface or the process down.
