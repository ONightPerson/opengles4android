/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox.objects

import android.graphics.Color
import android.opengl.GLES20
import com.onzhou.opengles.data.VertexArray
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.skybox.programs.ParticleShaderProgram
import com.onzhou.opengles.utils.Constants.BYTES_PER_FLOAT

class ParticleSystem(private val maxParticleCount: Int) {
    private val particles: FloatArray = FloatArray(maxParticleCount * TOTAL_COMPONENT_COUNT)
    private val vertexArray: VertexArray = VertexArray(particles)

    private var currentParticleCount = 0
    private var nextParticle = 0

    fun addParticle(
        position: Point, color: Int, direction: Vector,
        particleStartTime: Float
    ) {
        val particleOffset: Int = nextParticle * TOTAL_COMPONENT_COUNT
        var currentOffset = particleOffset
        nextParticle++

        if (currentParticleCount < maxParticleCount) {
            currentParticleCount++
        }

        if (nextParticle == maxParticleCount) {
            // Start over at the beginning, but keep currentParticleCount so
            // that all the other particles still get drawn.
            nextParticle = 0
        }

        particles[currentOffset++] = position.x
        particles[currentOffset++] = position.y
        particles[currentOffset++] = position.z

        particles[currentOffset++] = Color.red(color) / 255f
        particles[currentOffset++] = Color.green(color) / 255f
        particles[currentOffset++] = Color.blue(color) / 255f

        particles[currentOffset++] = direction.x
        particles[currentOffset++] = direction.y
        particles[currentOffset++] = direction.z

        particles[currentOffset] = particleStartTime

        vertexArray.updateBuffer(particles, particleOffset, TOTAL_COMPONENT_COUNT)
    }

    fun bindData(particleProgram: ParticleShaderProgram) {
        var dataOffset = 0
        vertexArray.setVertexPointer(
            dataOffset,
            particleProgram.positionAttributeLocation,
            POSITION_COMPONENT_COUNT, STRIDE
        )
        dataOffset += POSITION_COMPONENT_COUNT

        vertexArray.setVertexPointer(
            dataOffset,
            particleProgram.colorAttributeLocation,
            COLOR_COMPONENT_COUNT, STRIDE
        )
        dataOffset += COLOR_COMPONENT_COUNT

        vertexArray.setVertexPointer(
            dataOffset,
            particleProgram.directionVectorAttributeLocation,
            VECTOR_COMPONENT_COUNT, STRIDE
        )
        dataOffset += VECTOR_COMPONENT_COUNT

        vertexArray.setVertexPointer(
            dataOffset,
            particleProgram.particleStartTimeAttributeLocation,
            PARTICLE_START_TIME_COMPONENT_COUNT, STRIDE
        )
    }

    fun draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, currentParticleCount)
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
        private const val COLOR_COMPONENT_COUNT = 3
        private const val VECTOR_COMPONENT_COUNT = 3
        private const val PARTICLE_START_TIME_COMPONENT_COUNT = 1

        private const val TOTAL_COMPONENT_COUNT: Int = (POSITION_COMPONENT_COUNT
                + COLOR_COMPONENT_COUNT
                + VECTOR_COMPONENT_COUNT
                + PARTICLE_START_TIME_COMPONENT_COUNT)

        private const val STRIDE: Int = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT
    }
}
