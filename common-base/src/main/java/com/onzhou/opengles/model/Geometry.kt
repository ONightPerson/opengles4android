package com.onzhou.opengles.model

import kotlin.math.sqrt

data class Point(val x: Float, val y: Float, val z: Float) {
    fun translateY(distance: Float) = Point(x, y + distance, z)
    fun translate(vector: Vector) = Point(x + vector.x, y + vector.y, z + vector.z)
}

data class Vector(val x: Float, val y: Float, val z: Float) {
    fun length() = sqrt(x * x + y * y + z * z)

    // 叉积
    fun crossProduct(other: Vector) = Vector(
        y * other.z - z * other.y,
        z * other.x - x * other.z,
        x * other.y - y * other.x
    )

    // 点积
    fun dotProduct(other: Vector) = x * other.x + y * other.y + z * other.z

    fun scale(f: Float) = Vector(x * f, y * f, z * f)

    fun normalize(): Vector {
        return scale(1f / length())
    }
}

data class Ray(val point: Point, val vector: Vector)

// ---------- 几何图形（用 data class，无需 sealed） ----------
data class Circle(val center: Point, val radius: Float) {
    fun scale(scale: Float) = Circle(center, radius * scale)
}

data class Cylinder(val center: Point, val radius: Float, val height: Float)

data class Sphere(val center: Point, val radius: Float)

data class Plane(val point: Point, val normal: Vector)

// ---------- 顶级工具函数（替代 Java 静态方法） ----------
fun vectorBetween(from: Point, to: Point) = Vector(to.x - from.x, to.y - from.y, to.z - from.z)

fun intersects(sphere: Sphere, ray: Ray): Boolean = distanceBetween(sphere.center, ray) < sphere.radius

// 点到射线的距离（射线视为无限延伸）
fun distanceBetween(point: Point, ray: Ray): Float {
    val p1ToPoint = vectorBetween(ray.point, point)
    val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)
    val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
    val lengthOfBase = ray.vector.length()
    return areaOfTriangleTimesTwo / lengthOfBase
}

// 射线与平面的交点（若无交点返回 NaN）
fun intersectionPoint(ray: Ray, plane: Plane): Point {
    val rayToPlaneVector = vectorBetween(ray.point, plane.point)
    val scaleFactor = rayToPlaneVector.dotProduct(plane.normal) / ray.vector.dotProduct(plane.normal)
    return ray.point.translate(ray.vector.scale(scaleFactor))
}