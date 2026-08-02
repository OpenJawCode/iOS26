package dev.ios26.controlcenter.state

import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.ios26.controlcenter.CcLog

/**
 * Active media session observation (ADR-0037): metadata + transport only, via the public
 * MediaSessionManager API. No artwork capture, no notification interception.
 */
class MediaSessionState(context: Context) {
    private val app: Context = context.applicationContext
    private val sessionManager: MediaSessionManager? =
        runCatching { app.getSystemService(MediaSessionManager::class.java) }.getOrNull()

    var title by mutableStateOf<String?>(null)
        private set
    var artist by mutableStateOf<String?>(null)
        private set
    var isPlaying by mutableStateOf(false)
        private set

    private var controller: MediaController? = null

    fun refresh() {
        val active = runCatching {
            sessionManager
                ?.getActiveSessions(null)
                ?.firstOrNull { it.playbackState?.state != PlaybackState.STATE_NONE }
        }.onFailure { CcLog.tag("media sessions unavailable: $it") }.getOrNull()
        controller = active
        active ?: run {
            title = null
            artist = null
            isPlaying = false
            return
        }
        val metadata = active.metadata
        title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        isPlaying = active.playbackState?.state == PlaybackState.STATE_PLAYING
        CcLog.tag("media session: $title — $artist")
    }

    fun togglePlay() {
        controller ?: return
        val tc = controller!!.transportControls
        if (isPlaying) tc.pause() else tc.play()
    }

    fun next() {
        controller?.transportControls?.skipToNext()
    }

    fun previous() {
        controller?.transportControls?.skipToPrevious()
    }
}
