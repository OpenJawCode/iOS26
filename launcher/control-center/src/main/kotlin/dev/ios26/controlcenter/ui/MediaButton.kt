package dev.ios26.controlcenter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ios26.design.engines.HapticEngine
import dev.ios26.design.engines.glassLighting
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

/** Media transport button (play/pause/next/previous) — glass, haptics, semantics. */
@Composable
internal fun MediaButton(
    label: String,
    enabled: Boolean,
    primary: Boolean = false,
    onClick: () -> Unit,
) {
    val colors = LocalTokenSet.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val interaction = remember { MutableInteractionSource() }
    val shape = CircleShape
    Row(
        Modifier
            .size(if (primary) 56.dp else 44.dp)
            .clip(shape)
            .glassMaterial(shape)
            .glassLighting(shape)
            .semantics {
                role = Role.Button
                contentDescription = label
            }
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
                onClick = {
                    HapticEngine.perform(context, Tokens.Haptics.selection)
                    onClick()
                },
            ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            label,
            style = TextStyle(
                color = if (enabled) colors.labelPrimary else colors.labelSecondary,
                fontSize = if (primary) Tokens.Type.Body.size else Tokens.Type.Caption1.size,
                fontWeight = FontWeight(600),
            ),
        )
    }
}
