package dev.ios26.controlcenter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

/**
 * Keeps the overlay host process visible while the CC exists (device finding: this
 * firmware removes/never-presents overlay surfaces of cached processes — "setHasOverlayUi
 * on unknown pid"). The standard overlay-app pattern: a lightweight foreground service.
 * targetSdk 33 → no FGS type required.
 */
class CcForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, notification())
        return START_STICKY
    }

    private fun notification(): Notification {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "iOS26 Control Center", NotificationManager.IMPORTANCE_MIN),
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("iOS26 Control Center")
            .setContentText("Gesture surface active")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "ios26-cc"
        private const val NOTIFICATION_ID = 2601

        fun start(context: Context) {
            context.startForegroundService(Intent(context, CcForegroundService::class.java))
        }
    }
}
