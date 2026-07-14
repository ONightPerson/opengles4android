/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox.programs

import android.content.Context
import android.opengl.GLES20
import com.onzhou.opengles.airhockey.util.ShaderHelper
import com.onzhou.opengles.airhockey.util.TextResourceReader

abstract class ShaderProgram protected constructor(
    context: Context?, vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) {
    // Shader program
    // Compile the shaders and link the program.
    @JvmField
    protected val program: Int = ShaderHelper.buildProgram(
        TextResourceReader
            .readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader
            .readTextFileFromResource(context, fragmentShaderResourceId)
    )

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        GLES20.glUseProgram(program)
    }

    companion object {
        // Uniform constants
        const val U_MATRIX: String = "u_Matrix"
        const val U_COLOR: String = "u_Color"
        const val U_TEXTURE_UNIT: String = "u_TextureUnit"
        const val U_TIME: String = "u_Time"

        // Attribute constants
        const val A_POSITION: String = "a_Position"
        const val A_COLOR: String = "a_Color"
        const val A_TEXTURE_COORDINATES: String = "a_TextureCoordinates"
        const val A_DIRECTION_VECTOR: String = "a_DirectionVector"
        const val A_PARTICLE_START_TIME: String = "a_ParticleStartTime"
    }
}
