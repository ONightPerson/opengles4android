/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.onzhou.opengles.airhockey.util.TextureHelper.loadCubeMap
import com.onzhou.opengles.airhockey.util.TextureHelper.loadTexture
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.shader.R
import com.onzhou.opengles.skybox.objects.ParticleShooter
import com.onzhou.opengles.skybox.objects.ParticleSystem
import com.onzhou.opengles.skybox.objects.Skybox
import com.onzhou.opengles.skybox.programs.ParticleShaderProgram
import com.onzhou.opengles.skybox.programs.SkyboxShaderProgram
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ParticlesRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    private var skyboxProgram: SkyboxShaderProgram? = null
    private var skybox: Skybox? = null

    private var particleProgram: ParticleShaderProgram? = null
    private var particleSystem: ParticleSystem? = null
    private var redParticleShooter: ParticleShooter? = null
    private var greenParticleShooter: ParticleShooter? = null
    private var blueParticleShooter: ParticleShooter? = null

    private var globalStartTime: Long = 0
    private var particleTexture = 0
    private var skyboxTexture = 0

    private var xRotation = 0f
    private var yRotation = 0f


    fun handleTouchDrag(deltaX: Float, deltaY: Float) {
        xRotation += deltaX / 16f
        yRotation += deltaY / 16f

        if (yRotation < -90) {
            yRotation = -90f
        } else if (yRotation > 90) {
            yRotation = 90f
        }
    }

    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        skyboxProgram = SkyboxShaderProgram(context)
        skybox = Skybox()

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection = Vector(0f, 0.5f, 0f)
        val angleVarianceInDegrees = 5f
        val speedVariance = 1f

        redParticleShooter = ParticleShooter(
            Point(-1f, 0f, 0f),
            particleDirection,
            Color.rgb(255, 50, 5),
            angleVarianceInDegrees,
            speedVariance
        )

        greenParticleShooter = ParticleShooter(
            Point(0f, 0f, 0f),
            particleDirection,
            Color.rgb(25, 255, 25),
            angleVarianceInDegrees,
            speedVariance
        )

        blueParticleShooter = ParticleShooter(
            Point(1f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255),
            angleVarianceInDegrees,
            speedVariance
        )

        particleTexture = loadTexture(context, R.drawable.particle_texture)

        skyboxTexture = loadCubeMap(
            context,
            intArrayOf(
                R.drawable.left, R.drawable.right,
                R.drawable.bottom, R.drawable.top,
                R.drawable.front, R.drawable.back
            )
        )
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        Matrix.perspectiveM(projectionMatrix, 0, 45f, width.toFloat() / height.toFloat(), 1f, 10f)
    }

    override fun onDrawFrame(glUnused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        drawSkybox()
        drawParticles()
    }

    private fun drawSkybox() {
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        skyboxProgram!!.useProgram()
        skyboxProgram!!.setUniforms(viewProjectionMatrix, skyboxTexture)
        skybox!!.bindData(skyboxProgram!!)
        skybox!!.draw()
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        greenParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)
        blueParticleShooter!!.addParticles(particleSystem!!, currentTime, 1)

        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram!!.useProgram()
        particleProgram!!.setUniforms(viewProjectionMatrix, currentTime, particleTexture)
        particleSystem!!.bindData(particleProgram!!)
        particleSystem!!.draw()

        GLES20.glDisable(GLES20.GL_BLEND)
    }
}