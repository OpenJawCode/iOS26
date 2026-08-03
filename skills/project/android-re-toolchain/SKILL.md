---
name: project-android-re-toolchain
description: "PROJECT-SPECIFIC (iOS26). Android reverse-engineering workflows used in this project: JADX decompilation of Moto SystemUI/APKs, APKTool resource decoding, smali inspection, dex class dumps, RRO overlay basics. Use when hook-target surveys, icon/asset extraction checks, or firmware behavior questions require reading compiled Android artifacts. Tool references are the canonical upstream repos (Apache-2.0/GPL)."
license: GPL-3.0
project: iOS26
source: project-specific (docs/phase0/research-log.md R5, hook surveys)
---

# Android RE Toolchain

## Canonical tools (upstream, reference only)

| Tool | Upstream | License | Use |
|---|---|---|---|
| JADX | github.com/skylot/jadx | Apache-2.0 | dex → Java; Moto SystemUI method surveys |
| APKTool | github.com/iBotPeaches/Apktool | Apache-2.0 | resources decode/rebuild, RRO authoring |
| smali/baksmali | github.com/JesusFreke/smali | — | bytecode-level patches (avoid; last resort) |
| aapt2 | build-tools | Apache-2.0 | resource id queries (lab: qemu binfmt) |

## Survey pattern (proven in R5/3.1)

1. `adb pull` the target APK (e.g., `/system/priv-app/SystemUIGoogle/...apk` or framework jar).
2. JADX: find the class (e.g., `NotificationPanelViewController`), list declared methods,
   note parameter/return types — Moto renames methods (`onInterceptTouchEvent` →
   `onQsIntercept`), so survey ALL touch/intercept candidates.
3. Cross-check at runtime: the hook's probe fallback logs candidates from `declaredMethods`.
4. Prefer `dumpsys` + `uiautomator dump` (text) over screenshots when no vision tool is
   available; pixel-sample screenshots with PIL for color/contrast checks.

## RRO overlays (overlays/ dir in repo)

- Resource-only overlays: `overlayable` + `resource-path`; build with aapt2, install as
  `/data/adb/modules/...` or `/vendor/overlay`. Used for presentation-only changes; our
  runtime surfaces come from hooks (ADR-0032), not overlays — keep them for static bits.
