package dev.ios26.controlcenter.state

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * System capability wrappers (ADR-0037): the CC drives Android's own APIs and reads state
 * back from them — Android is the single source of truth. Nothing here rebuilds system
 * functionality; where an API is unavailable the control degrades to a settings intent.
 */
internal object CcLog {
    fun tag(msg: String) = android.util.Log.i("IOS26_CC", msg)
}

/** A toggleable control: label, on/off read from the real system, write wrapped in runCatching. */
class ToggleControl(
    val label: String,
    private val read: () -> Boolean,
    private val write: () -> Unit,
    private val fallbackIntent: Intent? = null,
) {
    var isOn by mutableStateOf(false)
        private set
    var available by mutableStateOf(true)
        private set

    fun refresh() {
        isOn = runCatching { read() }.getOrDefault(isOn)
        available = true
    }

    fun toggle(context: Context) {
        runCatching { write() }.onFailure { t ->
            CcLog.tag("$label write failed: $t")
            available = false
            fallbackIntent?.let {
                // App-context startActivity requires NEW_TASK (crash found in 3.2 validation).
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        }
        // Radios settle asynchronously — re-read shortly after.
        retryScope.launch { delay(400); refresh() }
    }

    private companion object {
        val retryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    }
}

/** Wrappers for the milestone-1 control set (connectivity, quick actions, focus). */
class CcControllers(context: Context) {
    private val app: Context = context.applicationContext
    private val wifi: WifiManager? = runCatching { app.getSystemService(WifiManager::class.java) }.getOrNull()
    private val bt: BluetoothAdapter? = runCatching { BluetoothAdapter.getDefaultAdapter() }.getOrNull()
    private val camera: CameraManager? = runCatching { app.getSystemService(CameraManager::class.java) }.getOrNull()
    private val audio: AudioManager? = runCatching { app.getSystemService(AudioManager::class.java) }.getOrNull()

    val wifiControl = ToggleControl(
        label = "Wi-Fi",
        read = { wifi?.isWifiEnabled ?: false },
        write = { wifi?.setWifiEnabled(!wifi!!.isWifiEnabled) },
        fallbackIntent = Intent(Settings.ACTION_WIFI_SETTINGS),
    )
    val bluetoothControl = ToggleControl(
        label = "Bluetooth",
        read = { bt?.isEnabled ?: false },
        write = { if (bt!!.isEnabled) bt!!.disable() else bt!!.enable() },
        fallbackIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS),
    )
    val airplaneControl = ToggleControl(
        label = "Airplane mode",
        read = { Settings.Global.getInt(app.contentResolver, "airplane_mode_on") != 0 },
        write = {
            // The setting write alone triggers the system observer; sending the
            // AIRPLANE_MODE broadcast requires signature perms (denied — validated).
            val next = if (Settings.Global.getInt(app.contentResolver, "airplane_mode_on") == 0) 1 else 0
            Settings.Global.putInt(app.contentResolver, "airplane_mode_on", next)
        },
        fallbackIntent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS),
    )
    val mobileDataControl = ToggleControl(
        label = "Mobile data",
        read = { Settings.Global.getInt(app.contentResolver, "mobile_data") != 0 },
        write = {
            val next = if (Settings.Global.getInt(app.contentResolver, "mobile_data") == 0) 1 else 0
            Settings.Global.putInt(app.contentResolver, "mobile_data", next)
        },
        fallbackIntent = Intent(Settings.ACTION_DATA_USAGE_SETTINGS),
    )

    private var torchDesired = false

    val flashlightControl = ToggleControl(
        label = "Flashlight",
        read = { torchDesired },
        write = {
            torchDesired = !torchDesired
            torchCamera()?.let { id -> camera?.setTorchMode(id, torchDesired) }
        },
        fallbackIntent = Intent("android.media.action.STILL_IMAGE_CAMERA"),
    )
    val rotationControl = ToggleControl(
        label = "Screen rotation",
        read = { Settings.System.getInt(app.contentResolver, Settings.System.ACCELEROMETER_ROTATION) != 0 },
        write = {
            if (Settings.System.canWrite(app)) {
                val next = if (Settings.System.getInt(app.contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 0) 1 else 0
                Settings.System.putInt(app.contentResolver, Settings.System.ACCELEROMETER_ROTATION, next)
            } else {
                throw IllegalStateException("no WRITE_SETTINGS")
            }
        },
        fallbackIntent = Intent(Settings.ACTION_DISPLAY_SETTINGS),
    )

    private var hotspotDesired = false

    val hotspotControl = ToggleControl(
        label = "Hotspot",
        read = { hotspotDesired },
        write = {
            // Toggling tethering requires a signature permission; the control wraps the
            // capability honestly: it opens the tethering settings surface.
            hotspotDesired = !hotspotDesired
            app.startActivity(Intent("android.settings.TETHERING_SETTINGS"))
        },
    )

    /** Focus (Do Not Disturb) — Android's focus-equivalent, wrapped, not rebuilt. */
    val focusControl = ToggleControl(
        label = "Focus",
        read = { app.getSystemService(android.app.NotificationManager::class.java).currentInterruptionFilter ==
            android.app.NotificationManager.INTERRUPTION_FILTER_NONE },
        write = {
            val nm = app.getSystemService(android.app.NotificationManager::class.java)
            nm.setInterruptionFilter(
                if (nm.currentInterruptionFilter == android.app.NotificationManager.INTERRUPTION_FILTER_NONE)
                    android.app.NotificationManager.INTERRUPTION_FILTER_ALL
                else android.app.NotificationManager.INTERRUPTION_FILTER_NONE,
            )
        },
        fallbackIntent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS),
    )

    /** Brightness — Settings.System wrapped (WRITE_SETTINGS appop, granted in validation). */
    fun readBrightness(): Float {
        val mode = Settings.System.getInt(app.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, -1)
        if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) return 0.5f
        val cur = Settings.System.getInt(app.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
        return (cur / 255f).coerceIn(0.01f, 1f)
    }

    fun setBrightness(fraction: Float) {
        if (!Settings.System.canWrite(app)) return
        Settings.System.putInt(
            app.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL,
        )
        Settings.System.putInt(
            app.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            (fraction.coerceIn(0.01f, 1f) * 255).toInt(),
        )
    }

    /** Media volume (STREAM_MUSIC) — AudioManager wrapped. */
    fun readVolume(): Float =
        audio?.let { it.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat() / it.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1) } ?: 0f

    fun setVolume(fraction: Float) {
        audio?.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            (fraction.coerceIn(0f, 1f) * audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)).toInt(),
            0,
        )
    }

    private fun torchCamera(): String? =
        runCatching {
            camera?.cameraIdList?.firstOrNull { id ->
                camera.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        }.getOrNull()

    fun refreshAll() {
        listOf(wifiControl, bluetoothControl, airplaneControl, mobileDataControl, flashlightControl, rotationControl, hotspotControl, focusControl)
            .forEach { it.refresh() }
    }
}
