package dev.ios26.controlcenter

import android.content.Context
import android.provider.Settings
import dev.ios26.config.ConfigStore
import dev.ios26.config.PollWatcher
import dev.ios26.controlcenter.state.CcUiState
import dev.ios26.controlcenter.ui.ControlCenterSurface
import dev.ios26.controlcenter.window.CcOverlayWindow
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * CC host (ADR-0036/0037): watches the hook's event bus, raises/dismisses the overlay.
 * Graceful degradation: no flag → no watcher; no overlay permission → raise() no-ops
 * (the launcher keeps its in-app sheet fallback, ADR-0005).
 */
class CcHost(context: Context) {
    private val app: Context = context.applicationContext
    private val store = ConfigStore(File(SHARED_EVENTS_ROOT))
    private val window = CcOverlayWindow(app)
    private val state = CcUiState(app)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var watcherJob: Job? = null

    /** The validated 3.1 flag gate — same file the hook checks (ADR-0038). */
    fun isFlagEnabled(): Boolean =
        flagFile().exists()

    fun canOverlay(): Boolean =
        Settings.canDrawOverlays(app)

    /** True when the surface is live end-to-end (flag + overlay permission). */
    fun isActive(): Boolean =
        isFlagEnabled() && canOverlay()

    fun start() {
        if (!isFlagEnabled()) {
            CcLog.tag("flag off — host idle (stock behavior)")
            return
        }
        CcForegroundService.start(app)
        watcherJob?.cancel()
        watcherJob = PollWatcher(store.eventFile(EVENT_OPEN), scope).observe {
            store.consumeEvent(EVENT_OPEN)
            CcLog.tag("cc-open event")
            raise()
        }
    }

    fun stop() {
        watcherJob?.cancel()
        watcherJob = null
        dismiss()
    }

    /** Manual raise (launcher in-app gesture / future companions). */
    fun raise() {
        if (!canOverlay()) {
            CcLog.tag("overlay permission missing — in-app fallback expected")
            return
        }
        // Device finding: this Moto firmware only presents an app's windows while it has a
        // resumed activity. The transparent host activity keeps the process present so the
        // overlay renders over ANY foreground app (not just the launcher).
        runCatching {
            app.startActivity(
                android.content.Intent(app, CcHostActivity::class.java)
                    .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
        state.refresh()
        window.show { ControlCenterSurface(state, onDismiss = { dismiss() }) }
    }

    fun dismiss() {
        if (!window.isAttached) return
        window.hide()
        CcHostActivity.finishHost()
        state.media.refresh()
        runCatching { store.writeEvent(EVENT_CLOSE) } // informational (ADR-0037)
    }

    private fun flagFile(): File {
        val primary = File("/data/adb/ios26/shared/flags/${FLAG}.flag")
        return if (primary.exists()) primary else File(FLAGS_FALLBACK, "${FLAG}.flag")
    }

    companion object {
        private const val FLAG = "control-center"
        private const val FLAGS_FALLBACK = "/data/local/tmp/ios26/flags"
        private const val SHARED_EVENTS_ROOT = "/data/local/tmp/ios26"
        private const val EVENT_OPEN = "cc-open"
        private const val EVENT_CLOSE = "cc-close"
    }
}

internal object CcLog {
    fun tag(msg: String) = android.util.Log.i("IOS26_CC", msg)
}
