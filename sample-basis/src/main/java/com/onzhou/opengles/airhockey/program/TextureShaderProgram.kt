package com.onzhou.opengles.airhockey.program

import android.opengl.GLES30
import com.onzhou.opengles.shader.R

/**
 * Created by liubaozhu on 2026/6/25.
 */
class TextureShaderProgram : ShaderProgram {
    private var matrixLocation: Int = 0
    private var textureUnitLocation: Int = 0

    constructor() : super(R.raw.vertex_air_hockey_texture, R.raw.fragment_air_hockey_texture) {
        matrixLocation = GLES30.glGetUniformLocation(program, PROJECT_MATRIX)
        textureUnitLocation = GLES30.glGetUniformLocation(program, TEXTURE_UNIT)
    }

    fun setUniforms(matrix: FloatArray, textureId: Int) {
        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0)
        // 选槽位
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0)
        // 挂上纹理对象
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId)
        GLES30.glUniform1i(textureUnitLocation, 0)
    }

    override fun getPosLocation() = 0

    override fun getColorTexLocation() = 1
}