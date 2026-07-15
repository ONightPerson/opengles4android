/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.lighting.objects

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import com.onzhou.opengles.data.IndexBuffer
import com.onzhou.opengles.data.VertexBuffer
import com.onzhou.opengles.lighting.programs.LightingShaderProgram
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.model.vectorBetween
import com.onzhou.opengles.utils.Constants.BYTES_PER_FLOAT
import kotlin.math.max
import kotlin.math.min

class Heightmap(bitmap: Bitmap) {
    private val width: Int
    private val height: Int
    private val numElements: Int

    private val vertexBuffer: VertexBuffer
    private val indexBuffer: IndexBuffer

    init {
        width = bitmap.getWidth()
        height = bitmap.getHeight()

        if (width * height > 65536) {
            throw RuntimeException("Heightmap is too large for the index buffer.")
        }

        numElements = calculateNumElements()
        vertexBuffer = VertexBuffer(loadBitmapData(bitmap))
        indexBuffer = IndexBuffer(createIndexData())
    }

    /**
     * Copy the heightmap data into a vertex buffer object.
     */
    private fun loadBitmapData(bitmap: Bitmap): FloatArray {
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        bitmap.recycle()

        val heightmapVertices =
            FloatArray(width * height * TOTAL_COMPONENT_COUNT)

        var offset = 0

        for (row in 0 until height) {
            for (col in 0 until width) {
                // The heightmap will lie flat on the XZ plane and centered
                // around (0, 0), with the bitmap width mapped to X and the
                // bitmap height mapped to Z, and Y representing the height. We
                // assume the heightmap is grayscale, and use the value of the
                // red color to determine the height.
                val point: Point = getPoint(pixels, row, col)

                heightmapVertices[offset++] = point.x
                heightmapVertices[offset++] = point.y
                heightmapVertices[offset++] = point.z

                val top: Point = getPoint(pixels, row - 1, col)
                val left: Point = getPoint(pixels, row, col - 1)
                val right: Point = getPoint(pixels, row, col + 1)
                val bottom: Point = getPoint(pixels, row + 1, col)

                val rightToLeft: Vector = vectorBetween(right, left)
                val topToBottom: Vector = vectorBetween(top, bottom)
                val normal: Vector = rightToLeft.crossProduct(topToBottom).normalize()

                heightmapVertices[offset++] = normal.x
                heightmapVertices[offset++] = normal.y
                heightmapVertices[offset++] = normal.z
            }
        }

        return heightmapVertices
    }

    /**
     * Returns a point at the expected position given by row and col, but if the
     * position is out of bounds, then it clamps the position and uses the
     * clamped position to read the height. For example, calling with row = -1
     * and col = 5 will set the position as if the point really was at -1 and 5,
     * but the height will be set to the heightmap height at (0, 5), since (-1,
     * 5) is out of bounds. This is useful when we're generating normals, and we
     * need to read the heights of neighbouring points.
     */
    private fun getPoint(pixels: IntArray, row: Int, col: Int): Point {
        var row = row
        var col = col
        val x = (col.toFloat() / (width - 1).toFloat()) - 0.5f
        val z = (row.toFloat() / (height - 1).toFloat()) - 0.5f

        row = clamp(row, 0, width - 1)
        col = clamp(col, 0, height - 1)

        val y = Color.red(pixels[(row * height) + col]).toFloat() / 255f

        return Point(x, y, z)
    }

    private fun clamp(`val`: Int, min: Int, max: Int): Int {
        return max(min, min(max, `val`))
    }

    private fun calculateNumElements(): Int {
        // There should be 2 triangles for every group of 4 vertices, so a
        // heightmap of, say, 10x10 pixels would have 9x9 groups, with 2
        // triangles per group and 3 vertices per triangle for a total of (9 x 9
        // x 2 x 3) indices.
        return (width - 1) * (height - 1) * 2 * 3
    }

    /**
     * Create an index buffer object for the vertices to wrap them together into
     * triangles, creating indices based on the width and height of the
     * heightmap.
     */
    private fun createIndexData(): ShortArray {
        val indexData = ShortArray(numElements)
        var offset = 0

        for (row in 0 until height - 1) {
            for (col in 0 until width - 1) {
                // Note: The (short) cast will end up underflowing the number
                // into the negative range if it doesn't fit, which gives us the
                // right unsigned number for OpenGL due to two's complement.
                // This will work so long as the heightmap contains 65536 pixels
                // or less.
                val topLeftIndexNum = (row * width + col).toShort()
                val topRightIndexNum = (row * width + col + 1).toShort()
                val bottomLeftIndexNum = ((row + 1) * width + col).toShort()
                val bottomRightIndexNum = ((row + 1) * width + col + 1).toShort()


                // Write out two triangles.
                indexData[offset++] = topLeftIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = topRightIndexNum

                indexData[offset++] = topRightIndexNum
                indexData[offset++] = bottomLeftIndexNum
                indexData[offset++] = bottomRightIndexNum
            }
        }

        return indexData
    }

    fun bindData(heightmapProgram: LightingShaderProgram) {
        vertexBuffer.setVertexAttribPointer(
            0,
            heightmapProgram.positionAttributeLocation(),
            POSITION_COMPONENT_COUNT, STRIDE
        )

        vertexBuffer.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT,
            heightmapProgram.getNormalAttributeLocation(),
            NORMAL_COMPONENT_COUNT, STRIDE
        )
    }

    fun draw() {
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numElements, GLES20.GL_UNSIGNED_SHORT, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val NORMAL_COMPONENT_COUNT = 3
        private val TOTAL_COMPONENT_COUNT: Int = POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT
        private val STRIDE: Int =
            (POSITION_COMPONENT_COUNT + NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }
}
