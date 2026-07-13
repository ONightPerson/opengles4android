/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.airhockey.objects

import android.opengl.GLES20
import com.onzhou.opengles.model.Circle
import com.onzhou.opengles.model.Cylinder
import com.onzhou.opengles.model.Point
import kotlin.math.cos
import kotlin.math.sin

internal class ObjectBuilder private constructor(sizeInVertices: Int) {
    private val vertexData: FloatArray = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private val drawList: MutableList<DrawCommand> = ArrayList<DrawCommand>()
    private var offset = 0

    internal interface DrawCommand {
        fun draw()
    }

    internal class GeneratedData(
        @JvmField val vertexData: FloatArray,
        @JvmField val drawList: MutableList<DrawCommand>
    )


    private fun appendCircle(circle: Circle, numPoints: Int) {
        val startVertex: Int = offset / FLOATS_PER_VERTEX
        val numVertices: Int = sizeOfCircleInVertices(numPoints)

        // 三角形扇的中心点
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (i in 0..numPoints) {
            val angleInRadians = ((i.toFloat() / numPoints) * (Math.PI * 2f))

            vertexData[offset++] = (circle.center.x + circle.radius * cos(angleInRadians).toFloat())
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = (circle.center.z + circle.radius * sin(angleInRadians).toFloat())
        }
        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }

    private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {
        val startVertex: Int = offset / FLOATS_PER_VERTEX
        val numVertices: Int = sizeOfOpenCylinderInVertices(numPoints)
        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (i in 0..numPoints) {
            val angleInRadians = ((i.toFloat() / numPoints) * (Math.PI.toFloat() * 2f))

            val xPosition: Float = cylinder.center.x + cylinder.radius * cos(angleInRadians)

            val zPosition: Float = cylinder.center.z + cylinder.radius * sin(angleInRadians)

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition

            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }
        drawList.add(object : DrawCommand {
            override fun draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

    companion object {
        private const val FLOATS_PER_VERTEX = 3

        @JvmStatic
        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
            val size: Int = (sizeOfCircleInVertices(numPoints)
                    + sizeOfOpenCylinderInVertices(numPoints))

            val builder = ObjectBuilder(size)

            val puckTop = Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius
            )

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)

            return builder.build()
        }

        @JvmStatic
        fun createMallet(
            center: Point, radius: Float, height: Float, numPoints: Int
        ): GeneratedData {
            val size: Int = (sizeOfCircleInVertices(numPoints) * 2
                    + sizeOfOpenCylinderInVertices(numPoints) * 2)

            val builder = ObjectBuilder(size)


            // First, generate the mallet base.
            val baseHeight = height * 0.25f

            val baseCircle = Circle(
                center.translateY(-baseHeight),
                radius
            )
            val baseCylinder = Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight
            )

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)


            // Now generate the mallet handle.
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f

            val handleCircle = Circle(
                center.translateY(height * 0.5f),
                handleRadius
            )
            val handleCylinder = Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight
            )

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)

            return builder.build()
        }

        private fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }
    }
}
