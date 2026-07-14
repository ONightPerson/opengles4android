/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox.objects

import android.opengl.Matrix
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import java.util.Random

/** This class shoots particles in a particular direction.  */
class ParticleShooter(
    private val position: Point, direction: Vector, private val color: Int,
    private val angleVariance: Float, private val speedVariance: Float
) {
    private val random = Random()

    private val rotationMatrix = FloatArray(16)
    private val directionVector = FloatArray(4)
    private val resultVector = FloatArray(4)

    init {
        directionVector[0] = direction.x
        directionVector[1] = direction.y
        directionVector[2] = direction.z
    }

    fun addParticles(
        particleSystem: ParticleSystem, currentTime: Float,
        count: Int
    ) {
        for (i in 0 until count) {
            Matrix.setRotateEulerM(
                rotationMatrix, 0,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance,
                (random.nextFloat() - 0.5f) * angleVariance
            )

            Matrix.multiplyMV(
                resultVector, 0,
                rotationMatrix, 0,
                directionVector, 0
            )

            val speedAdjustment = 1f + random.nextFloat() * speedVariance

            val thisDirection = Vector(
                resultVector[0] * speedAdjustment,
                resultVector[1] * speedAdjustment,
                resultVector[2] * speedAdjustment
            )

            particleSystem.addParticle(position, color, thisDirection, currentTime)
        }
    }
}
