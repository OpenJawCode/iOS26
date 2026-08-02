package dev.ios26.controlcenter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
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
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

/** Media card — active session metadata + transport (ADR-0037: no artwork capture). */
@Composable
internal fun MediaCard(state: CcUiState) {
    val colors = LocalTokenSet.current
    val media = state.media
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(Tokens.ControlCenter.tileRadius)
    Column(
        Modifier
            .fillMaxWidth()
            .glassMaterial(shape)
            .glassLighting(shape)
            .adaptiveShadow(level = "low", shape = shape)
            .padding(Tokens.Spacing.l),
        verticalArrangement = Arrangement.spacedBy(Tokens.Spacing.sm),
    ) {
        BasicText(
            "Now Playing",
            style = TextStyle(color = colors.labelSecondary, fontSize = Tokens.Type.Caption2.size),
        )
        if (media.title == null) {
            BasicText(
                "No media playing",
                style = TextStyle(color = colors.labelSecondary, fontSize = Tokens.Type.Body.size),
            )
        } else {
            BasicText(
                media.title.orEmpty(),
                style = TextStyle(color = colors.labelPrimary, fontSize = Tokens.Type.Body.size, fontWeight = FontWeight(600)),
            )
            if (!media.artist.isNullOrEmpty()) {
                BasicText(
                    media.artist.orEmpty(),
                    style = TextStyle(color = colors.labelSecondary, fontSize = Tokens.Type.Caption1.size),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Tokens.Spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaButton("Previous", enabled = media.title != null) { media.previous() }
            MediaButton(if (media.isPlaying) "Pause" else "Play", primary = true, enabled = media.title != null) { media.togglePlay() }
            MediaButton("Next", enabled = media.title != null) { media.next() }
        }
    }
}

/** Focus card — Android DND wrapped (ADR-0037), presented as a focus-style toggle. */
@Composable
internal fun FocusCard(state: CcUiState) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val focus = state.focus
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(Tokens.ControlCenter.tileRadius)
    Row(
        Modifier
            .fillMaxWidth()
            .glassMaterial(shape)
            .glassLighting(shape)
            .adaptiveShadow(level = "low", shape = shape)
            .semantics {
                role = Role.Switch
                contentDescription = "Focus"
                stateDescription = if (focus.isOn) "On" else "Off"
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                    focus.toggle(context)
                },
            )
            .padding(horizontal = Tokens.Spacing.l, vertical = Tokens.Spacing.m),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            BasicText("Focus", style = TextStyle(color = colors.labelPrimary, fontSize = Tokens.Type.Body.size, fontWeight = FontWeight(600)))
            BasicText(
                if (focus.isOn) "Do Not Disturb — On" else "Do Not Disturb — Off",
                style = TextStyle(color = colors.labelSecondary, fontSize = Tokens.Type.Caption2.size),
            )
        }
        val trackShape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        Row(
            Modifier
                .size(width = 51.dp, height = 31.dp)
                .clip(trackShape)
                .glassMaterial(trackShape)
                .padding(2.dp),
            horizontalArrangement = if (focus.isOn) Arrangement.End else Arrangement.Start,
        ) {
            Box(
                Modifier
                    .size(27.dp)
                    .clip(CircleShape)
                    .glassMaterial(CircleShape),
            )
        }
    }
}
