package dev.ios26.launcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.ios26.design.components.Button
import dev.ios26.design.components.ControlCenterCard
import dev.ios26.design.components.QuickSettingsTile
import dev.ios26.design.components.Sheet
import dev.ios26.design.components.Slider
import dev.ios26.design.tokens.Tokens

/** Control Center surface (ADR-0005 host side) — glass sheet over the springboard. */
@Composable
fun ControlCenterSheet(onDismiss: () -> Unit) {
    Sheet(Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Spacing.m)) {
            QuickSettingsTile("Wi-Fi", active = true, onClick = {})
            QuickSettingsTile("Bluetooth", active = true, onClick = {})
            QuickSettingsTile("Aeroplane", active = false, onClick = {})
            QuickSettingsTile("Mobile data", active = true, onClick = {})
        }
        Spacer(Modifier.height(Tokens.Spacing.l))
        ControlCenterCard("Brightness") {
            Slider(value = 0.6f, onValueChange = {})
        }
        Spacer(Modifier.height(Tokens.Spacing.l))
        Button("Close", onClick = onDismiss)
    }
}
