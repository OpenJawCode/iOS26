package dev.ios26.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ios26.design.components.AppIcon
import dev.ios26.design.components.Button
import dev.ios26.design.components.Card
import dev.ios26.design.components.ControlCenterCard
import dev.ios26.design.components.ContextMenu
import dev.ios26.design.components.Dock
import dev.ios26.design.components.Folder
import dev.ios26.design.components.ListItem
import dev.ios26.design.components.LockScreenComponent
import dev.ios26.design.components.NavigationBar
import dev.ios26.design.components.Notification
import dev.ios26.design.components.Popover
import dev.ios26.design.components.QuickSettingsTile
import dev.ios26.design.components.SearchField
import dev.ios26.design.components.Sheet
import dev.ios26.design.components.Slider
import dev.ios26.design.components.Switch
import dev.ios26.design.components.Toggle
import dev.ios26.design.components.WidgetFrame
import dev.ios26.design.engines.DynamicColorEngine
import dev.ios26.design.engines.GlassEngine
import dev.ios26.design.engines.applyGlassBlur
import dev.ios26.design.engines.blurBackdrop
import dev.ios26.design.engines.elevation
import dev.ios26.design.theme.Ios26Theme
import dev.ios26.design.tokens.Tokens

/**
 * DEV-ONLY Component Gallery — the interactive prototype surface for the design system.
 * Not launcher functionality; replaced by real screens from Phase 3. Renders every
 * component token-driven, light/dark + glass intensity variants.
 */
class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyGlassBlur(GlassEngine.blurRadiusFor(dev.ios26.design.theme.GlassIntensity.Standard))
        setContent {
            Ios26Theme {
                GalleryContent()
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun GalleryContent() {
    val accent = Tokens.Semantic.Light.accent // bisect: DynamicColorEngine
    LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .background(dev.ios26.design.theme.LocalTokenSet.current.background)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item { LockScreenComponent(clock = "21:47", date = "Saturday, August 1") }
                    item { Button("Button", onClick = {}) }
                    item { Button("Button (filled=false)", onClick = {}, filled = false) }
                    item { Switch(checked = true, onCheckedChange = {}) }
                    item { Slider(value = 0.4f, onValueChange = {}) }
                    item { Toggle(checked = true, onCheckedChange = {}) }
                    item { Card("Card", subtitle = "A glass card with token radius + elevation") }
                    item { ListItem("List Item", subtitle = "with a subtitle") }
                    item { Notification(title = "Notification", body = "A glass notification banner") }
                    item { SearchField(placeholder = "Search") }
                    item { QuickSettingsTile(label = "Wi-Fi", active = true, onClick = {}) }
                    item { ControlCenterCard("Control Center") { Slider(value = 0.6f, onValueChange = {}) } }
                    item { WidgetFrame { Card("Widget", subtitle = "glass-framed (survey R8)") } }
                    item { Folder(icons = listOf(Color(0xFF0A84FF), Color(0xFF30D158), Color(0xFFFF9F0A), Color(0xFFBF5AF2), Color(0xFFFF2D55), Color(0xFF64D2FF))) }
                    item { ContextMenu(items = listOf("Rename", "Share", "Delete"), onItemClick = {}) }
                    item { Popover { ListItem("Popover item") } }
                    item { Sheet { Button("Sheet Button", onClick = {}) } }
                    item {
                        Dock(icons = listOf(
                            { AppIcon(color = accent) },
                            { AppIcon(color = Color(0xFF30D158)) },
                            { AppIcon(color = Color(0xFFFF9F0A)) },
                            { AppIcon(color = Color(0xFF0A84FF)) },
                        ))
                    }
                    item { NavigationBar(items = listOf("Home", "Search", "Settings"), selectedIndex = 0, onSelect = {}) }
                }
}
