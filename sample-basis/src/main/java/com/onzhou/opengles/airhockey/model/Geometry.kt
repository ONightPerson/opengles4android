package com.onzhou.opengles.airhockey.model

/**
 * Created by liubaozhu on 2026/6/25.
 */
class Geometry {
    class Point(val x: Float, val y: Float, val z: Float) {
        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }
    }

    class Circle(val center: Point, val radius: Float) {
        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    class Cylinder(val center: Point, val radius: Float, val height: Float)
}