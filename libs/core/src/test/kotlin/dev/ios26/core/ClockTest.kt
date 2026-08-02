package dev.ios26.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ClockTest {
    @Test
    fun systemClockTracksTime() {
        val before = System.currentTimeMillis()
        val now = SystemClock.nowMillis()
        assertTrue(now >= before)
    }

    @Test
    fun clockIsInjectable() {
        val fake = Clock { 42L }
        assertEquals(42L, fake.nowMillis())
    }
}
