package com.onzhou.opengles.model

class Point(@JvmField val x: Float, @JvmField val y: Float, @JvmField val z: Float) {
    fun translateY(distance: Float): Point {
        return Point(x, y + distance, z)
    }
}