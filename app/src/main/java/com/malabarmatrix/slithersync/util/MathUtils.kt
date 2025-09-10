package com.malabarmatrix.slithersync.util

fun lowPassFilter(previous: Float, input: Float, alpha: Float): Float {
    val a = alpha.coerceIn(0f, 1f)
    return previous + a * (input - previous)
}
