package dev.ios26.controlcenter

import android.app.Activity
import android.os.Bundle

/**
 * Transparent host activity (device finding: on this Moto firmware an app's windows only
 * present while the app has a RESUMED activity — the overlay alone renders nothing over
 * other apps). The CC raises this invisible activity behind the overlay so the process's
 * surfaces present; it is never visible, never in recents, and finishes on dismiss.
 * Lock-screen CC stays out of scope (D1: lock screen deferred).
 */
class CcHostActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The overlay window already holds the UI; this activity exists only to keep the
        // process present. Nothing to render here.
        overridePendingTransition(0, 0)
        INSTANCE = this
    }

    override fun onDestroy() {
        super.onDestroy()
        if (INSTANCE === this) INSTANCE = null
    }

    companion object {
        private var INSTANCE: CcHostActivity? = null

        /** Dismiss path: finishes the host activity if it is up. */
        fun finishHost() {
            INSTANCE?.finish()
            INSTANCE = null
        }
    }
}
