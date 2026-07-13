package com.onzhou.opengles.airhockey.program

import android.opengl.GLES30
import com.onzhou.opengles.shader.R

/**
 * Created by liubaozhu on 2026/6/25.
 */
class ColorShaderProgram : ShaderProgram {
    private var matrixLocation: Int = 0
    private var uColorPos: Int

    constructor() : super(
        R.raw.vertex_air_hockey_with_better_mallets_color,
        R.raw.fragment_air_hockey_with_better_mallets_color
    ) {
        matrixLocation = GLES30.glGetUniformLocation(program, PROJECT_MATRIX)
        uColorPos = GLES30.glGetUniformLocation(program, U_COLOR)
    }

    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        GLES30.glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0)
        GLES30.glUniform4f(uColorPos, r, g, b, 1f)
    }

    override fun getPosLocation() = 0

    override fun getColorTexLocation() = 1
}