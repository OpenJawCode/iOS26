package dev.ios26.config

import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * File watch via polling (spike R7: FileObserver/inotify is unreliable from untrusted_app on
 * this firmware). Interval 200ms proven at ~68ms effective e2e. Production: FileObserver behind
 * Phase-4 policy; polling stays the documented fallback (ADR-0019).
 */
class PollWatcher(
    private val file: File,
    private val scope: CoroutineScope,
    private val intervalMillis: Long = 200L,
) {
    @Volatile
    private var lastSeen: Long = 0L

    fun observe(onChange: (mtime: Long) -> Unit): Job = scope.launch {
        while (isActive) {
            val mtime = file.lastModified()
            if (mtime > lastSeen) {
                lastSeen = mtime
                onChange(mtime)
            }
            delay(intervalMillis)
        }
    }
}
