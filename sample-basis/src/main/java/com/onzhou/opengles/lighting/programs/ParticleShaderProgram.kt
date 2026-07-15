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

class ParticleShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.particle_vertex_shader,
    R.raw.particle_fragment_shader
) {
    // Uniform locations
    // Retrieve uniform locations for the shader program.
    private val uMatrixLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_MATRIX)
    private val uTimeLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_TIME)
    private val uTextureUnitLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_TEXTURE_UNIT)

    // Attribute locations


    // Retrieve attribute locations for the shader program.
    private val positionAttributeLocation: Int =
        GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_POSITION)
    private val colorAttributeLocation: Int =
        GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_COLOR)
    private val directionVectorAttributeLocation: Int =
        GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_DIRECTION_VECTOR)
    private val particleStartTimeAttributeLocation: Int =
        GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_PARTICLE_START_TIME)

    fun getPositionAttributeLocation(): Int {
        return positionAttributeLocation
    }

    fun getColorAttributeLocation(): Int {
        return colorAttributeLocation
    }

    fun getDirectionVectorAttributeLocation(): Int {
        return directionVectorAttributeLocation
    }

    fun getParticleStartTimeAttributeLocation(): Int {
        return particleStartTimeAttributeLocation
    }

    fun setUniforms(matrix: FloatArray?, elapsedTime: Float, textureId: Int) {
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        GLES20.glUniform1f(uTimeLocation, elapsedTime)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }
}
