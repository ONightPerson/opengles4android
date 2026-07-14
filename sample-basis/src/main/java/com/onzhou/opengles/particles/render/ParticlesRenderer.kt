package com.onzhou.opengles.particles.render

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.onzhou.opengles.airhockey.util.TextureHelper
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.particles.objects.ParticleShooter
import com.onzhou.opengles.particles.objects.ParticleSystem
import com.onzhou.opengles.particles.programs.ParticleShaderProgram
import com.onzhou.opengles.shader.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
class ParticlesRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)

    /*
    // Maximum saturation and value.
    private final float[] hsv = {0f, 1f, 1f};*/
    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter

    /*private ParticleFireworksExplosion particleFireworksExplosion;
    private Random random;*/
    private var globalStartTime: Long = 0
    private var texture = 0

    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)


        // Enable additive blending
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection = Vector(0f, 0.5f, 0f)

        val angleVarianceInDegrees = 5f
        val speedVariance = 1f


        /*
        redParticleShooter = new ParticleShooter(
            new Point(-1f, 0f, 0f),
            particleDirection,
            Color.rgb(255, 50, 5));

        greenParticleShooter = new ParticleShooter(
            new Point(0f, 0f, 0f),
            particleDirection,
            Color.rgb(25, 255, 25));

        blueParticleShooter = new ParticleShooter(
            new Point(1f, 0f, 0f),
            particleDirection,
            Color.rgb(5, 50, 255));
        */
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


        /*
        particleFireworksExplosion = new ParticleFireworksExplosion();

        random = new Random();  */
        texture = TextureHelper.loadTexture(context, R.drawable.particle_texture)
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        Matrix.perspectiveM(projectionMatrix, 0, 45f, width.toFloat() / height.toFloat(), 1f, 10f)

        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)
        Matrix.multiplyMM(
            viewProjectionMatrix, 0, projectionMatrix, 0,
            viewMatrix, 0
        )
    }

    override fun onDrawFrame(glUnused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter.addParticles(particleSystem, currentTime, 5)
        greenParticleShooter.addParticles(particleSystem, currentTime, 5)
        blueParticleShooter.addParticles(particleSystem, currentTime, 5)

        /*
        if (random.nextFloat() < 0.02f) {
            hsv[0] = random.nextInt(360);

            particleFireworksExplosion.addExplosion(
                particleSystem,
                new Vector(
                    -1f + random.nextFloat() * 2f,
                     3f + random.nextFloat() / 2f,
                    -1f + random.nextFloat() * 2f),
                Color.HSVToColor(hsv),
                globalStartTime);
        }    */
        particleProgram.useProgram()
        /*
        particleProgram.setUniforms(viewProjectionMatrix, currentTime);
         */
        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()
    }
}