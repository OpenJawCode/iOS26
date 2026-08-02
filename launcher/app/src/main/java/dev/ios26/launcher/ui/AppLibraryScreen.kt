package dev.ios26.launcher.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.ios26.design.components.AppIcon
import dev.ios26.design.components.Button
import dev.ios26.design.components.SearchField
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens
import dev.ios26.launcher.data.AppInfo

/** App Library: Spotlight-style search + auto-grid (categories in Phase 4). */
@Composable
fun AppLibraryScreen(apps: List<AppInfo>, onDismiss: () -> Unit, onApp: (AppInfo) -> Unit) {
    val colors = LocalTokenSet.current
    var query by remember { mutableStateOf("") }
    val filtered = if (query.isBlank()) apps else apps.filter { it.label.contains(query, ignoreCase = true) }

    Column(
        Modifier.fillMaxSize().padding(Tokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(Tokens.Spacing.xxxl))
        SearchField(placeholder = "Search")
        Spacer(Modifier.height(Tokens.Spacing.l))
        Button("Back to springboard", onClick = onDismiss)
        Spacer(Modifier.height(Tokens.Spacing.l))
        LazyVerticalGrid(
            columns = GridCells.Fixed(Tokens.Grid.appLibraryColumns),
            horizontalArrangement = Arrangement.spacedBy(Tokens.Spacing.l),
            verticalArrangement = Arrangement.spacedBy(Tokens.Spacing.xl),
        ) {
            items(filtered, key = { it.packageName }) { app ->
                AppIcon(label = app.label, color = iconColor(app.packageName))
                    .let { icon ->
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(Tokens.Radius.small))
                                .clickableNoRipple { onApp(app) },
                            contentAlignment = Alignment.Center,
                        ) { icon }
                    }
            }
        }
    }
}

private fun iconColor(pkg: String): Color =
    Color.hsv((pkg.hashCode() and 0xFFFFFF).toFloat() % 360f, 0.55f, 0.85f)

@androidx.compose.runtime.Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clip(RoundedCornerShape(Tokens.Radius.small))
        .clickable(onClick = onClick, interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }, indication = null)
