package dev.ios26.controlcenter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ios26.controlcenter.state.CcUiState
import dev.ios26.design.engines.HapticEngine
import dev.ios26.design.engines.adaptiveShadow
import dev.ios26.design.engines.glassLighting
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.components.pressFeedback
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

/** Glass tile — the ONLY tile building block (GlassEngine v2 + tokens, ADR-0031). */
@Composable
internal fun CcTile(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val interaction = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(Tokens.ControlCenter.tileRadius)
    Box(
        modifier
            .size(Tokens.ControlCenter.tileSize)
            .glassMaterial(shape)
            .glassLighting(shape)
            .adaptiveShadow(level = "low", contentLuma = if (active) 0.3f else null, shape = shape)
            .pressFeedback(interaction)
            .semantics {
                role = Role.Button
                contentDescription = label
                stateDescription = if (active) "On" else "Off"
            }
            .clip(shape)
            .background(if (active) colors.accent else Color.Transparent)
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                    onClick()
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            label,
            style = TextStyle(
                color = if (active) Tokens.Color.Core.white else colors.labelSecondary,
                fontSize = Tokens.Type.Caption1.size,
                fontWeight = FontWeight(600),
            ),
        )
    }
}

/** Connectivity cluster — 2×2 glass grid (research: radios first, iOS 17 structure). */
@Composable
internal fun ConnectivityCluster(state: CcUiState) {
    val context = androidx.compose.ui.platform.LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(Tokens.ControlCenter.clusterGap)) {
        Row(horizontalArrangement = Arrangement.spacedBy(Tokens.ControlCenter.clusterGap)) {
            CcTile("Wi-Fi", state.wifi.isOn) { state.wifi.toggle(context) }
            CcTile("Bluetooth", state.bluetooth.isOn) { state.bluetooth.toggle(context) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Tokens.ControlCenter.clusterGap)) {
            CcTile("Airplane", state.airplane.isOn) { state.airplane.toggle(context) }
            CcTile("Cellular", state.mobileData.isOn) { state.mobileData.toggle(context) }
        }
    }
}

/** Slider card — brightness/volume (tokens; drag is interruptible, haptic on change). */
@Composable
internal fun CcSliderCard(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    val colors = LocalTokenSet.current
    val shape = RoundedCornerShape(Tokens.ControlCenter.tileRadius)
    Column(
        Modifier
            .fillMaxWidth()
            .glassMaterial(shape)
            .glassLighting(shape)
            .adaptiveShadow(level = "low", shape = shape)
            .padding(horizontal = Tokens.Spacing.l, vertical = Tokens.Spacing.m),
        verticalArrangement = Arrangement.spacedBy(Tokens.Spacing.sm),
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BasicText(title, style = TextStyle(color = colors.labelSecondary, fontSize = Tokens.Type.Caption2.size))
            BasicText(
                "${(value * 100).toInt()}%",
                style = TextStyle(color = colors.labelPrimary, fontSize = Tokens.Type.Caption1.size, fontWeight = FontWeight(600)),
            )
        }
        CcSlider(value = value, onValueChange = onValueChange)
    }
}

/** Token-styled slider: track + fill + thumb, horizontal drag, touch-target height. */
@Composable
internal fun CcSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val trackShape = RoundedCornerShape(Tokens.Radius.pill)
    val fillFraction = value.coerceIn(0f, 1f)
    val currentOnChange by rememberUpdatedState(onValueChange)
    Box(
        Modifier
            .fillMaxWidth()
            .height(Tokens.ControlCenter.sliderHeight)
            .semantics {
                role = Role.Button
                contentDescription = "slider"
                stateDescription = "${(value * 100).toInt()} percent"
                progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f)
            }
            .pointerInput(Unit) {
                awaitSliderDrag { currentOnChange(it) }
            },
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(Modifier.fillMaxWidth().height(6.dp).clip(trackShape).background(colors.backgroundTertiary))
        Box(
            Modifier
                .fillMaxWidth(fillFraction)
                .height(6.dp)
                .clip(trackShape)
                .background(colors.accent),
        )
        Box(
            Modifier
                .fillMaxWidth(fillFraction),
            contentAlignment = Alignment.CenterEnd,
        ) {
            Box(
                Modifier
                    .size(16.dp)
                    .clip(trackShape)
                    .background(Color.White),
            )
        }
    }
}

/** Horizontal drag on the slider; starts from tap position, interrupts freely. */
private suspend fun PointerInputScope.awaitSliderDrag(onValueChange: (Float) -> Unit) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        val width = size.width.toFloat().coerceAtLeast(1f)
        val fraction = (down.position.x / width).coerceIn(0f, 1f)
        onValueChange(fraction)
        while (true) {
            val event = awaitPointerEvent()
            val change = event.changes.firstOrNull { it.id == down.id } ?: break
            onValueChange((change.position.x / width).coerceIn(0f, 1f))
            change.consume()
            if (!change.pressed) break
        }
    }
}
