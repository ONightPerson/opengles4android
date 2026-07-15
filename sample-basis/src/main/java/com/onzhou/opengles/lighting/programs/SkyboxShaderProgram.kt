/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.lighting.programs

import android.content.Context
import android.opengl.GLES20
import com.onzhou.opengles.shader.R

class SkyboxShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.skybox_vertex_shader,
    R.raw.skybox_fragment_shader
) {
    private val uMatrixLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_MATRIX)
    private val uTextureUnitLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_TEXTURE_UNIT)
    private val positionAttributeLocation: Int =
        GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_POSITION)

    fun getPositionAttributeLocation(): Int {
        return positionAttributeLocation
    }

    fun setUniforms(matrix: FloatArray?, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }
}
