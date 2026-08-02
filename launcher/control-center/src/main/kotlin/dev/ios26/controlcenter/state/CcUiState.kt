package dev.ios26.controlcenter.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/** All mutable surface state — host-local, Android APIs are the source of truth (ADR-0037). */
class CcUiState(context: Context) {
    private val app: Context = context.applicationContext
    val controllers = CcControllers(context)
    val media = MediaSessionState(context)

    var brightness by mutableFloatStateOf(0.5f)
        private set
    var volume by mutableFloatStateOf(0.5f)
        private set

    val wifi get() = controllers.wifiControl
    val bluetooth get() = controllers.bluetoothControl
    val airplane get() = controllers.airplaneControl
    val mobileData get() = controllers.mobileDataControl
    val flashlight get() = controllers.flashlightControl
    val rotation get() = controllers.rotationControl
    val hotspot get() = controllers.hotspotControl
    val focus get() = controllers.focusControl

    var isBrightnessWritable by mutableStateOf(true)
        private set

    fun refresh() {
        runCatching { controllers.refreshAll() }
        brightness = runCatching { controllers.readBrightness() }.getOrDefault(0.5f)
        volume = runCatching { controllers.readVolume() }.getOrDefault(0.5f)
        isBrightnessWritable = android.provider.Settings.System.canWrite(app)
        runCatching { media.refresh() }
    }

    fun updateBrightness(fraction: Float) {
        brightness = fraction
        runCatching { controllers.setBrightness(fraction) }
    }

    fun updateVolume(fraction: Float) {
        volume = fraction
        runCatching { controllers.setVolume(fraction) }
    }
}
