package com.onzhou.opengles.simple

import android.opengl.GLES20.GL_LINES
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_TRIANGLES
import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glUniform4f
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.onzhou.opengles.shader.R
import com.onzhou.opengles.utils.ShaderReaderUtil.readResource
import com.onzhou.opengles.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by liubaozhu on 2026/6/23.
 */
class AirHockeyRender2 : GLSurfaceView.Renderer {

    companion object {
        private const val BYTES_PER_FLOAT = 4
        private const val POS_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE = (POS_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }

    private val vertexPoints: FloatArray = floatArrayOf(
        // table vertex
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
        0f, -0.5f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
        0.5f, 0f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
        0f, 0.5f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
        // line
        -0.5f, 0f, 1.0f, 0f, 0f,
        0.5f, 0f, 1.0f, 0f, 1f,
        // mallets
        0f, 0.25f, 0f, 1f, 1f,
        0f, -0.25f, 1f, 0f, 1f,
    )

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexPoints.size * BYTES_PER_FLOAT).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()

    constructor() {
        vertexBuffer.put(vertexPoints)
        vertexBuffer.position(0)
    }


    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {

        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)

        //编译
        val vertexShaderId =
            ShaderUtils.compileVertexShader(readResource(R.raw.vertex_air_hockey))
        val fragmentShaderId =
            ShaderUtils.compileFragmentShader(readResource(R.raw.fragment_air_hockey))

        // 链接程序片段
        val program = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId)

        // 使用程序片段
        GLES30.glUseProgram(program)

        GLES30.glVertexAttribPointer(
            0,
            POS_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            STRIDE,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(0)
        vertexBuffer.position(POS_COMPONENT_COUNT)
        GLES30.glVertexAttribPointer(
            1,
            COLOR_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            STRIDE,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(1)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GLES30.GL_COLOR_BUFFER_BIT)
        // 绘制桌面
        glDrawArrays(GL_TRIANGLE_FAN, 0, 10)
        // 绘制分割线
        glDrawArrays(GL_LINES, 10, 2)
        // 绘制木槌
        glDrawArrays(GL_POINTS, 12, 1)
        glDrawArrays(GL_POINTS, 13, 1)
    }
}