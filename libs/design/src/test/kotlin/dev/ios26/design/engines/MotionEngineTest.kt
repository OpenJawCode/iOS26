package dev.ios26.design.engines

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MotionEngineTest {

    @Test
    fun standardCurveIsCubicBezier() {
        val easing = MotionEngine.curveOf(listOf(0.4f, 0f, 0.2f, 1f))
        assertEquals(0f, easing.transform(0f), 0.001f)
        assertEquals(1f, easing.transform(1f), 0.001f)
        val mid = easing.transform(0.5f)
        assertTrue(mid in 0.1f..0.9f)
    }

    @Test
    fun bezierRequiresFourValues() {
        runCatching { MotionEngine.curveOf(listOf(0.4f, 0f)) }
            .onFailure { return }
        throw AssertionError("expected IllegalArgumentException")
    }

    @Test
    fun durationTokensResolve() {
        assertEquals(100, MotionEngine.duration("fast"))
        assertEquals(200, MotionEngine.duration("standard"))
    }

    @Test
    fun reducedMotionScalesDuration() {
        val normal = MotionEngine.tween("standard", "standard", reducedMotion = false)
        val reduced = MotionEngine.tween("standard", "standard", reducedMotion = true)
        assertTrue(normal.durationMillis > reduced.durationMillis)
    }

    @Test
    fun springTokensMapToComposeSprings() {
        val snappy = MotionEngine.spring("snappy")
        assertFalse(snappy.stiffness.isNaN())
    }
}

class HapticEngineTest {
    @Test
    fun effectMappingCoversAllTypes() {
        assertEquals(android.os.VibrationEffect.EFFECT_CLICK, HapticEngine.effectId("selection"))
        assertEquals(android.os.VibrationEffect.EFFECT_HEAVY_CLICK, HapticEngine.effectId("heavy"))
        assertEquals(android.os.VibrationEffect.EFFECT_DOUBLE_CLICK, HapticEngine.effectId("success"))
    }
}
