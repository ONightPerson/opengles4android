package com.onzhou.opengles.model

class Circle(@JvmField val center: Point, @JvmField val radius: Float) {
    fun scale(scale: Float): Circle {
        return Circle(center, radius * scale)
    }
}