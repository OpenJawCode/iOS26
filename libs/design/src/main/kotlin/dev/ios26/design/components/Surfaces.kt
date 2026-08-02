package dev.ios26.design.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import dev.ios26.design.engines.glassLighting
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens

private val BodyStyle = TextStyle(
    fontSize = Tokens.Type.Body.size,
    fontWeight = FontWeight(400),
)
private val CaptionStyle = TextStyle(fontSize = Tokens.Type.Caption1.size)

/** iOS-style card — glass surface, token radius, elevation by level. */
@Composable
fun Card(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    val colors = LocalTokenSet.current
    Column(
        modifier
            .fillMaxWidth()
            .glassMaterial(RoundedCornerShape(Tokens.Radius.card)).glassLighting(RoundedCornerShape(Tokens.Radius.card))
            .padding(Tokens.Spacing.l),
    ) {
        androidx.compose.foundation.text.BasicText(
            title,
            style = BodyStyle.copy(color = colors.labelPrimary, fontWeight = FontWeight(600)),
        )
        if (subtitle != null) {
            Spacer(Modifier.height(Tokens.Spacing.xs))
            androidx.compose.foundation.text.BasicText(subtitle, style = CaptionStyle.copy(color = colors.labelSecondary))
        }
    }
}

/** iOS-style list item — label rows with separators. */
@Composable
fun ListItem(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val colors = LocalTokenSet.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Tokens.Spacing.l, vertical = Tokens.Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            androidx.compose.foundation.text.BasicText(
                title,
                style = BodyStyle.copy(color = colors.labelPrimary),
            )
            if (subtitle != null) {
                androidx.compose.foundation.text.BasicText(subtitle, style = CaptionStyle.copy(color = colors.labelSecondary))
            }
        }
        trailing?.invoke()
    }
}

/** iOS-style sheet — rounded top corners, grabber, token sheet background. */
@Composable
fun Sheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalTokenSet.current
    Column(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = Tokens.Radius.sheet, topEnd = Tokens.Radius.sheet))
            .background(colors.sheetBackground)
            .padding(Tokens.Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .width(36.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(Tokens.Radius.pill))
                .background(colors.separatorOpaque)
        )
        Spacer(Modifier.height(Tokens.Spacing.l))
        content()
    }
}

/** iOS-style popover — scales from its trigger (emil-design-eng doctrine). */
@Composable
fun Popover(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalTokenSet.current
    Box(
        modifier
            .glassMaterial(RoundedCornerShape(Tokens.Radius.card)).glassLighting(RoundedCornerShape(Tokens.Radius.card))
            .padding(Tokens.Spacing.l),
    ) {
        content()
    }
}
