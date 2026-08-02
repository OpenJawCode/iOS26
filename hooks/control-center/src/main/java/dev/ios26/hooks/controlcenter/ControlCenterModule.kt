package dev.ios26.hooks.controlcenter

import android.view.MotionEvent
import dev.ios26.hooks.api.FeatureFlags
import dev.ios26.hooks.api.HookEvents
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * Control Center surface hook — modern libxposed API (ADR-0032).
 *
 * Safety contract (ADR-0033): all hooks apply inside one guarded block; ANY failure disables
 * the whole surface for this process (all-or-nothing) and logs. Feature flags default OFF —
 * a missing flag file means the system is completely unchanged (3.1 acceptance).
 */
class ControlCenterModule : XposedModule() {

    private val flags = FeatureFlags("/data/adb/ios26/shared/flags")
    private var hooksActive = false

    override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        // Process targeting: SystemUI only (hook target from survey R5).
        if (param.packageName != "com.android.systemui") return
        if (!flags.isEnabled("control-center")) return

        @Suppress("TooGenericExceptionCaught") // ADR-0033: all-or-nothing crash protection
        try {
            val cls = param.defaultClassLoader.loadClass(
                "com.android.systemui.statusbar.phone.NotificationPanelViewController",
            )
            val method = cls.getDeclaredMethod("onInterceptTouchEvent", MotionEvent::class.java)
            hook(method).intercept(TouchInterceptor())
            hooksActive = true
            log("hooks active")
        } catch (t: Throwable) {
            // All-or-nothing rollback: never leave the system partially hooked.
            hooksActive = false
            log("hook application FAILED — surface disabled: $t")
        }
    }

    /** Consumes top-right swipes (region per survey R5) and emits a CC-open event (ADR-0034). */
    inner class TouchInterceptor : XposedInterface.Hooker {
        @Suppress("ReturnCount") // guard clauses are the contract
        override fun intercept(chain: XposedInterface.Chain): Any? {
            val ev = chain.args.firstOrNull() as? MotionEvent ?: return chain.proceed()
            if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
                val x = ev.rawX
                val y = ev.rawY
                val w = android.content.res.Resources.getSystem().displayMetrics.widthPixels
                if (x > w * RATIO_X && y < MAX_Y) {
                    writeEvent()
                    return true // consume: shade never tracks it
                }
            }
            return chain.proceed()
        }

        private fun writeEvent() {
            runCatching {
                dev.ios26.config.ConfigStore(java.io.File("/data/adb/ios26")).writeEvent(HookEvents.CC_OPEN)
            }.onFailure { log("event write failed: $it") }
        }
    }

    private fun log(msg: String) {
        android.util.Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "IOS26_CC_HOOK"
        private const val RATIO_X = 0.66f
        private const val MAX_Y = 400f
    }
}
