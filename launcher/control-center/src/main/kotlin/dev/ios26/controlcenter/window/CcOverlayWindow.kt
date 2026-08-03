package dev.ios26.controlcenter.window

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import dev.ios26.design.tokens.Tokens

/**
 * The CC overlay window (ADR-0036): ONE window, window-level blur-behind as the only real
 * blur (ADR-0030 budget), drawn over the status bar (FLAG_LAYOUT_IN_SCREEN), focusable
 * while open (toggles + tap-outside-to-close).
 *
 * The ComposeView hosts no Activity, so the lifecycle/saved-state owners Compose requires
 * are attached by [OverlayOwners] (Java — see its doc for why).
 */
class CcOverlayWindow(context: Context) {
    private val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val view = ComposeView(context.applicationContext).apply {
        attachOwners(this)
    }
    private var attached = false

    fun show(content: @Composable () -> Unit) {
        if (attached) return
        view.setContent {
            // The overlay hosts no Activity — the theme is provided here explicitly
            // (tokens, mode, glass intensity; CC = prominent glass per research).
            dev.ios26.design.theme.Ios26Theme {
                androidx.compose.runtime.CompositionLocalProvider(
                    dev.ios26.design.theme.LocalGlassIntensity provides dev.ios26.design.theme.GlassIntensity.Prominent,
                ) {
                    content()
                }
            }
        }
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // Blur-behind removed: untrusted blur windows don't present on this Moto
            // firmware (device finding). Backdrop blur = RenderEffect in the surface
            // (single blur budget, ADR-0030) once rendering is verified.
        }
        runCatching { wm.addView(view, lp) }
            .onFailure { CcLog.tag("overlay attach failed: $it") }
            .onSuccess { attached = true }
    }

    fun hide() {
        if (!attached) return
        runCatching { wm.removeView(view) }
            .onFailure { CcLog.tag("overlay detach failed: $it") }
        attached = false
    }

    val isAttached: Boolean get() = attached

    /**
     * Delegates to the Java helper [OverlayOwners] (lifecycle + saved-state owners for the
     * standalone ComposeView). AGP 9's built-in Kotlin compiles Kotlin before Java, so the
     * Java class cannot be referenced from Kotlin source — invoked reflectively instead.
     * The helper itself is plain public API against androidx (see its doc).
     */
    private fun attachOwners(view: android.view.View) {
        try {
            Class.forName(OVERLAY_OWNERS)
                .getMethod(METHOD_ATTACH, android.view.View::class.java)
                .invoke(null, view)
        } catch (t: Throwable) {
            CcLog.tag("owner attach failed: $t")
        }
    }

    private object CcLog {
        fun tag(msg: String) = android.util.Log.i("IOS26_CC", msg)
    }

    private companion object {
        const val OVERLAY_OWNERS = "dev.ios26.controlcenter.window.OverlayOwners"
        const val METHOD_ATTACH = "attach"
    }
}
