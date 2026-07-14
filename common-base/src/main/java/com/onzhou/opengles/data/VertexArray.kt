package com.onzhou.opengles.data

import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES30
import com.onzhou.opengles.utils.Constants
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Created by liubaozhu on 2026/6/24.
 */
class VertexArray {
    private val buffer: FloatBuffer

    constructor(vertexes: FloatArray) {
        buffer = ByteBuffer.allocateDirect(vertexes.size * Constants.BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        buffer.put(vertexes)
    }

    fun setVertexPointer(offset: Int, location: Int, componentCount: Int, stride: Int) {
        buffer.position(offset)
        glVertexAttribPointer(
            location,
            componentCount,
            GLES30.GL_FLOAT,
            false,
            stride,
            buffer
        )
        glEnableVertexAttribArray(location)
        buffer.position(0)
    }

    fun updateBuffer(vertexData: FloatArray, start: Int, count: Int) {
        buffer.position(start)
        buffer.put(vertexData, start, count)
        buffer.position(0)
    }
}