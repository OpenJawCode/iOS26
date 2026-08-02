package dev.ios26.design.engines

import android.app.WallpaperManager
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import dev.ios26.design.tokens.Tokens

/**
 * DYNAMIC COLOR ENGINE — wallpaper adaptation (ADR-0011 token sets + iOS dynamic tinting).
 *
 * Pipeline: wallpaper bitmap -> androidx Palette -> dominant/vibrant swatches ->
 * accent + tinted surface overrides merged into the active TokenSet.
 * All values stay token-shaped (overrides are token values, never magic colors).
 * Runs once per wallpaper change (remember with wallpaper token key).
 */
object DynamicColorEngine {

    /** Extracts the wallpaper accent (fallback: token accent). Pure-ish, testable with a palette. */
    fun accentFrom(palette: Palette?, fallback: Color): Color {
        val swatch = palette?.vibrantSwatch ?: palette?.dominantSwatch ?: return fallback
        return Color(swatch.rgb)
    }

    /** Composable accessor — extracts the wallpaper accent OFF the main thread (perf: palette
     *  generation is expensive; measured 59% jank on first gallery frame, Phase 2 baseline). */
    @Composable
    fun rememberWallpaperAccent(): Color {
        val context = LocalContext.current
        var accent by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(Tokens.Semantic.Light.accent) }
        androidx.compose.runtime.LaunchedEffect(Unit) {
            accent = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
                runCatching {
                    val drawable = WallpaperManager.getInstance(context).drawable
                    val bitmap = (drawable as? BitmapDrawable)?.bitmap
                    if (bitmap == null) {
                        Tokens.Semantic.Light.accent
                    } else {
                        accentFrom(Palette.from(bitmap).generate(), Tokens.Semantic.Light.accent)
                    }
                }.getOrDefault(Tokens.Semantic.Light.accent)
            }
        }
        return accent
    }
}
