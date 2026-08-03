---
name: project-systemui-hook-injection
description: "PROJECT-SPECIFIC (iOS26). SystemUI hooking on the Motorola Edge 20 via LSPosed (JingMatrix fork) using the modern libxposed API. Use for any hooks work: module entry, targeting, all-or-nothing rollback, Moto method drift, daemon registration. Distilled from the validated 3.1 chain — do not use the fork's legacy Xposed API (broken, AbstractMethodError)."
license: GPL-3.0
project: iOS26
source: project-specific (docs/phase3/validation-report.md, ADR-0032/0033/0038)
---

# SystemUI Hook Injection (validated on Motorola Edge 20)

## Non-negotiables (each one cost a debugging session)

1. **Modern libxposed API only.** Entry: `META-INF/xposed/java_init.list` + `module.prop`
   with `targetApiVersion=101`; entry class `io.github.libxposed.api.XposedModule` overriding
   `onPackageLoaded(PackageLoadedParam)` — use `getPackageName()` (NOT legacy
   `getCurrentProcessName`) and `getDefaultClassLoader()`. Hooks via
   `hook(method).intercept(Hooker)` with `chain.args` / `chain.proceed()`.
2. **Never package the Xposed API classes into the APK** — the framework rejects the module
   ("API classes compiled into the module's APK"). Split the API into a `libxposed-api`
   module used as `compileOnly` (repo pattern: `hooks/libxposed-api`).
3. **All-or-nothing**: every hook inside one guarded block; ANY failure → disable ALL hooks
   for the process + log. Forced-failure flag (`force-hook-failure.flag`) exercises this.
4. **Feature flags default OFF** (`control-center.flag`); missing flag = system unchanged.
5. **Process targeting**: `if (param.packageName != "com.android.systemui") return`.
6. **Moto method drift**: hook targets must be found by hierarchy walk + name/arity fallback
   (survey said `onInterceptTouchEvent`; the runtime name was `onQsIntercept(MotionEvent)`).
   When nothing matches, probe `declaredMethods` and log candidates.
7. **Registration**: daemon DB = `/data/adb/lspd/config/modules_config.db`. Manual insert
   works but registration persists only when the daemon's own scan sees the module — clean
   restart: `kill lspd` → copy/insert/checkpoint → push back →
   `setsid /data/adb/modules/zygisk_lsposed/daemon &`.

## Event seam (ADR-0034/0035)

Hooks write typed events only: `ConfigStore(root).writeEvent(type)` → `shared/events/$type.json`
via atomic tmp+rename. Hosts poll (`PollWatcher` 200ms — FileObserver unreliable for
untrusted_app on this firmware). Consume = read + delete. SELinux: `platform_app` write needs
`magiskpolicy --live 'allow platform_app shell_data_file file { read write open create rename unlink }'`
(+ dir perms) while the production sepolicy is pending (Phase 4).

## Rollback contract (3.1-verified, byte-identical for every surface)

Flag off → module disable (manager) → forced failure: all three = fully stock SystemUI, 0 hook logs.

## References

- `hooks/control-center/src/main/java/.../ControlCenterModule.kt` (working reference)
- ADR-0032/0033/0038; docs/phase3/validation-report.md
- Vendored: LSPosed upstream docs are at github.com/LSPosed/LSPosed (GPL-3.0, reference only)
