package dev.ios26.config

import dev.ios26.testing.TempStore
import java.io.File
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AtomicWriterTest {

    private lateinit var dir: File

    @Before
    fun setUp() {
        dir = TempStore.create()
    }

    @After
    fun tearDown() {
        TempStore.cleanup(dir)
    }

    @Test
    fun writeThenReadRoundTrips() {
        val writer = AtomicWriter(dir, "doc.json")
        writer.write("""{"a":1}""")
        assertEquals("""{"a":1}""", writer.readOrNull())
    }

    @Test
    fun noTempFileLeftBehind() {
        val writer = AtomicWriter(dir, "doc.json")
        writer.write("hello")
        assertFalse(File(dir, "doc.json.tmp").exists())
        assertTrue(File(dir, "doc.json").exists())
    }

    @Test
    fun overwriteReplacesContent() {
        val writer = AtomicWriter(dir, "doc.json")
        writer.write("v1")
        writer.write("v2")
        assertEquals("v2", writer.readOrNull())
    }

    @Test
    fun missingFileReadsNull() {
        val writer = AtomicWriter(dir, "absent.json")
        assertNull(writer.readOrNull())
    }
}
