package com.onzhou.opengles.airhockey.program

import android.opengl.GLES20.glUseProgram
import com.onzhou.opengles.utils.ResReader
import com.onzhou.opengles.utils.ShaderUtils

/**
 * Created by liubaozhu on 2026/6/25.
 */
abstract class ShaderProgram {
    companion object {
        // uniform constants
        const val PROJECT_MATRIX = "matrix"
        const val TEXTURE_UNIT = "textureUnit"
        const val U_COLOR = "uColor"
    }

    protected val program: Int

    constructor(vertexShaderResId: Int, fragmentShaderResId: Int) {
        val vertexShaderCode = ResReader.readResource(vertexShaderResId)
        val fragmentShaderCode = ResReader.readResource(fragmentShaderResId)
        with(ShaderUtils) {
            program = linkProgram(
                compileVertexShader(vertexShaderCode),
                compileFragmentShader(fragmentShaderCode)
            )
        }
    }

    fun useProgram() {
        glUseProgram(program)
    }

    /**
     * 获取着色器程序顶点位置
     */
    abstract fun getPosLocation(): Int

    /**
     * 获取着色器程序颜色或纹理位置
     */
    abstract fun getColorTexLocation(): Int
}