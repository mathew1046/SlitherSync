package com.malabarmatrix.slithersync.domain

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class SnakeEngine(
    private val canvasWidth: Int,
    private val canvasHeight: Int,
    initialLength: Int = 10,
    private val stepPixels: Float = 10f,
    private val headRadius: Float = 8f,
    private val borderMargin: Float = 10f,
) {
    private val random = Random(System.currentTimeMillis())

    private val points = ArrayDeque<Point>()
    private var pendingGrowth: Float = 0f

    private var headingRadians: Float = 0f

    private var currentFood: Point? = null
    private var score: Int = 0
    private var isGameOver: Boolean = false

    // --- Step smoothing ---
    private var stepBuffer = 0
    private val stepThreshold = 2 // require at least 2 valid detections before moving

    init {
        val startX = canvasWidth / 2f
        val startY = canvasHeight / 2f
        repeat(initialLength) { i ->
            points.addFirst(Point(startX + i * headRadius * 1.5f, startY))
        }
        spawnFood()
    }

    fun turnToDegrees(angleDegrees: Float) {
        headingRadians = (angleDegrees / 180f * PI).toFloat()
    }

    /**
     * Called when sensors detect possible steps.
     * Smooths false positives by buffering.
     */
    fun onStepDetected(rawSteps: Int) {
        if (isGameOver) return
        if (rawSteps <= 0) return

        stepBuffer += rawSteps
        if (stepBuffer >= stepThreshold) {
            moveForwardOneStep()
            stepBuffer = 0
        }
    }
    /**
     * Old API kept for compatibility.
     * Re-routes to step-detection logic.
     */
    fun moveForwardBySteps(stepDelta: Int) {
        onStepDetected(stepDelta)
    }

    private fun moveForwardOneStep() {
        for(i in 1..5)
            advance(stepPixels)
        checkCollisions()
    }

    /**
     * Used for continuous baseline movement (e.g., from touch joystick)
     */
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

        // Tail trimming considering growth
        var trimAmount = distance - pendingGrowth
        if (trimAmount <= 0f) {
            pendingGrowth = -trimAmount
            trimAmount = 0f
        } else {
            pendingGrowth = 0f
        }

        var remaining = trimAmount
        while (remaining > 0f && points.size > 1) {
            val last = points.last()
            val beforeLast = points.elementAt(points.size - 2)
            val segLen = distanceBetween(beforeLast, last)
            if (segLen <= remaining) {
                points.removeLast()
                remaining -= segLen
            } else {
                val t = (segLen - remaining) / segLen
                val nx = beforeLast.x * (1 - t) + last.x * t
                val ny = beforeLast.y * (1 - t) + last.y * t
                points.removeLast()
                points.addLast(Point(nx, ny))
                remaining = 0f
            }
        }
    }

    private fun checkCollisions() {
        val head = points.first()
        val minX = borderMargin
        val minY = borderMargin
        val maxX = canvasWidth - borderMargin
        val maxY = canvasHeight - borderMargin

        // walls
        if (head.x < minX || head.y < minY || head.x > maxX || head.y > maxY) {
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
            if (distanceBetween(head, f) < headRadius * 4) {
                score += 1
                pendingGrowth += headRadius * 12
                spawnFood()
            }
        }
    }

    private fun spawnFood() {
        val minX = (borderMargin + headRadius * 2).toInt()
        val maxX = (canvasWidth - borderMargin - headRadius * 2).toInt()
        val minY = (borderMargin + headRadius * 2).toInt()
        val maxY = (canvasHeight - borderMargin - headRadius * 2).toInt()
        currentFood = Point(
            x = random.nextInt(minX, maxX).toFloat(),
            y = random.nextInt(minY, maxY).toFloat()
        )
    }

    private fun distanceBetween(a: Point, b: Point): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}
