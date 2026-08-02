package dev.ios26.config

import dev.ios26.testing.TempStore
import java.io.File
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PollWatcherTest {

    private lateinit var dir: File
    private lateinit var file: File

    @Before
    fun setUp() {
        dir = TempStore.create()
        file = File(dir, "event.json")
    }

    @After
    fun tearDown() {
        TempStore.cleanup(dir)
    }

    @Test
    fun firesWhenFileChanges() = runBlocking {
        val seen = CompletableDeferred<Long>()
        val watcher = PollWatcher(file, this, intervalMillis = 20L)
        val job = watcher.observe { seen.complete(it) }
        try {
            file.writeText("first")
            file.setLastModified(123_456L)

            val mtime = withTimeout(2_000) { seen.await() }
            assertEquals(123_456L, mtime)
        } finally {
            job.cancel()
        }
    }

    @Test
    fun doesNotFireWhenUnchanged() = runBlocking {
        var fired = 0
        val watcher = PollWatcher(file, this, intervalMillis = 20L)
        val job = watcher.observe { fired++ }
        try {
            // No file ever appears — watcher must stay silent.
            delay(150)
            assertEquals(0, fired)
        } finally {
            job.cancel()
        }
    }
}
