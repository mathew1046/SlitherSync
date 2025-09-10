package com.malabarmatrix.slithersync.domain

import kotlin.math.hypot

data class Point(val x: Float, val y: Float) {
    fun distanceTo(other: Point): Float = hypot(x - other.x, y - other.y)
}

data class SnakeSegment(val position: Point)

data class GameState(
    val segments: List<SnakeSegment>,
    val food: Point?,
    val score: Int,
    val isGameOver: Boolean,
    val isPaused: Boolean
)
