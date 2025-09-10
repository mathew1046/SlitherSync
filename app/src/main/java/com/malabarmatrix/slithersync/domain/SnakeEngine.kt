package com.malabarmatrix.slithersync.domain

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SnakeEngine(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    initialLength: Int = 10,
    private val stepPixels: Float = 10f,
    private val headRadius: Float = 8f,
) {
    private val random = Random(System.currentTimeMillis())

    private val points = ArrayDeque<Point>()
    private var pendingGrowth: Float = 0f

    private var headingRadians: Float = 0f

    private var currentFood: Point? = null
    private var score: Int = 0
    private var isGameOver: Boolean = false

    init {
        val startX = canvasWidth / 2f
        val startY = canvasHeight / 2f
        repeat(initialLength) { i ->
            points.addFirst(Point(startX - i * headRadius * 1.5f, startY))
        }
        spawnFood()
    }

    fun turnToDegrees(angleDegrees: Float) {
        headingRadians = (angleDegrees / 180f * PI).toFloat()
    }

    fun moveForwardBySteps(stepDelta: Int) {
        if (isGameOver) return
        val distance = stepDelta * stepPixels
        if (distance <= 0f) return
        advance(distance)
        checkCollisions()
    }

    fun tickBaseline(speedPixels: Float) {
        if (isGameOver) return
        if (speedPixels <= 0f) return
        advance(speedPixels)
        checkCollisions()
    }

    fun getState(): GameState = GameState(
        segments = points.map { SnakeSegment(it) },
        food = currentFood,
        score = score,
        isGameOver = isGameOver,
        isPaused = false
    )

    private fun advance(distance: Float) {
        val dx = cos(headingRadians) * distance
        val dy = sin(headingRadians) * distance
        val head = points.first()
        val newHead = Point(head.x + dx, head.y + dy)
        points.addFirst(newHead)

        var remaining = distance + pendingGrowth
        while (remaining > 0f && points.size > 1) {
            val last = points.last()
            val beforeLast = points.elementAt(points.size - 2)
            val segLen = distanceBetween(beforeLast, last)
            if (segLen <= remaining) {
                points.removeLast()
                remaining -= segLen
            } else {
                // shorten last segment proportionally
                val t = (segLen - remaining) / segLen
                val nx = beforeLast.x * (1 - t) + last.x * t
                val ny = beforeLast.y * (1 - t) + last.y * t
                points.removeLast()
                points.addLast(Point(nx, ny))
                remaining = 0f
            }
        }
        pendingGrowth = 0f
    }

    private fun checkCollisions() {
        val head = points.first()
        // walls
        if (head.x < 0 || head.y < 0 || head.x > canvasWidth || head.y > canvasHeight) {
            isGameOver = true
            return
        }
        // self collision
        for (i in 3 until points.size) { // skip immediate neighbors
            if (distanceBetween(head, points.elementAt(i)) < headRadius) {
                isGameOver = true
                return
            }
        }
        // food
        currentFood?.let { f ->
            if (distanceBetween(head, f) < headRadius * 2) {
                score += 1
                pendingGrowth += headRadius * 6
                spawnFood()
            }
        }
    }

    private fun spawnFood() {
        currentFood = Point(
            x = random.nextInt((headRadius * 2).toInt(), canvasWidth - (headRadius * 2).toInt()).toFloat(),
            y = random.nextInt((headRadius * 2).toInt(), canvasHeight - (headRadius * 2).toInt()).toFloat()
        )
    }

    private fun distanceBetween(a: Point, b: Point): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}
