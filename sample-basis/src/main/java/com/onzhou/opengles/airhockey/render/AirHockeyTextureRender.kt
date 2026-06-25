package com.onzhou.opengles.airhockey.render

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.onzhou.opengles.airhockey.model.Mallet
import com.onzhou.opengles.airhockey.model.Table
import com.onzhou.opengles.airhockey.program.ColorShaderProgram
import com.onzhou.opengles.airhockey.program.TextureShaderProgram
import com.onzhou.opengles.core.AppCore
import com.onzhou.opengles.shader.R
import com.onzhou.opengles.utils.ResReader
import com.onzhou.opengles.utils.ShaderUtils
import com.onzhou.opengles.utils.TextureUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by liubaozhu on 2026/6/23.
 */
class AirHockeyTextureRender : GLSurfaceView.Renderer {
    private var projectionM = FloatArray(16)
    private var modelM = FloatArray(16)
    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {

        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f)

        table = Table()
        mallet = Mallet()
        textureProgram = TextureShaderProgram()
        colorProgram = ColorShaderProgram()
        texture =
            TextureUtils.loadTexture(AppCore.getInstance().context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        GLES20.glViewport(0, 0, width, height)
        Matrix.perspectiveM(projectionM, 0, 40f, width.toFloat() / height, 1f, 10f)
        Matrix.setIdentityM(modelM, 0)
        Matrix.translateM(modelM, 0, 0f, 0f, -3f)
        Matrix.rotateM(modelM, 0, -60f, 1f, 0f, 0f)

        val temp = FloatArray(16)
        Matrix.multiplyMM(temp, 0, projectionM, 0, modelM, 0)
        System.arraycopy(temp, 0, projectionM, 0, projectionM.size)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        textureProgram.run {
            useProgram()
            setUniforms(projectionM, texture)
            table.bindData(this)
            table.draw()
        }
        colorProgram.run {
            useProgram()
            setUniforms(projectionM)
            mallet.bindData(this)
            mallet.draw()
        }
    }
}