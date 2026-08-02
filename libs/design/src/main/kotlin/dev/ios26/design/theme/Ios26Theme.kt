package dev.ios26.design.theme

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/** Current semantic token set (mode-resolved). */
val LocalTokenSet = staticCompositionLocalOf { TokenSet.light() }

/** Current glass intensity (runtime-adjustable, R3). */
val LocalGlassIntensity = staticCompositionLocalOf { GlassIntensity.Standard }

/** Reduced motion — durations scale by the motion token multiplier (accessibility). */
val LocalReducedMotion = staticCompositionLocalOf { false }

@Immutable
data class ThemeState(
    val mode: ThemeMode,
    val glassIntensity: GlassIntensity,
    val reducedMotion: Boolean,
)

/**
 * ThemeEngine root — provides the token set + glass intensity + reduced motion to the
 * whole tree. Everything below reads only these locals; nothing hardcodes values.
 */
@Composable
fun Ios26Theme(
    mode: ThemeMode? = null,
    glassIntensity: GlassIntensity = GlassIntensity.Standard,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val resolvedMode = mode ?: if (isSystemInDarkTheme()) ThemeMode.Dark else ThemeMode.Light
    val reduced = remember { isReducedMotion(context) }

    CompositionLocalProvider(
        LocalTokenSet provides remember(resolvedMode) { tokenSetFor(resolvedMode) },
        LocalGlassIntensity provides glassIntensity,
        LocalReducedMotion provides reduced,
        content = content,
    )
}

fun tokenSetFor(mode: ThemeMode): TokenSet = when (mode) {
    ThemeMode.Light -> TokenSet.light()
    ThemeMode.Dark -> TokenSet.dark()
}

/** Reads the system animator scale (0 = animations off). */
fun isReducedMotion(context: Context): Boolean {
    val scale = runCatching {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    }.getOrDefault(1f)
    return scale == 0f
}
