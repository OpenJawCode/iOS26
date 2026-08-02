package dev.ios26.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.engines.glassLighting
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

private val LabelStyle = TextStyle(fontSize = Tokens.Type.Caption1.size, fontWeight = FontWeight(400))
private val TitleStyle = TextStyle(fontSize = Tokens.Type.Footnote.size, fontWeight = FontWeight(600))

/** Navigation bar (iOS tab bar) — glass, token height, centered items. */
@Composable
fun NavigationBar(
    items: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Row(
        modifier
            .fillMaxWidth()
            .glassMaterial(RoundedCornerShape(topStart = Tokens.Radius.sheet, topEnd = Tokens.Radius.sheet)).glassLighting(RoundedCornerShape(topStart = Tokens.Radius.sheet, topEnd = Tokens.Radius.sheet))
            .padding(vertical = Tokens.Spacing.sm),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEachIndexed { index, item ->
            val selected = index == selectedIndex
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(Tokens.Radius.small))
                    .background(if (selected) colors.backgroundTertiary else Color.Transparent)
                    .padding(horizontal = Tokens.Spacing.m, vertical = Tokens.Spacing.xs)
                    .clickableNoRipple { onSelect(index) },
            ) {
                Spacer(Modifier.height(4.dp))
                androidx.compose.foundation.text.BasicText(
                    item,
                    style = LabelStyle.copy(color = if (selected) colors.accent else colors.labelSecondary),
                )
            }
        }
    }
}

/** Springboard dock — glass, token height, centered icons. */
@Composable
fun Dock(
    icons: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Tokens.Grid.dockMargin)
            .height(Tokens.Grid.dockHeight)
            .glassMaterial(RoundedCornerShape(Tokens.Radius.largeCard))
            .glassLighting(RoundedCornerShape(Tokens.Radius.largeCard)),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icons.forEach { icon ->
            Box(Modifier.size(Tokens.Grid.dockIconSize), contentAlignment = Alignment.Center) { icon() }
        }
    }
}

/** Quick Settings tile (CC grid) — glass, token radius. */
@Composable
fun QuickSettingsTile(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Column(
        modifier
            .clip(RoundedCornerShape(Tokens.Radius.card))
            .background(if (active) colors.accent else colors.glassFill)
            .glassLighting(RoundedCornerShape(Tokens.Radius.card))
            .padding(Tokens.Spacing.m)
            .clickableNoRipple(onClick),
    ) {
        androidx.compose.foundation.text.BasicText(
            label,
            style = LabelStyle.copy(color = if (active) Color.White else colors.labelPrimary),
        )
    }
}

/** Control Center card — larger glass card with title + content. */
@Composable
fun ControlCenterCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalTokenSet.current
    Column(
        modifier
            .glassMaterial(RoundedCornerShape(Tokens.Radius.largeCard))
            .glassLighting(RoundedCornerShape(Tokens.Radius.largeCard))
            .padding(Tokens.Spacing.l),
    ) {
        androidx.compose.foundation.text.BasicText(title, style = TitleStyle.copy(color = colors.labelSecondary))
        Spacer(Modifier.height(Tokens.Spacing.sm))
        content()
    }
}
