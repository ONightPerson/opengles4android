package com.onzhou.opengles.airhockey.program

import android.opengl.GLES30
import com.onzhou.opengles.shader.R

/**
 * Created by liubaozhu on 2026/6/25.
 */
class ColorShaderProgram : ShaderProgram {
    private var matrixLocation: Int = 0

    constructor() : super(R.raw.vertex_air_hockey, R.raw.fragment_air_hockey) {
        matrixLocation = GLES30.glGetUniformLocation(program, PROJECT_MATRIX)
    }

    fun setUniforms(matrix: FloatArray) {
        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0)
    }

    override fun getPosLocation() = 0

    override fun getColorTexLocation() = 1
}