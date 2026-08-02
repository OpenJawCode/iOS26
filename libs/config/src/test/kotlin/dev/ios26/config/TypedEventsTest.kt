package dev.ios26.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/** Typed event lifecycle on the shared store (ADR-0019/0037): write → read → consume. */
class TypedEventsTest {

    @get:Rule
    val tmp = TemporaryFolder()

    private fun store(): ConfigStore =
        ConfigStore(tmp.newFolder("store")).also { it.ensureZones() }

    @Test
    fun `writeEvent creates a typed json file`() {
        val s = store()
        s.writeEvent("cc-open")
        assertTrue(s.eventFile("cc-open").exists())
        assertEquals("cc-open", s.readEvent("cc-open")?.type)
    }

    @Test
    fun `typed events do not collide`() {
        val s = store()
        s.writeEvent("cc-open")
        s.writeEvent("cc-close")
        assertEquals("cc-open", s.readEvent("cc-open")?.type)
        assertEquals("cc-close", s.readEvent("cc-close")?.type)
        assertTrue(s.eventFile("cc-open").exists())
        assertTrue(s.eventFile("cc-close").exists())
    }

    @Test
    fun `consumeEvent deletes the event file (ephemeral)`() {
        val s = store()
        s.writeEvent("cc-open")
        assertEquals("cc-open", s.consumeEvent("cc-open")?.type)
        assertNull(s.readEvent("cc-open"))
    }

    @Test
    fun `readEvent on missing type returns null`() {
        assertNull(store().readEvent("never-written"))
    }
}
