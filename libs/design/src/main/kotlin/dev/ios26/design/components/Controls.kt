package dev.ios26.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import dev.ios26.design.engines.HapticEngine
import dev.ios26.design.engines.MotionEngine
import dev.ios26.design.engines.pressScale
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.theme.ThemeMode
import dev.ios26.design.tokens.Tokens

/**
 * Interaction states — token-driven (state group): pressed overlay, disabled opacity,
 * focus ring. Press feedback responds on press (apple-design doctrine), animated with
 * the motion engine (springs: interruptible, velocity-aware).
 */

/** Shared pressed overlay color for a mode (state token). */
internal fun pressedOverlay(mode: ThemeMode): Color = when (mode) {
    ThemeMode.Light -> Tokens.State.pressedOverlay
    ThemeMode.Dark -> Tokens.State.pressedOverlayDark
}

/** Press feedback via MotionEngine v2 (research timings: 80ms down, spring up). */
@Composable
internal fun Modifier.pressFeedback(
    interactionSource: MutableInteractionSource,
    enabled: Boolean = true,
): Modifier = this.pressScale(interactionSource, enabled)

/** iOS-style button: accent fill, token type, press feedback, haptic on click. */
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    filled: Boolean = true,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(Tokens.Radius.control)
    val style = TextStyle(
        color = if (filled) Tokens.Color.Core.white else colors.accent,
        fontSize = Tokens.Type.Body.size,
        fontWeight = androidx.compose.ui.text.font.FontWeight(600),
    )
    Box(
        modifier
            .pressFeedback(interaction, enabled)
            .semantics { role = Role.Button }
            .clip(shape)
            .background(if (filled) colors.accent else Color.Transparent)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.medium)
                    onClick()
                },
            )
            .defaultMinSize(minHeight = Tokens.Spacing.touchTarget)
            .padding(horizontal = Tokens.Spacing.xl, vertical = Tokens.Spacing.sm),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(text, style = style)
    }
}

/** iOS-style switch. */
@Composable
fun Switch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val interaction = remember { MutableInteractionSource() }
    val trackColor by animateColorAsState(
        targetValue = if (checked) colors.accent else colors.backgroundTertiary,
        animationSpec = androidx.compose.animation.core.tween(
            MotionEngine.duration("standard"),
            easing = MotionEngine.curve("standard"),
        ),
        label = "switchTrack",
    )
    Box(
        modifier
            .size(width = 51.dp, height = 31.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(trackColor)
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                    onCheckedChange(!checked)
                },
            )
            .semantics { role = Role.Switch },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .padding(horizontal = 2.dp)
                .size(27.dp)
                .clip(CircleShape)
                .background(Tokens.Color.Core.white)
                .graphicsLayerOffset(if (checked) 20.dp else 0.dp)
        )
    }
}

private fun Modifier.graphicsLayerOffset(x: androidx.compose.ui.unit.Dp): Modifier =
    this.graphicsLayer { translationX = x.toPx() }

/** iOS-style slider. */
@Composable
fun Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    Box(
        modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = Tokens.Spacing.touchTarget)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.backgroundTertiary)
        )
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(colors.accent)
        )
    }
}

/** iOS-style toggle (checkmark/plus row item). */
@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val mark = if (checked) "✓" else "+"
    Box(
        modifier
            .size(Tokens.Spacing.xxl)
            .clip(RoundedCornerShape(Tokens.Radius.small))
            .background(if (checked) colors.accent else colors.backgroundTertiary)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                    onCheckedChange(!checked)
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(mark, style = TextStyle(color = Tokens.Color.Core.white, fontSize = Tokens.Type.Body.size))
    }
}
