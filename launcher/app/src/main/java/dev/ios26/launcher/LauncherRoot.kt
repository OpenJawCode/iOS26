package dev.ios26.launcher

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.unit.dp
import dev.ios26.design.components.AppIcon
import dev.ios26.design.components.Dock
import dev.ios26.design.components.Sheet
import dev.ios26.design.engines.MotionEngine
import dev.ios26.design.engines.glassMaterial
import dev.ios26.design.theme.GlassIntensity
import dev.ios26.design.theme.LocalTokenSet
import dev.ios26.design.tokens.Tokens
import dev.ios26.launcher.data.AppInfo
import dev.ios26.launcher.data.AppRepository
import dev.ios26.launcher.ui.AppLibraryScreen
import dev.ios26.launcher.ui.ControlCenterSheet
import dev.ios26.launcher.ui.SettingsSheet

/** Loading / empty / error states, then the springboard. */
@Composable
fun LauncherRoot() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var state by remember { mutableStateOf<AppsState>(AppsState.Loading) }

    LaunchedEffect(Unit) {
        state = AppsState.Loading
        state = runCatching { AppsState.Loaded(AppRepository.loadLaunchableApps(context)) }
            .getOrElse { AppsState.Error(it.message ?: "unknown") }
    }

    when (val s = state) {
        AppsState.Loading -> LoadingState()
        is AppsState.Error -> ErrorState(s.message)
        is AppsState.Loaded -> SpringboardHost(s.apps)
    }
}

sealed interface AppsState {
    data object Loading : AppsState
    data class Loaded(val apps: List<AppInfo>) : AppsState
    data class Error(val message: String) : AppsState
}

@Composable
private fun LoadingState() {
    val colors = LocalTokenSet.current
    Box(Modifier.fillMaxSize().background(colors.background), contentAlignment = Alignment.Center) {
        dev.ios26.design.components.Card("Loading", subtitle = "indexing apps…")
    }
}

@Composable
private fun ErrorState(message: String) {
    val colors = LocalTokenSet.current
    Box(Modifier.fillMaxSize().background(colors.background), contentAlignment = Alignment.Center) {
        dev.ios26.design.components.Card("Something went wrong", subtitle = message)
    }
}

/** Springboard host: pager of pages + dock + dots + sheets (CC/settings) + app library. */
@Composable
private fun SpringboardHost(apps: List<AppInfo>) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val colors = LocalTokenSet.current
    var showCc by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showLibrary by remember { mutableStateOf(false) }
    var selectedFolder by remember { mutableStateOf<List<AppInfo>?>(null) }
    val dockApps = apps.take(5)
    val gridApps = apps.drop(5)

    val pageSize = Tokens.Grid.springboardColumns * 4
    val pages = if (gridApps.isEmpty()) 1 else (gridApps.size + pageSize - 1) / pageSize
    val pager = rememberPagerState(initialPage = 0) { pages }

    Box(Modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxSize()) {
            HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { page ->
                val slice = gridApps.drop(page * pageSize).take(pageSize)
                SpringboardPage(apps = slice, onApp = { AppRepository.launch(context, it) }, onFolder = { selectedFolder = it })
            }
            Spacer(Modifier.height(Tokens.Spacing.xs))
            PageDots(count = pages, current = pager.currentPage)
            Dock(
                icons = dockApps.map { app ->
                    { AppIcon(color = app.iconColor()) }
                },
            )
            Spacer(Modifier.height(Tokens.Spacing.l))
        }
    }

    // Gesture: swipe down from top edge opens Control Center (in-app until the LSPosed hook lands).
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (down.position.y < 80f) {
                        var opened = false
                        while (true) {
                            val event = awaitPointerEvent()
                            if (!opened && event.changes.any { change -> change.position.y - down.position.y > 100f }) {
                                showCc = true
                                opened = true
                            }
                            if (event.changes.all { !it.pressed }) break
                        }
                    }
                }
            },
    )

    if (showCc) ControlCenterSheet(onDismiss = { showCc = false })
    if (showSettings) SettingsSheet(onDismiss = { showSettings = false })
    if (selectedFolder != null) {
        FolderSheet(selectedFolder!!, onDismiss = { selectedFolder = null })
    }
    if (showLibrary) {
        AppLibraryScreen(apps = apps, onDismiss = { showLibrary = false }, onApp = { AppRepository.launch(context, it) })
    }
}

/** One springboard page: rows of app icons per grid tokens. */
@Composable
private fun SpringboardPage(
    apps: List<AppInfo>,
    onApp: (AppInfo) -> Unit,
    onFolder: (List<AppInfo>) -> Unit,
) {
    val cols = Tokens.Grid.springboardColumns
    val rows = apps.chunked(cols)
    Column(
        Modifier.fillMaxSize().padding(horizontal = Tokens.Grid.margin, vertical = Tokens.Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Tokens.Grid.iconGap),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        rows.forEachIndexed { rowIndex, rowApps ->
            Row(horizontalArrangement = Arrangement.spacedBy(Tokens.Grid.gutter)) {
                rowApps.forEach { app ->
                    AppIcon(label = app.label, color = app.iconColor())
                        .let { icon ->
                            Box(Modifier
                                .size(Tokens.Grid.iconSize + 8.dp)
                                .clip(RoundedCornerShape(Tokens.Radius.small))
                                .clickableNoRipple { onApp(app) }
                                .pointerInput(app) {
                                    detectTapGestures(onLongPress = { onFolder(listOf(app)) })
                                },
                                contentAlignment = Alignment.Center,
                            ) { icon }
                        }
                }
            }
        }
    }
}

@Composable
private fun PageDots(count: Int, current: Int) {
    val colors = LocalTokenSet.current
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { index ->
            Box(
                Modifier
                    .size(Tokens.Grid.pageDotsSize)
                    .clip(RoundedCornerShape(Tokens.Radius.pill))
                    .background(if (index == current) colors.labelSecondary else colors.separator),
            )
        }
    }
}

@Composable
private fun FolderSheet(apps: List<AppInfo>, onDismiss: () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(dev.ios26.design.theme.LocalGlassIntensity provides GlassIntensity.Prominent) {
        Sheet(modifier = Modifier.fillMaxWidth()) {
            dev.ios26.design.components.Folder(
                icons = apps.map { it.iconColor() },
            )
            Spacer(Modifier.height(Tokens.Spacing.m))
            dev.ios26.design.components.Button("Close", onClick = onDismiss)
        }
    }
}

private fun AppInfo.iconColor(): Color {
    // Deterministic color from package name — placeholder until the icon pipeline lands (ADR-0012).
    val hue = (packageName.hashCode() and 0xFFFFFF).toFloat() % 360f
    return androidx.compose.ui.graphics.Color.hsv(hue, 0.55f, 0.85f)
}

@androidx.compose.runtime.Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.clip(RoundedCornerShape(Tokens.Radius.small))
        .clickable(onClick = onClick, interactionSource = remember { MutableInteractionSource() }, indication = null)
