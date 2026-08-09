package dev.ios26.controlcenter.ui

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.ios26.controlcenter.state.CcUiState
import dev.ios26.design.engines.MotionEngine
import dev.ios26.design.engines.adaptiveShadow
import dev.ios26.design.engines.glassLighting
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens
import androidx.compose.ui.input.pointer.PointerEventPass

/**
 * The glass Control Center surface (ADR-0036): one continuous glass field, entrance via
 * event-triggered spring, interactive velocity-aware close drag in-process, haptics on
 * settle, tap-outside to dismiss, reduced-motion cross-fade.
 */
@Composable
fun ControlCenterSurface(
    state: CcUiState,
    onDismiss: () -> Unit,
) {
    val colors = LocalTokenSet.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val screenHeight = with(density) { LocalConfiguration.current.screenHeightDp.dp.toPx() }
    val reducedMotion = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    ) == 0f

    val offsetY = remember { Animatable(0f) }
    val animAlpha = remember { Animatable(1f) }
    val gestureScope = rememberCoroutineScope()
    var panelHeightPx by remember { mutableStateOf(1f) }

    // Entrance: spring from off-screen (interruptible; reduced motion → cross-fade).
    LaunchedEffect(Unit) {
        if (reducedMotion) {
            animAlpha.snapTo(0f)
            animAlpha.animateTo(1f, MotionEngine.tween("fast", "standard", reducedMotion = true))
        } else {
            offsetY.snapTo(-screenHeight)
            offsetY.animateTo(
                targetValue = 0f,
                animationSpec = MotionEngine.spring("standard"),
            )
            HapticEngineSettle.perform(context)
        }
    }

    Box(Modifier.fillMaxSize()) {
        // Scrim — tap anywhere outside the panel dismisses (research: tap-outside close).
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.25f * animAlpha.value))
                .alpha(animAlpha.value)
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        gestureScope.launch { startDismiss(offsetY, animAlpha, screenHeight, onDismiss, reducedMotion) }
                    },
                ),
        )

        // Panel — top-right anchored, continuous glass field.
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .fillMaxHeight()
                .width(with(density) { (LocalConfiguration.current.screenWidthDp.dp * Tokens.ControlCenter.panelWidthFraction) })
                .padding(horizontal = Tokens.ControlCenter.margin)
                .graphicsLayer {
                    translationY = offsetY.value
                    alpha = animAlpha.value
                }
                .onSizeChanged { panelHeightPx = it.height.toFloat() }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = true)
                        if (down == null) return@awaitEachGesture
                        val tracker = VelocityTracker()
                        var dragging = false
                        var dismissByVelocity = false
                        var flingVelocity = 0f
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Main)
                            val change = event.changes.firstOrNull { it.id == down.id } ?: break
                            tracker.addPosition(change.uptimeMillis, change.position)
                            val dy = change.positionChange().y
                            if (change.pressed) {
                                dragging = true
                                val current = offsetY.value
                                gestureScope.launch { offsetY.snapTo((current + dy).coerceAtLeast(0f)) }
                                change.consume()
                            } else {
                                if (dragging) {
                                    flingVelocity = tracker.calculateVelocity().y
                                    dismissByVelocity = flingVelocity > Tokens.ControlCenter.dismissVelocity ||
                                        offsetY.value / panelHeightPx.coerceAtLeast(1f) > Tokens.ControlCenter.dismissFraction
                                }
                                break
                            }
                        }
                        if (dragging) {
                            if (dismissByVelocity) {
                                gestureScope.launch {
                                    dismissWithVelocity(offsetY, screenHeight, flingVelocity, onDismiss, reducedMotion)
                                }
                            } else {
                                gestureScope.launch {
                                    offsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = MotionEngine.spring("standard"),
                                    )
                                }
                            }
                        }
                    }
                },
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(top = Tokens.Spacing.xl)
                    .glassMaterial(RoundedCornerShape(Tokens.ControlCenter.panelRadius))
                    .glassLighting(RoundedCornerShape(Tokens.ControlCenter.panelRadius))
                    .adaptiveShadow(level = "high", shape = RoundedCornerShape(Tokens.ControlCenter.panelRadius))
                    .padding(Tokens.ControlCenter.margin),
                verticalArrangement = Arrangement.spacedBy(Tokens.ControlCenter.clusterGap),
            ) {
                ConnectivityCluster(state)
                CcSliderCard("Brightness", state.brightness, onValueChange = { state.updateBrightness(it) })
                CcSliderCard("Media volume", state.volume, onValueChange = { state.updateVolume(it) })
                MediaCard(state)
                FocusCard(state)
                QuickActions(state)
                Spacer(Modifier.height(Tokens.Spacing.xs))
            }
        }
    }
}

/** Quick actions row — flashlight / rotation / hotspot (wraps capabilities, ADR-0037). */
@Composable
internal fun QuickActions(state: CcUiState) {
    val context = LocalContext.current
    Row(horizontalArrangement = Arrangement.spacedBy(Tokens.ControlCenter.clusterGap)) {
        CcTile("Flashlight", state.flashlight.isOn) { state.flashlight.toggle(context) }
        CcTile("Rotate", state.rotation.isOn) { state.rotation.toggle(context) }
        CcTile("Hotspot", state.hotspot.isOn) { state.hotspot.toggle(context) }
    }
}

private suspend fun startDismiss(
    offsetY: Animatable<Float, *>,
    animAlpha: Animatable<Float, *>,
    screenHeight: Float,
    onDismiss: () -> Unit,
    reducedMotion: Boolean,
) {
    if (reducedMotion) {
        animAlpha.animateTo(0f, MotionEngine.tween("fast", "standard", reducedMotion = true))
    } else {
        offsetY.animateTo(screenHeight, MotionEngine.spring("standard"))
    }
    onDismiss()
}

private suspend fun dismissWithVelocity(
    offsetY: Animatable<Float, *>,
    screenHeight: Float,
    velocity: Float,
    onDismiss: () -> Unit,
    reducedMotion: Boolean,
) {
    if (reducedMotion) {
        offsetY.animateTo(screenHeight, MotionEngine.tween("fast", "standard", reducedMotion = true))
    } else {
        // Velocity-aware trigger already passed; the exit rides a spring from the live
        // offset (interruptible, no forced duration — WWDC18 doctrine).
        offsetY.animateTo(screenHeight, MotionEngine.spring("standard"))
    }
    onDismiss()
}

private object HapticEngineSettle {
    fun perform(context: android.content.Context) {
        runCatching {
            dev.ios26.design.engines.HapticEngine.perform(context, Tokens.ControlCenter.hapticSettle)
        }
    }
}
