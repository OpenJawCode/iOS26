package dev.ios26.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
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

private val BodyStyle = TextStyle(fontSize = Tokens.Type.Body.size, fontWeight = FontWeight(400))
private val CaptionStyle = TextStyle(fontSize = Tokens.Type.Caption1.size)

/** iOS-style notification banner — glass, icon slot, title + body. */
@Composable
fun Notification(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null,
) {
    val colors = LocalTokenSet.current
    Row(
        modifier
            .fillMaxWidth()
            .glassMaterial(RoundedCornerShape(Tokens.Radius.card)).glassLighting(RoundedCornerShape(Tokens.Radius.card))
            .padding(Tokens.Spacing.l),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.invoke()
        if (icon != null) Spacer(Modifier.width(Tokens.Spacing.m))
        Column {
            androidx.compose.foundation.text.BasicText(title, style = BodyStyle.copy(color = colors.labelPrimary, fontWeight = FontWeight(600)))
            androidx.compose.foundation.text.BasicText(body, style = CaptionStyle.copy(color = colors.labelSecondary))
        }
    }
}

/** iOS-style context menu — list of actions in a glass sheet. */
@Composable
fun ContextMenu(
    items: List<String>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Column(
        modifier
            .glassMaterial(RoundedCornerShape(Tokens.Radius.card)).glassLighting(RoundedCornerShape(Tokens.Radius.card))
            .padding(vertical = Tokens.Spacing.xs),
    ) {
        items.forEach { item ->
            androidx.compose.foundation.text.BasicText(
                item,
                style = BodyStyle.copy(color = colors.labelPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Tokens.Spacing.l, vertical = Tokens.Spacing.sm)
                    .clickableNoRipple { onItemClick(item) },
            )
        }
    }
}

/** App icon placeholder — squircle per iOS factor token (grid.iconSize). */
@Composable
fun AppIcon(
    label: String? = null,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val size = Tokens.Grid.iconSize
    val radius = size.value * Tokens.Radius.squircleFactor
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(size)
                .clip(RoundedCornerShape(radius.dp))
                .background(color),
        )
        if (label != null) {
            Spacer(Modifier.height(Tokens.Spacing.xs))
            androidx.compose.foundation.text.BasicText(label, style = CaptionStyle.copy(color = LocalTokenSet.current.labelPrimary))
        }
    }
}

/** Search field (Spotlight) — glass pill with placeholder. */
@Composable
fun SearchField(
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Tokens.Radius.pill))
            .background(colors.backgroundTertiary)
            .padding(horizontal = Tokens.Spacing.l, vertical = Tokens.Spacing.sm),
        contentAlignment = Alignment.CenterStart,
    ) {
        androidx.compose.foundation.text.BasicText(placeholder, style = BodyStyle.copy(color = colors.labelTertiary))
    }
}

/** Folder UI — grid of icons per folder tokens. */
@Composable
fun Folder(
    icons: List<Color>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    val columns = Tokens.Grid.folderColumns
    Column(
        modifier
            .glassMaterial(RoundedCornerShape(Tokens.Radius.largeCard)).glassLighting(RoundedCornerShape(Tokens.Radius.largeCard))
            .padding(Tokens.Spacing.l),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        icons.chunked(columns).forEach { row ->
            Row {
                row.forEach { color ->
                    Box(
                        Modifier
                            .padding(Tokens.Grid.folderGap)
                            .size(Tokens.Spacing.huge)
                            .clip(RoundedCornerShape(Tokens.Radius.small))
                            .background(color),
                    )
                }
            }
        }
    }
}

/** Widget frame — glass framing for non-tinting widgets (survey R8). */
@Composable
fun WidgetFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .glassMaterial(RoundedCornerShape(Tokens.Radius.largeCard)).glassLighting(RoundedCornerShape(Tokens.Radius.largeCard))
            .padding(Tokens.Spacing.m),
    ) {
        content()
    }
}

/** Lock screen component scaffold — clock + status, token-driven. */
@Composable
fun LockScreenComponent(
    clock: String,
    date: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTokenSet.current
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        androidx.compose.foundation.text.BasicText(
            clock,
            style = TextStyle(fontSize = Tokens.Type.LargeTitle.size, fontWeight = FontWeight(700), color = colors.labelPrimary),
        )
        androidx.compose.foundation.text.BasicText(date, style = CaptionStyle.copy(color = colors.labelSecondary))
    }
}

@Composable
internal fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick,
    )
