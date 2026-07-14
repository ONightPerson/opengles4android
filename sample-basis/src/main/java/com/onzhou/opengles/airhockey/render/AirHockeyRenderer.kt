/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.airhockey.render

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.opengl.Matrix.multiplyMV
import android.util.Log
import com.onzhou.opengles.airhockey.objects.Mallet
import com.onzhou.opengles.airhockey.objects.Puck
import com.onzhou.opengles.airhockey.objects.Table
import com.onzhou.opengles.airhockey.program.ColorShaderProgram
import com.onzhou.opengles.airhockey.program.TextureShaderProgram
import com.onzhou.opengles.airhockey.util.TextureHelper
import com.onzhou.opengles.model.Plane
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Ray
import com.onzhou.opengles.model.Sphere
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.model.intersectionPoint
import com.onzhou.opengles.model.intersects
import com.onzhou.opengles.model.vectorBetween
import com.onzhou.opengles.shader.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min


class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "AirHockeyRenderer"
        private const val LEFT_BOUND = -0.5f
        private const val RIGHT_BOUND = 0.5f
        private const val FAR_BOUND = -0.8f
        private const val NEAR_BOUND = 0.8f
    }
    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)



    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck
    private lateinit var blueMalletPosition: Point
    private lateinit var previousBlueMalletPosition: Point
    private lateinit var puckPosition: Point
    private lateinit var puckVector: Vector
    private var malletPressed = false

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f)
        Log.i(TAG, "onSurfaceCreated: blueMalletPosition: $blueMalletPosition")
        puckPosition = Point(0f, puck.height / 2f, 0f)
        puckVector = Vector(0f, 0f, 0f)

        textureProgram = TextureShaderProgram()
        colorProgram = ColorShaderProgram()

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        // Set the OpenGL viewport to fill the entire surface.
        GLES20.glViewport(0, 0, width, height)
        Matrix.perspectiveM(
            projectionMatrix,
            0,
            45f,
            width.toFloat() / height.toFloat(),
            1f,
            10f
        )
        Matrix.setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }


    override fun onDrawFrame(glUnused: GL10?) {
        // Clear the rendering surface.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        puckPosition = puckPosition.translate(puckVector)

        if (puckPosition.x < LEFT_BOUND + puck.radius
            || puckPosition.x > RIGHT_BOUND - puck.radius
        ) {
            puckVector = Vector(-puckVector.x, puckVector.y, puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }
        if (puckPosition.z < FAR_BOUND + puck.radius
            || puckPosition.z > NEAR_BOUND - puck.radius
        ) {
            puckVector = Vector(puckVector.x, puckVector.y, -puckVector.z)
            puckVector = puckVector.scale(0.9f)
        }


        // Clamp the puck position.
        puckPosition = Point(
            clamp(puckPosition.x, LEFT_BOUND + puck.radius, RIGHT_BOUND - puck.radius),
            puckPosition.y,
            clamp(puckPosition.z, FAR_BOUND + puck.radius, NEAR_BOUND - puck.radius)
        )


        // Friction factor
        puckVector = puckVector.scale(0.99f)

        // Multiply the view and projection matrices together.
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        Matrix.invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)
        // Draw the table.
        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw()

        // Draw the puck.
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        Matrix.multiplyMM(
            modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0
        )
    }

    // The mallets and the puck are positioned on the same plane as the table.
    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.multiplyMM(
            modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0
        )
    }

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        val ray: Ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

        // Now test if this ray intersects with the mallet by creating a
        // bounding sphere that wraps the mallet.
        val malletBoundingSphere = Sphere(
            Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
            ),
            mallet.height / 2f
        )

        // If the ray intersects (if the user touched a part of the screen that
        // intersects the mallet's bounding sphere), then set malletPressed =
        // true.
        malletPressed = intersects(malletBoundingSphere, ray)
    }

    private fun convertNormalized2DPointToRay(
        normalizedX: Float, normalizedY: Float
    ): Ray {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        multiplyMV(
            nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0
        )
        multiplyMV(
            farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0
        )

        // Why are we dividing by W? We multiplied our vector by an inverse
        // matrix, so the W value that we end up is actually the *inverse* of
        // what the projection matrix would create. By dividing all 3 components
        // by W, we effectively undo the hardware perspective divide.
        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        // We don't care about the W value anymore, because our points are now
        // in world coordinates.
        val nearPointRay: Point =
            Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])

        val farPointRay: Point =
            Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return Ray(
            nearPointRay,
            vectorBetween(nearPointRay, farPointRay)
        )
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if (malletPressed) {
            val ray: Ray = convertNormalized2DPointToRay(normalizedX, normalizedY)
            // Define a plane representing our air hockey table.
            val plane = Plane(Point(0f, 0f, 0f), Vector(0f, 1f, 0f))
            // Find out where the touched point intersects the plane
            // representing our table. We'll move the mallet along this plane.
            val touchedPoint: Point = intersectionPoint(ray, plane)

            // Clamp to bounds
            previousBlueMalletPosition = blueMalletPosition

            // Clamp to bounds
            blueMalletPosition = Point(
                clamp(
                    touchedPoint.x,
                    LEFT_BOUND + mallet.radius,
                    RIGHT_BOUND - mallet.radius
                ),
                mallet.height / 2f,
                clamp(
                    touchedPoint.z,
                    0f + mallet.radius,
                    NEAR_BOUND - mallet.radius
                )
            )


            // Now test if mallet has struck the puck.
            val distance: Float =
                vectorBetween(blueMalletPosition, puckPosition).length()

            if (distance < (puck.radius + mallet.radius)) {
                // The mallet has struck the puck. Now send the puck flying
                // based on the mallet velocity.
                puckVector = vectorBetween(
                    previousBlueMalletPosition, blueMalletPosition
                )
            }
        }
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return min(max, max(value, min))
    }
}