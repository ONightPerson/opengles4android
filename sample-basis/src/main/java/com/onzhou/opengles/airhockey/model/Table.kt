package com.onzhou.opengles.airhockey.model

import android.opengl.GLES30
import com.onzhou.opengles.airhockey.program.ShaderProgram
import com.onzhou.opengles.data.VertexArray
import com.onzhou.opengles.utils.Constants

/**
 * Created by liubaozhu on 2026/6/24.
 */
class Table {
    companion object {
        private const val POS_COMPONENT_COUNT = 2
        private const val TEXTURE_COMPONENT_COUNT = 2
        private const val STRIDE =
            (POS_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT) * Constants.BYTES_PER_FLOAT

        private val VERTEX_DATA = floatArrayOf(
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f,
        )
    }

    private val vertexArray: VertexArray by lazy { VertexArray(VERTEX_DATA) }

    fun bindData(program: ShaderProgram) {
        vertexArray.setVertexPointer(0, program.getPosLocation(), POS_COMPONENT_COUNT, STRIDE)
        vertexArray.setVertexPointer(
            POS_COMPONENT_COUNT,
            program.getColorTexLocation(),
            TEXTURE_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_FAN, 0, 6)
    }
}