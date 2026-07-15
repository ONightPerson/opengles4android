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

class LightingShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.lighting_vertex_shader,
    R.raw.lighting_fragment_shader
) {
    private val uVectorToLightLocation: Int = GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_VECTOR_TO_LIGHT)
    private val uMVMatrixLocation: Int = GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_MV_MATRIX)
    private val uIT_MVMatrixLocation: Int = GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_IT_MV_MATRIX)
    private val uMVPMatrixLocation: Int = GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_MVP_MATRIX)
    private val uPointLightPositionsLocation: Int =
        GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_POINT_LIGHT_POSITIONS)
    private val uPointLightColorsLocation: Int = GLES20.glGetUniformLocation(program, ShaderProgram.Companion.U_POINT_LIGHT_COLORS)

    private val positionAttributeLocation: Int = GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_POSITION)
    private val normalAttributeLocation: Int = GLES20.glGetAttribLocation(program, ShaderProgram.Companion.A_NORMAL)

    fun positionAttributeLocation(): Int {
        return positionAttributeLocation
    }

    fun getNormalAttributeLocation() : Int {
        return normalAttributeLocation
    }

    /*
    public void setUniforms(float[] matrix, Vector vectorToLight) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);   
        glUniform3f(uVectorToLightLocation, 
            vectorToLight.x, vectorToLight.y, vectorToLight.z);
    }
     */
    fun setUniforms(
        mvMatrix: FloatArray?,
        it_mvMatrix: FloatArray?,
        mvpMatrix: FloatArray?,
        vectorToDirectionalLight: FloatArray?,
        pointLightPositions: FloatArray?,
        pointLightColors: FloatArray?
    ) {
        GLES20.glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uIT_MVMatrixLocation, 1, false, it_mvMatrix, 0)
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0)
        GLES20.glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0)

        GLES20.glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0)
        GLES20.glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0)
    }
}
