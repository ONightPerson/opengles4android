/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.heightmap.programs

import android.content.Context
import android.opengl.GLES20
import com.onzhou.opengles.shader.R

class HeightmapShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.heightmap_vertex_shader,
    R.raw.heightmap_fragment_shader
) {
    private val uMatrixLocation: Int = GLES20.glGetUniformLocation(program, U_MATRIX)
    val aPositionLocation: Int = GLES20.glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray?) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }
}
