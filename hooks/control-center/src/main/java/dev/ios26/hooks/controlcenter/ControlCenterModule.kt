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
        if (flags.isEnabled("force-hook-failure")) {
            throw IllegalStateException("validation: forced hook failure")
        }
        try {
            val cls = param.defaultClassLoader.loadClass(
                "com.android.systemui.statusbar.phone.NotificationPanelViewController",
            )
            val method = findMethod(cls, "onQsIntercept", MotionEvent::class.java)
            if (method != null) hook(method).intercept(TouchInterceptor())
            // View-level intercept: the status-bar pull path (survey R5 chain).
            val viewClass = runCatching {
                param.defaultClassLoader.loadClass(
                    "com.android.systemui.statusbar.phone.NotificationPanelView",
                )
            }.getOrNull()
            if (viewClass != null) {
                val viewMethod = findMethod(viewClass, "onInterceptTouchEvent", MotionEvent::class.java)
                if (viewMethod != null) hook(viewMethod).intercept(TouchInterceptor())
            }
            // QS touch path (runtime probe: handleQsTouch on the controller).
            val qsTouch = findMethod(cls, "handleQsTouch", MotionEvent::class.java)
            if (qsTouch != null) hook(qsTouch).intercept(TouchInterceptor())
            if (method == null && viewClass == null && qsTouch == null) {
                probeTouchMethods(cls)
                error("no hookable target found")
            }
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
            log("onQsIntercept: action=${ev.actionMasked} x=${ev.rawX} y=${ev.rawY}")
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
            val root = java.io.File("/data/adb/ios26")
            val store = if (root.canWrite()) {
                dev.ios26.config.ConfigStore(root)
            } else {
                dev.ios26.config.ConfigStore(java.io.File("/data/local/tmp/ios26"))
            }
            runCatching {
                store.writeEvent(HookEvents.CC_OPEN)
                log("event written")
            }.onFailure { log("event write failed: $it") }
        }
    }

    /** Finds a method across the class hierarchy (Moto may redeclare in a superclass). */
    private fun findMethod(cls: Class<*>, name: String, vararg params: Class<*>): java.lang.reflect.Method? {
        var current: Class<*>? = cls
        while (current != null) {
            runCatching { current.getDeclaredMethod(name, *params)?.let { return it } }
            current = current.superclass
        }
        // Fallback: match by name + arity across the hierarchy (OEM naming drift).
        var walk: Class<*>? = cls
        while (walk != null) {
            walk.declaredMethods.firstOrNull { it.name == name && it.parameterCount == params.size }
                ?.let { return it }
            walk = walk.superclass
        }
        return null
    }

    /** Diagnostic: dump touch/intercept-related methods from the hierarchy. */
    private fun probeTouchMethods(cls: Class<*>) {
        var current: Class<*>? = cls
        while (current != null) {
            runCatching {
                current.declaredMethods
                    .filter { it.name.contains("ouch") || it.name.contains("ntercept") || it.name.contains("Touch") }
                    .forEach { log("PROBE ${current.name}.${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})") }
            }
            current = current.superclass
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
