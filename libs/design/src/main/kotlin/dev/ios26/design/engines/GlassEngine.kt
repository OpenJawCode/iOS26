package dev.ios26.design.engines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.runtime.Immutable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ios26.design.theme.GlassIntensity
import dev.ios26.design.theme.LocalGlassIntensity
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

/**
 * GLASS ENGINE (ADR-0023) — the Liquid Glass material language, token-driven.
 *
 * Capability matrix on API 33 (verified Phase 0/2):
 * - BEHIND-WINDOW blur: `Window.setBackgroundBlurRadius` / `LayoutParams.blurBehindRadius`
 *   (API 31+) — host-owned, NOT a composable (see GlassWindow helper).
 * - IN-WINDOW backdrop blur: `android.graphics.RenderEffect.createBlurEffect` on the layer behind content.
 * - Scrim+fill compositing: always available, cheap, the fallback for any unsupported path.
 *
 * Budget rule (PERFORMANCE.md): one blur layer per surface; radius capped by tokens
 * (Blur.maxWindow / maxRenderEffect); never stack blur.
 */
object GlassEngine {

    /** Blur radius for a glass intensity (token-driven). */
    fun blurRadiusFor(intensity: GlassIntensity): Int = when (intensity) {
        GlassIntensity.Subtle -> Tokens.Blur.subtle
        GlassIntensity.Standard -> Tokens.Blur.standard
        GlassIntensity.Prominent -> Tokens.Blur.prominent
    }

    /** Tint strength for a glass intensity (fill alpha pair, token-driven). */
    fun tintFor(intensity: GlassIntensity): Color = when (intensity) {
        GlassIntensity.Subtle -> Tokens.Semantic.Light.glassFillSecondary
        GlassIntensity.Standard -> Tokens.Semantic.Light.glassFill
        GlassIntensity.Prominent -> Tokens.Semantic.Dark.glassFillSecondary
    }

    /** True when behind-window blur is available on this API level. */
    fun supportsWindowBlur(apiLevel: Int): Boolean = apiLevel >= 31
}

/** Composable glass surface: fill + stroke + highlight, rounded per token. */
@Composable
fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(Tokens.Radius.card),
    intensity: GlassIntensity = LocalGlassIntensity.current,
): Modifier {
    val colors = LocalTokenSet.current
    return this
        .clip(shape)
        .background(colors.glassFill)
        .border(width = 1.dp, color = colors.glassStroke, shape = shape)
}

/** In-window backdrop blur (budget: maxRenderEffect token; compose blur handles RenderEffect). */
@Composable
fun Modifier.blurBackdrop(radius: Int = LocalGlassIntensity.current.let { GlassEngine.blurRadiusFor(it) }): Modifier {
    val capped = radius.coerceAtMost(Tokens.Blur.maxRenderEffect)
    return this.blur(radius = capped.dp)
}

/** Vibrancy: tint compositing over the glass (token-driven tint color). */
@Composable
fun Modifier.vibrancy(tint: Color? = null): Modifier {
    val color = tint ?: LocalTokenSet.current.glassHighlight
    return this.background(color.copy(alpha = color.luminance() * 0.5f))
}

/** Window-level blur helper for hosts (CC overlay, sheets) — API 31+. */
fun android.app.Activity.applyGlassBlur(radius: Int) {
    if (GlassEngine.supportsWindowBlur(android.os.Build.VERSION.SDK_INT)) {
        window.setBackgroundBlurRadius(radius)
    }
}

/** Elevation → shadow mapping (token-driven; 120Hz-cheap — single shadow pass). */
@Immutable
data class ShadowSpec(val y: Dp, val radius: Dp, val alpha: Float)

object ShadowEngine {
    fun elevationSpec(elevation: String): ShadowSpec = when (elevation) {
        "hairline" -> ShadowSpec(Tokens.Elevation.Hairline.y.dp, Tokens.Elevation.Hairline.radius.dp, Tokens.Elevation.Hairline.alpha)
        "low" -> ShadowSpec(Tokens.Elevation.Low.y.dp, Tokens.Elevation.Low.radius.dp, Tokens.Elevation.Low.alpha)
        "medium" -> ShadowSpec(Tokens.Elevation.Medium.y.dp, Tokens.Elevation.Medium.radius.dp, Tokens.Elevation.Medium.alpha)
        "high" -> ShadowSpec(Tokens.Elevation.High.y.dp, Tokens.Elevation.High.radius.dp, Tokens.Elevation.High.alpha)
        "floating" -> ShadowSpec(Tokens.Elevation.Floating.y.dp, Tokens.Elevation.Floating.radius.dp, Tokens.Elevation.Floating.alpha)
        else -> ShadowSpec(0.dp, 0.dp, 0f)
    }
}

@Composable
fun Modifier.elevation(level: String): Modifier {
    val spec = ShadowEngine.elevationSpec(level)
    val shadowColor = LocalTokenSet.current.glassShadow.copy(alpha = spec.alpha)
    return this.shadow(
        elevation = spec.y,
        shape = RoundedCornerShape(Tokens.Radius.card),
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )
}
