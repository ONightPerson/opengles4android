package com.onzhou.opengles.simple

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.onzhou.opengles.shader.R
import com.onzhou.opengles.utils.ShaderReaderUtil
import com.onzhou.opengles.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by liubaozhu on 2026/6/15.
 */
class PointRenderer : GLSurfaceView.Renderer {

    companion object {
        private val VERTEX_POS = floatArrayOf(
            0.0f, 0.0f, 0.0f,
        )
        private val VERTEX_COLOR = floatArrayOf(
            1.0f, 0.0f, 0.0f, 1.0f,
        )
    }

    private val posBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(VERTEX_POS.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }
    private val colorBuffer: FloatBuffer by lazy {
        ByteBuffer.allocateDirect(VERTEX_COLOR.size * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
    }

    constructor() {
        posBuffer.put(VERTEX_POS)
        posBuffer.position(0)

        colorBuffer.put(VERTEX_COLOR)
        colorBuffer.position(0)
    }

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f)
        // 编译
        val vertexShaderId =
            ShaderUtils.compileVertexShader(ShaderReaderUtil.readResource(R.raw.vertex_point))
        val fragmentShaderId =
            ShaderUtils.compileFragmentShader(ShaderReaderUtil.readResource(R.raw.fragment_point))
        // 链接
        val programId = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)
        // 使用
        GLES30.glUseProgram(programId)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        //准备坐标数据
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, posBuffer)

        // 准备顶点颜色数据
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 0, colorBuffer)

        GLES30.glDrawArrays(GLES30.GL_POINTS, 0, 1)

        GLES30.glDisableVertexAttribArray(0)
        GLES30.glDisableVertexAttribArray(1)

    }
}