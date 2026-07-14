/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox.objects

import android.opengl.GLES20
import com.onzhou.opengles.data.VertexArray
import com.onzhou.opengles.skybox.programs.SkyboxShaderProgram
import java.nio.ByteBuffer

class Skybox {
    // Create a unit cube.
    private val vertexArray: VertexArray = VertexArray(
        floatArrayOf(
            -1f, 1f, 1f,  // (0) Top-left near
            1f, 1f, 1f,  // (1) Top-right near
            -1f, -1f, 1f,  // (2) Bottom-left near
            1f, -1f, 1f,  // (3) Bottom-right near
            -1f, 1f, -1f,  // (4) Top-left far
            1f, 1f, -1f,  // (5) Top-right far
            -1f, -1f, -1f,  // (6) Bottom-left far
            1f, -1f, -1f // (7) Bottom-right far
        )
    )


    // 6 indices per cube side
    private val indexArray: ByteBuffer = ByteBuffer.allocateDirect(6 * 6)
        .put(
            byteArrayOf( // Front
                1, 3, 0,
                0, 3, 2,  // Back

                4, 6, 5,
                5, 6, 7,  // Left

                0, 2, 4,
                4, 2, 6,  // Right

                5, 7, 1,
                1, 7, 3,  // Top

                5, 1, 4,
                4, 1, 0,  // Bottom

                6, 2, 7,
                7, 2, 3
            )
        )

    init {
        indexArray.position(0)
    }

    fun bindData(skyboxProgram: SkyboxShaderProgram) {
        vertexArray.setVertexPointer(
            0,
            skyboxProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0
        )
    }

    fun draw() {
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 36, GLES20.GL_UNSIGNED_BYTE, indexArray)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }
}