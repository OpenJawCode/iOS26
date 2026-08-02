package dev.ios26.config

import dev.ios26.testing.TempStore
import dev.ios26.testing.withZones
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConfigStoreTest {

    private lateinit var root: File
    private lateinit var store: ConfigStore

    @Before
    fun setUp() {
        root = TempStore.create().withZones()
        store = ConfigStore(root)
    }

    @After
    fun tearDown() {
        TempStore.cleanup(root)
    }

    @Test
    fun storeRootRoundTrip() {
        val expected = StoreRoot(version = 1, zones = listOf("system", "shared", "events"))
        store.writeStoreRoot(expected)
        assertEquals(expected, store.readStoreRoot())
    }

    @Test
    fun missingStoreRootReadsNull() {
        assertNull(store.readStoreRoot())
    }

    @Test
    fun invalidStoreRootRejected() {
        val invalid = StoreRoot(version = 0)
        try {
            store.writeStoreRoot(invalid)
            throw AssertionError("expected IllegalStateException")
        } catch (expected: IllegalStateException) {
            // schema violation blocked the write
        }
        assertNull(store.readStoreRoot())
    }

    @Test
    fun eventWriteIsAtomicAndReadable() {
        store.ensureZones()
        store.writeEvent("cc-open")
        val written = store.eventFile().readText()
        assertTrue(written.contains("\"type\":\"cc-open\""))
        assertTrue(written.contains("\"ts\":"))
        assertNotNull(store.eventFile())
    }

    @Test
    fun zonesCreatedByIdempotentCall() {
        store.ensureZones()
        store.ensureZones()
        assertTrue(File(root, "system").isDirectory)
        assertTrue(File(root, "shared/events").isDirectory)
    }
}
