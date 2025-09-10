package com.malabarmatrix.slithersync

import com.malabarmatrix.slithersync.domain.SnakeEngine
import org.junit.Assert.assertTrue
import org.junit.Test

class SnakeEngineTest {
    @Test
    fun movesForwardOnSteps() {
        val engine = SnakeEngine(canvasWidth = 500, canvasHeight = 500, stepPixels = 10f)
        val before = engine.getState().segments.first().position
        engine.moveForwardBySteps(3)
        val after = engine.getState().segments.first().position
        val dx = after.x - before.x
        val dy = after.y - before.y
        assertTrue(kotlin.math.sqrt(dx*dx + dy*dy) >= 30f - 0.1f)
    }
}
