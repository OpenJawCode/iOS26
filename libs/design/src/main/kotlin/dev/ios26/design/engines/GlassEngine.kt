package dev.ios26.design.engines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.ios26.design.theme.GlassIntensity
import dev.ios26.design.theme.LocalGlassIntensity
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

/**
 * GLASS ENGINE v2 (ADR-0027/0028) — the compositing pipeline every surface uses.
 * Layers, back to front: shadow → material (blur + vibrancy + adaptive tint) →
 * content → lighting (specular + rim). One blur per surface; budgets binding (ADR-0030).
 */

/** Adaptive tint color: base glass fill biased toward the backdrop accent (token fraction). */
@Composable
fun adaptiveTint(): Color {
    val colors = LocalTokenSet.current
    val bias = Tokens.Material.tintBias
    return androidx.compose.ui.graphics.lerp(colors.glassFill, colors.accent, bias)
}

/** Base material layer: fill (adaptive tint) + vibrancy pass (saturation/luminance lift). */
@Composable
fun Modifier.glassMaterial(
    shape: Shape = RoundedCornerShape(Tokens.Radius.card),
    intensity: GlassIntensity = LocalGlassIntensity.current,
): Modifier = this
    .clip(shape)
    .background(adaptiveTint())
    .drawWithContent {
        drawContent()
        // Vibrancy: saturation lift (research: 140-160%) + luminance lift (~5%).
        drawRect(
            color = Color.White,
            blendMode = BlendMode.Saturation,
            alpha = Tokens.Material.Vibrancy.saturation - 1f,
        )
        drawRect(
            color = Color.White,
            blendMode = BlendMode.Plus,
            alpha = Tokens.Material.Vibrancy.luminanceLift,
        )
    }

/** Lighting layer: Fresnel-style specular sheen (two opposite gradients) + cut-glass rim. */
@Composable
fun Modifier.glassLighting(
    shape: Shape = RoundedCornerShape(Tokens.Radius.card),
    intensity: GlassIntensity = LocalGlassIntensity.current,
): Modifier = this.drawWithCache {
    val w = size.width
    val h = size.height
    val alpha = Tokens.Material.Specular.startAlpha
    val sheen = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = alpha), Color.White.copy(alpha = 0f)),
        start = Offset(0f, 0f),
        end = Offset(w, h * Tokens.Material.Specular.heightFraction),
    )
    val counter = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = alpha * 0.6f), Color.White.copy(alpha = 0f)),
        start = Offset(w, h),
        end = Offset(w * 0.4f, h * 0.6f),
    )
    onDrawWithContent {
        drawContent()
        drawRect(brush = sheen)
        drawRect(brush = counter)
    }
}.border(width = 1.dp, color = LocalTokenSet.current.glassStroke, shape = shape)

/** Depth layer: adaptive shadow (alpha scaled by content luminance approximation). */
@Composable
fun Modifier.adaptiveShadow(
    level: String = "low",
    contentLuma: Float? = null,
    shape: Shape = RoundedCornerShape(Tokens.Radius.card),
): Modifier {
    val spec = ShadowEngine.elevationSpec(level)
    val factor = contentLuma?.let { 1f + (0.5f - it) } ?: 1f
    val alpha = (spec.alpha * factor).coerceIn(0.05f, 0.5f)
    val color = LocalTokenSet.current.glassShadow.copy(alpha = alpha)
    return this.shadow(
        elevation = spec.y,
        shape = shape,
        ambientColor = color,
        spotColor = color,
    )
}

/** Full pipeline: the ONLY way surfaces get material (ADR-0031). */
@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Tokens.Radius.card),
    intensity: GlassIntensity = LocalGlassIntensity.current,
    shadowLevel: String = "low",
    backdropBlur: Boolean = false,
    content: @Composable () -> Unit,
) {
    val radius = blurRadiusFor(intensity)
    Box(
        modifier
            .adaptiveShadow(shadowLevel, shape = shape)
            .then(if (backdropBlur) Modifier.blur(radius = radius.dp) else Modifier)
            .glassMaterial(shape, intensity)
            .glassLighting(shape, intensity),
    ) {
        content()
    }
}

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

/** In-window backdrop blur (budget: maxRenderEffect token). */
@Composable
fun Modifier.blurBackdrop(radius: Int = LocalGlassIntensity.current.let { blurRadiusFor(it) }): Modifier {
    val capped = radius.coerceAtMost(Tokens.Blur.maxRenderEffect)
    return this.blur(radius = capped.dp)
}

/** Window-level blur helper for hosts (CC overlay, sheets) — API 31+. */
fun android.app.Activity.applyGlassBlur(radius: Int) {
    if (android.os.Build.VERSION.SDK_INT >= 31) {
        window.setBackgroundBlurRadius(radius)
    }
}

/** Blur radius for a glass intensity (token-driven). */
fun blurRadiusFor(intensity: GlassIntensity): Int = when (intensity) {
    GlassIntensity.Subtle -> Tokens.Blur.subtle
    GlassIntensity.Standard -> Tokens.Blur.standard
    GlassIntensity.Prominent -> Tokens.Blur.prominent
}
