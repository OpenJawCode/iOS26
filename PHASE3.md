# PHASE3.md — Android System Surface Integration (LSPosed)

> Status: **3.1 in progress** (opened 2026-08-02). Doctrine: prove the injection architecture
> FIRST, one surface at a time, with rollback everywhere. No fake screenshots, no standalone
> demos — real Android events drive real UI (engineering rules).

## 1. Review recap (ADRs, engines, contracts, budgets, 2.6 results)

All 31 ADRs + GlassEngine v2 + MotionEngine v2 + component contracts + budgets (8.33ms @120Hz,
one blur/surface) + 2.6 measurements (gallery 25ms median) reviewed. Fork finding (R7): the
JingMatrix LSPosed legacy API is broken for new modules → **modern libxposed API is the only
path**. Hook target from the survey (R5): `NotificationPanelViewController#onInterceptTouchEvent`.

## 2. Execution plan

| Phase | Goal | Exit criterion |
|---|---|---|
| 3.1 LSPosed Modern API Foundation | Module skeleton, modern API, targeting, flags, crash protection, rollback | ✅ **DEVICE-VALIDATED 2026-08-02** — hook live in SystemUI, event bridge proven, rollback ×3 stock (reboot-persistence pending user-approved reboot) |
| 3.2 Control Center / QS Replacement | Glass CC surface over Moto SystemUI QS | Top-right swipe → glass panel; QS preserved underneath; <8.33ms budget |
| 3.3 Notification Surface | Presentation-only: cards, grouping, media, priority | Android behavior intact; only presentation replaced |
| 3.4 System Panels | Volume/power/screenshot/dialogs where possible | Rollback-safe; Android functionality preserved |
| 3.5 Springboard Prototype | Home surface (real apps): grid, dock, pages, wallpaper | Real-app grid on device; bridge to Phase 4 launcher |

## 3. Minimal viable integration path (3.1)

1. Modern module (`hooks/control-center`): `XposedModule` entry via `java_init.list`,
   `module.prop` (`targetApiVersion=101`), scope `com.android.systemui` + `system`.
2. **Process targeting**: entry checks `packageName == com.android.systemui`; nothing else.
3. **Feature flags**: file-based (shared-store path, ADR-0021), default OFF. No flag → no hooks.
4. **Crash protection**: every hook wrapped; any failure → disable ALL hooks this process + log
   (never partially hooked). Rollback = delete flag file / disable module in manager.
5. **Rendering bridge**: hooks emit EVENTS only (ADR-0019 file-event bus); UI renders in the
   launcher overlay (ADR-0005). SystemUI never draws our UI in 3.1/3.2 unless the QS surface
   needs in-process compositing — decision in ADR-0033.

## 4. ADRs (this phase)

- ADR-0032 SystemUI injection strategy (modern libxposed only; events-not-UI in SystemUI)
- ADR-0033 Hook architecture (seam, adapters, one class per surface, flag-gated)
- ADR-0034 Rendering bridge (file-event bus; overlay host owns all rendering)
- ADR-0035 State synchronization (config store as the single state channel; no binder)
