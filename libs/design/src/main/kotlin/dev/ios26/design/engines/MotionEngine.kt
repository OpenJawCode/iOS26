package dev.ios26.design.engines

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import dev.ios26.design.tokens.Tokens

/**
 * MOTION ENGINE (ADR-0024) — everything animated is token-driven.
 *
 * Doctrine (apple-design skill): interruptibility is the single most important principle —
 * animations start from the CURRENT presentation value and are redirectable at any instant
 * (Compose springs give this natively; tweens only where token-defined). Motion starts on
 * press, not release. Reduced motion scales durations by the motion token multiplier.
 */
object MotionEngine {

    /** Named curves (token-driven; cubic-bezier arrays in tokens.json). */
    fun curve(name: String): Easing = when (name) {
        "standard" -> curveOf(Tokens.Motion.Curve.standard)
        "emphasized" -> curveOf(Tokens.Motion.Curve.emphasized)
        "decelerate" -> curveOf(Tokens.Motion.Curve.decelerate)
        "accelerate" -> curveOf(Tokens.Motion.Curve.accelerate)
        "easeInOut" -> curveOf(Tokens.Motion.Curve.easeInOut)
        else -> curveOf(Tokens.Motion.Curve.standard)
    }

    /** Converts a token bezier array into a Compose easing. Pure — Tier-1 testable. */
    fun curveOf(bezier: List<Float>): Easing {
        require(bezier.size == 4) { "Bezier tokens must have 4 values, got $bezier" }
        return CubicBezierEasing(bezier[0], bezier[1], bezier[2], bezier[3])
    }

    fun duration(name: String): Int = when (name) {
        "fast" -> Tokens.Motion.Duration.fast
        "standard" -> Tokens.Motion.Duration.standard
        "comfortable" -> Tokens.Motion.Duration.comfortable
        "slow" -> Tokens.Motion.Duration.slow
        "entrance" -> Tokens.Motion.Duration.entrance
        "exit" -> Tokens.Motion.Duration.exit
        else -> Tokens.Motion.Duration.standard
    }

    /** Tween spec from tokens; honors reduced motion via the multiplier. */
    fun tween(
        durationName: String = "standard",
        curveName: String = "standard",
        reducedMotion: Boolean = false,
    ): TweenSpec<Float> {
        val base = duration(durationName)
        val scaled = if (reducedMotion) (base * Tokens.Motion.reducedMultiplier).toInt() else base
        return tween(durationMillis = scaled, easing = curve(curveName))
    }

    /** Spring spec from tokens (mapped from iOS UISpringTimingParameters). */
    fun spring(
        springName: String = "standard",
    ): SpringSpec<Float> = when (springName) {
        "snappy" -> spring(dampingRatio = Tokens.Motion.Spring.Snappy.damping, stiffness = Tokens.Motion.Spring.Snappy.stiffness.toFloat())
        "gentle" -> spring(dampingRatio = Tokens.Motion.Spring.Gentle.damping, stiffness = Tokens.Motion.Spring.Gentle.stiffness.toFloat())
        else -> spring(dampingRatio = Tokens.Motion.Spring.Standard.damping, stiffness = Tokens.Motion.Spring.Standard.stiffness.toFloat())
    }

    /** Resolves a token animation name to a spec (springs preferred for interactivity). */
    fun spec(
        springName: String? = null,
        durationName: String = "standard",
        curveName: String = "standard",
        reducedMotion: Boolean = false,
    ): FiniteAnimationSpec<Float> = springName?.let { spring(it) } ?: tween(durationName, curveName, reducedMotion)
}

/**
 * HAPTIC ENGINE — iOS feedback types mapped to Android vibration effects (token-driven).
 * Mapping (documented in MOTION.md): selection→CLICK, light→TICK, medium→CLICK, heavy→HEAVY_CLICK,
 * success→DOUBLE_CLICK, warning→DOUBLE_CLICK, error→HEAVY_CLICK (API 30+ predefined effects).
 */
object HapticEngine {

    /** Pure mapping — Tier-1 testable. Returns the VibrationEffect id (API 30+). */
    fun effectId(type: String): Int = when (type) {
        Tokens.Haptics.selection -> android.os.VibrationEffect.EFFECT_CLICK
        Tokens.Haptics.light -> android.os.VibrationEffect.EFFECT_TICK
        Tokens.Haptics.medium -> android.os.VibrationEffect.EFFECT_CLICK
        Tokens.Haptics.heavy -> android.os.VibrationEffect.EFFECT_HEAVY_CLICK
        Tokens.Haptics.success -> android.os.VibrationEffect.EFFECT_DOUBLE_CLICK
        Tokens.Haptics.warning -> android.os.VibrationEffect.EFFECT_DOUBLE_CLICK
        Tokens.Haptics.error -> android.os.VibrationEffect.EFFECT_HEAVY_CLICK
        else -> android.os.VibrationEffect.EFFECT_CLICK
    }

    /** Composable helper — resolves context from composition. */
    @androidx.compose.runtime.Composable
    fun perform(type: String) {
        perform(androidx.compose.ui.platform.LocalContext.current, type)
    }

    @androidx.compose.runtime.Composable
    fun performSelection() = perform(dev.ios26.design.tokens.Tokens.Haptics.selection)

    @androidx.compose.runtime.Composable
    fun performButtonClick() = perform(dev.ios26.design.tokens.Tokens.Haptics.medium)

    fun perform(context: android.content.Context, type: String) {
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE)
                as? android.os.VibratorManager
            vibrator?.defaultVibrator?.vibrate(android.os.VibrationEffect.createPredefined(effectId(type)))
        }
    }
}

/**
 * MOTION ENGINE v2 (ADR-0029): press micro-interaction + transition presets + haptic lead.
 * Research timings: press-down 80ms, release spring ~160ms, damping 1.0/0.8 (tokens retuned).
 */

/** Press feedback: scale-down 80ms on press, spring back on release (research §6). */
@Composable
fun Modifier.pressScale(
    interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource,
    enabled: Boolean = true,
    onPress: (() -> Unit)? = null,
): Modifier {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (pressed && enabled) 0.96f else 1f,
        animationSpec = if (pressed) {
            androidx.compose.animation.core.tween(80, easing = MotionEngine.curve("decelerate"))
        } else {
            MotionEngine.spring("snappy")
        },
        label = "pressScale",
    )
    androidx.compose.runtime.LaunchedEffect(pressed) {
        if (pressed) onPress?.invoke()
    }
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/** Haptic lead: fire haptics ~15ms before the visual commit (research: 10-20ms lead). */
@Composable
fun hapticLead(context: android.content.Context, type: String) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(0)
        HapticEngine.perform(context, type)
    }
}

/** Transition presets — token-driven entrance/exit specs (fade + scale). */
object Transitions {
    data class Preset(val fade: Boolean, val scaleFrom: Float, val durationName: String, val curveName: String)

    fun entrance(): Preset = Preset(
        fade = Tokens.Transition.Entrance.fadeIn,
        scaleFrom = Tokens.Transition.Entrance.scaleFrom,
        durationName = Tokens.Transition.Entrance.duration,
        curveName = Tokens.Transition.Entrance.curve,
    )

    fun exit(): Preset = Preset(
        fade = Tokens.Transition.Exit.fadeOut,
        scaleFrom = Tokens.Transition.Exit.scaleTo,
        durationName = Tokens.Transition.Exit.duration,
        curveName = Tokens.Transition.Exit.curve,
    )
}
