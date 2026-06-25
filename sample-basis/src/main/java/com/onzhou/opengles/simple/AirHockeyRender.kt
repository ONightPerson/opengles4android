package com.onzhou.opengles.simple

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import com.onzhou.opengles.shader.R
import com.onzhou.opengles.utils.ResReader.readResource
import com.onzhou.opengles.utils.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by liubaozhu on 2026/6/23.
 */
class AirHockeyRender : GLSurfaceView.Renderer {

    companion object {
        private const val BYTES_PER_FLOAT = 4
        private const val POS_COMPONENT_COUNT = 2
        private const val U_COLOR = "uColor"
    }

    private val vertexPoints: FloatArray = floatArrayOf(
        // table vertex
        -0.5f, -0.5f,
        0.5f, 0.5f,
        -0.5f, 0.5f,
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.5f, 0.5f,
        // line
        -0.5f, 0f,
        0.5f, 0f,
        // mallets
        0f, 0.25f,
        0f, -0.25f,
        // hockey
        0f, 0f,
        // 边框
        -0.5f, -0.55f,
        0.5f, -0.5f,
        -0.5f, -0.5f,
        -0.5f, -0.55f,
        0.5f, -0.55f,
        0.5f, -0.5f,

        -0.5f, 0.5f,
        0.5f, 0.55f,
        -0.5f, 0.55f,
        -0.5f, 0.5f,
        0.5f, 0.5f,
        0.5f, 0.55f,
    )

    private val vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexPoints.size * BYTES_PER_FLOAT).order(
            ByteOrder.nativeOrder()
        ).asFloatBuffer()

    private var uColorLocation = 0

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

        uColorLocation = GLES30.glGetUniformLocation(program, U_COLOR)

        GLES30.glVertexAttribPointer(
            0,
            POS_COMPONENT_COUNT,
            GLES30.GL_FLOAT,
            false,
            0,
            vertexBuffer
        )
        GLES30.glEnableVertexAttribArray(0)

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
        // 绘制桌面
        GLES30.glUniform4f(uColorLocation, 1f, 1f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6)
        // 绘制分割线
        GLES30.glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_LINES, 6, 2)
        // 绘制木槌
        GLES30.glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 8, 1)
        GLES30.glUniform4f(uColorLocation, 0f, 1f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 9, 1)
        // 绘制冰球
        GLES30.glUniform4f(uColorLocation, 1f, 1f, 0f, 1f)
        GLES30.glDrawArrays(GLES30.GL_POINTS, 10, 1)
        // 绘制边框
        GLES30.glUniform4f(uColorLocation, 0f, 1f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 11, 6)
        GLES30.glUniform4f(uColorLocation, 1f, 0.2f, 1f, 1f)
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 17, 6)
    }
}