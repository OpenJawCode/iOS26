package dev.ios26.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ios26.design.components.Button
import dev.ios26.design.components.ListItem
import dev.ios26.design.components.Sheet
import dev.ios26.design.components.Switch
import dev.ios26.design.components.Toggle
import dev.ios26.design.theme.GlassIntensity
import dev.ios26.design.theme.LocalGlassIntensity
import dev.ios26.design.tokens.Tokens

/** Settings surface (companion is Phase 5; this is the in-launcher dev surface). */
@Composable
fun SettingsSheet(onDismiss: () -> Unit) {
    val intensity = LocalGlassIntensity.current
    var reduced by remember { mutableStateOf(false) }

    Sheet(Modifier.fillMaxWidth()) {
        ListItem("Glass intensity", subtitle = intensity.name, trailing = { Toggle(false, {}) })
        ListItem("Reduced motion", subtitle = "cross-fade scheme", trailing = { Switch(reduced, { reduced = it }) })
        ListItem("Haptics", subtitle = "token-driven feedback")
        Spacer(Modifier.height(Tokens.Spacing.l))
        Button("Close", onClick = onDismiss)
    }
}
