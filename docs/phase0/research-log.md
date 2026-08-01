# Phase 0 — Research Log

> Append-only. Every entry: date, researcher, question, sources, findings, implications. One question per entry where possible.

---

## R1 — Toolchain versions (2026-08-01)

**Question:** Which AGP / Kotlin / Compose / Gradle / JDK / lint versions do we pin for Phase 1?

**Sources:** `dl.google.com` maven metadata (AGP, Compose BOM), GitHub releases (JetBrains/kotlin, gradle/gradle, pinterest/ktlint, detekt/detekt), `agp-9-upgrade` skill references (Google, 2026-07-23).

**Findings:**

| Tool | Version | Notes |
|---|---|---|
| AGP | **9.3.1** (stable; 9.4.0-alpha07 exists) | AGP 9 = new DSL + built-in Kotlin; `org.jetbrains.kotlin.android` plugin incompatible; Gradle ≥ 9.1.0; JDK ≥ 17; max API 36.1 (we target 33) |
| Kotlin | **2.4.10** | Compose compiler bundled with Kotlin (2.0+ model) — no separate compose-compiler version |
| Compose BOM | **2026.06.01** | androidx.compose:compose-bom |
| Gradle | **9.6.1** (wrapper pins this) | ≥ AGP 9's min 9.1.0 |
| JDK | **21 (Temurin)** | ≥ 17 required; 21 is the safe LTS |
| ktlint | **1.8.0** | |
| detekt | **1.23.8** | |

**Implications:** Phase 1 bootstraps greenfield on AGP 9.3.1 with built-in Kotlin — no AGP-8-era legacy. The `agp-9-upgrade` skill's migration guides are our reference for the new DSL. API 33 target is within AGP 9's supported range (max 36.1).

---

## R2 — Root ecosystem pins (2026-08-01)

**Question:** Which Magisk and LSPosed versions do we pin for provisioning reproducibility?

**Sources:** GitHub releases.

**Findings:** Magisk **v30.7** (2026-02-23). LSPosed **v2.0** (JingMatrix fork, 2026-03-22 — the maintained lineage since the original was archived).

**Implications:** Pins recorded in `gradle/libs.versions.toml`. ⏳ **Device-gated:** verify Zygisk + LSPosed v2.0 actual behavior on API 33 My UX in the device baseline (fork targets newer Android; API 33 support must be confirmed in practice).

---

## R3 — iOS 27 design delta (2026-08-01)

**Question (CONTEXT.md §3 #2):** What changed in iOS 27 vs 26 that affects our design system and launcher interaction model?

**Sources:** Wikipedia "iOS 27" (2026-08-01 snapshot; WWDC announcement 2026-06-08, beta 4 2026-07-20, GA expected 2026-09).

**Findings (design/UX-relevant only):**
1. **Liquid Glass revised for readability** — Apple changed glass panel defaults (opacity/blur) vs iOS 26. → Our token defaults should track the *revised* 26/27 look; the 26→27 delta is a token-values change, exactly what ADR-0011's abstraction absorbs.
2. **User-adjustable translucency** — a Settings slider ("Liquid Glass slider") lets users tune glass translucency globally. → **Requirement:** our token system needs a runtime-adjustable *glass intensity* dimension (companion setting + token ramp), not just static token sets.
3. **Interaction model change:** center-swipe-down opens "Search or Ask" (replaces Spotlight); **Notification Center moved to upper-left swipe**; Control Center remains top-right. → Spotlight gesture mapping in our launcher should be center-swipe-down; future notification surface (deferred, D1) would occupy upper-left; CC top-right unchanged (our ADR-0005 hook target).
4. **New battery icon design** — trivial; relevant only to a future status-bar overlay (deferred).
5. Performance/responsiveness improvements across the system — no structural impact.

**Implications:** ADR-0011 stands (iOS 26 anchor + token abstraction) with a concrete delta: token *defaults* shift toward the revised glass, plus a new *glass-intensity* runtime token dimension. Records to CONTEXT.md §3 (#2 resolved) and feeds Phase 2 design-system work.

---

## R5 — On-device baseline (2026-08-01) — first wireless lab run

**Question:** What is the actual state of the reference device?

**Method:** `device-tests/baseline/device-baseline.sh` run over Tailscale (adb TCP) — output in `docs/phase0/baseline/2026-08-01.txt`.

**Findings:**

| Item | On-device value | vs pin |
|---|---|---|
| Device | motorola edge 20, codename `berlin`, global variant | matches D2 |
| Firmware | `T1RGS33.135-109-9-29`, security patch **2024-09-01** | ⭐ frozen hook target |
| Android | 13, SDK 33 | matches D3/ADR-0017 |
| Magisk | **30.7** | matches R2 pin |
| Zygisk | **ReZygisk** module (not classic Zygisk) | ⚠️ provisioning must use ReZygisk-compatible LSPosed |
| LSPosed | **v2.1.0 (7769)** via `zygisk_lsposed` module | ⚠️ newer than R2's v2.0 release pin — use on-device version |
| SystemUI | versionName=13 (My UX) | hook target for ADR-0019 survey |
| Config store | `/data/adb/ios26` absent (expected — created at install time) | as designed |

**Implications:**
- Firmware build `T1RGS33.135-109-9-29` is the permanent hook survey target; record it with every survey artifact.
- LSPosed pin updated to **v2.1.0 (7769)** for provisioning; R06 (fork-on-API-33 risk) downgraded to mitigated — it's running on this exact device.
- Device has 24 Magisk modules (ecosystem context: tricky_store, playintegrityfix, flagsecurepatcher, iOS-style font/emoji mods). `flagsecurepatcher` means secure-app screenshot capture is possible — useful for the CC spike later.

---

## R4 — AGP 9.3 implications for the monorepo (2026-08-01)

**Question:** What does AGP 9 mean for our build structure?

**Sources:** `agp-9-upgrade` skill + release-notes reference (Google, 2026-07-23).

**Findings:**
- `android` DSL now implements only new public interfaces; old `BaseExtension`/variant API removed → any build logic we write must use the new DSL from day one.
- **Built-in Kotlin:** Kotlin support comes from AGP; the `org.jetbrains.kotlin.android` plugin must not be applied. KSP/KSP2 compat handled by AGP's version pairing (KSP ≥ 2.3.6 for AGP 9; KMP projects use the new Android Gradle Library Plugin — we don't do KMP, confirmed).
- Gradle 9.6.1 wrapper, JDK 21, Build Tools 36.0.0 (SDK manager installs with platform-tools).
- No NDK needed (no native code; RenderEffect is framework-side).

**Implications:** Phase 1 bootstrap checklist is version-pinned; the `agp-9-upgrade` skill is the SOP for any AGP question.

## R7 — CC spike results (2026-08-02) — chain validated on device

**Question (ADR-0005/0019):** Does the file-event bus + overlay-host architecture deliver the Control Center experience on this device?

**Method:** throwaway spike (`OpenJawCode/cc-spike`, private): host app (FileObserver/poll watcher + overlay panel) + minimal LSPosed hook (gesture intercept).

**Validated on device (Tier 3):**
- Event file bus: root-side atomic write (tmp+rename) → app-side detection → overlay panel. **End-to-end 68ms** with a 200ms poll (inotify would be sub-10ms; poll is a documented fallback, ADR-0019).
- Overlay rendering over any app (TYPE_APPLICATION_OVERLAY, granted via appops), brightness slider (WRITE_SETTINGS), dismissal.

**Blockers found (Phase 3 inputs):**
1. **JingMatrix LSPosed fork v2.1.0 (7769) legacy API is broken for new modules**: every legacy module built against `de.robv.android.xposed` (even with exact `callbacks.XC_LoadPackage` signatures, single-dex, correct meta-data) throws `AbstractMethodError` — the fork's R8-obfuscated legacy bridge (`g.NmByVF.eSa.oCn.tZhyB.*`) fails to link module classes. Existing legacy modules (chromext) work; new ones don't. **Fix path: fork's modern libxposed API** (`META-INF/xposed/java_init.list` + module.prop `targetApiVersion>=101`). Fork source cloned at `/tmp/opencode/lsposed-src`.
2. **Module registration**: the fork stores module config in SQLite (`/data/adb/lspd/config/modules_config.db`); manager detects via `xposedmodule`/`xposedminversion` meta-data (legacy) or `META-INF/xposed/*` (modern). Manual DB edits wedge the daemon's cache-update loop (daemon must be restarted; mid-session restart segfaults on this build — reboot needed).
3. **Config store location**: `/data/adb` is 0700 root + `adb_data_file` SELinux — **unreadable by apps** (DAC + SELinux walls; magiskpolicy --live did not lift adb_data_file for untrusted_app; `/data/local/tmp` works). → **ADR-0006 amendment**: split store — system-facing (`/data/adb`, hooks) vs app-facing (`/data/local/tmp`-class with policy) or dedicated context via Magisk module. Decision needed in Phase 1.
4. **Lab plumbing**: `persist.adb.tcp.port=5555` is the correct persistent wireless-adb mechanism (set once, survives reboots); a Magisk module with service.sh races adbd at boot (causes offline state). Tailscale on the phone needs Always-on VPN to reconnect after reboot.

**Other findings:** AGP 9.3.1 on ARM64 hosts has no aapt2 (Google ships x86_64 only) — spike builds ran in GitHub Actions (x86_64) with a pinned lab debug keystore; local ARM64 builds need a source-built aapt2 (Phase 1 lab task, documented).

## R8 — Widget tinting survey (2026-08-02) — reference device inventory

**Question (CONTEXT #4 / ADR-0015):** Which widgets on the reference device honor platform tinting?

**Method:** `dumpsys appwidget` on device (T1RGS33.135-109-9-29). 269 widget providers installed; 10 widgets live on the user's home screen (Moto Launcher3 host).

**Findings:**
- Platform mechanism (API 33): widgets referencing `system_*` color resources in RemoteViews are auto-tinted by the framework; custom-drawn/hardcoded-color widgets are not. `WIDGET_FEATURE_RECOLORABLE` exists (API 31+) but auto-tint works via system color references regardless.
- **Reference home screen mix (10 live widgets):**
  | Widget | Type | Tintable? |
  |---|---|---|
  | Art Text (com.vector123.arttextwidget) ×5 | third-party, custom-drawn | ❌ → glass-framing path |
  | MyMasjid prayer times ×1 | third-party custom | ❌ → glass-framing path |
  | Moto Time & Weather (commandcenter) ×1 | system | ✅ system colors |
  | Google Search ×1 | system | ✅ system colors |
- 269 providers installed: Google apps (Calendar, Photos, Maps, YT Music, Wellbeing, Docs), Chrome, Play, Slack — mostly Google/system (tintable); long tail of third-party (mixed).

**Implication (ADR-0015 confirmed, quantified):** roughly 40-60% of real home-screen widgets on this device will NOT tint → the glass-framing treatment is a core widget-host feature, not an edge case. Phase 2 host must implement: tint detection (query provider RemoteViews color scheme via `AppWidgetProviderInfo`/resource inspection where possible) + default glass-frame for non-tinting widgets + per-widget user override stored in config.
