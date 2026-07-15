/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.lighting

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import androidx.core.content.res.ResourcesCompat
import com.onzhou.opengles.airhockey.util.TextureHelper
import com.onzhou.opengles.lighting.objects.Heightmap
import com.onzhou.opengles.lighting.objects.ParticleShooter
import com.onzhou.opengles.lighting.objects.ParticleSystem
import com.onzhou.opengles.lighting.objects.Skybox
import com.onzhou.opengles.lighting.programs.LightingShaderProgram
import com.onzhou.opengles.lighting.programs.ParticleShaderProgram
import com.onzhou.opengles.lighting.programs.SkyboxShaderProgram
import com.onzhou.opengles.model.Point
import com.onzhou.opengles.model.Vector
import com.onzhou.opengles.shader.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class ParticlesRenderer(private val context: Context) : GLSurfaceView.Renderer {
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewMatrixForSkybox = FloatArray(16)
    private val projectionMatrix = FloatArray(16)

    private val tempMatrix = FloatArray(16)
    private val modelViewMatrix = FloatArray(16)
    private val it_modelViewMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private lateinit var heightmapProgram: LightingShaderProgram
    private lateinit var heightmap: Heightmap

    /*
    private final Vector vectorToLight = new Vector(0.61f, 0.64f, -0.47f).normalize();
    */
    /*
    private final Vector vectorToLight = new Vector(0.30f, 0.35f, -0.89f).normalize();
    */
    val vectorToLight: FloatArray = floatArrayOf(0.30f, 0.35f, -0.89f, 0f)

    private val pointLightPositions = floatArrayOf(
        -1f, 1f, 0f, 1f,
        0f, 1f, 0f, 1f,
        1f, 1f, 0f, 1f
    )

    private val pointLightColors = floatArrayOf(
        1.00f, 0.20f, 0.02f,
        0.02f, 0.25f, 0.02f,
        0.02f, 0.20f, 1.00f
    )

    private lateinit var skyboxProgram: SkyboxShaderProgram
    private lateinit var skybox: Skybox

    private lateinit var particleProgram: ParticleShaderProgram
    private lateinit var particleSystem: ParticleSystem
    private lateinit var redParticleShooter: ParticleShooter
    private lateinit var greenParticleShooter: ParticleShooter
    private lateinit var blueParticleShooter: ParticleShooter

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


        // Setup view matrix
        updateViewMatrices()
    }

    private fun updateViewMatrices() {
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f)
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.size)


        // We want the translation to apply to the regular view matrix, and not
        // the skybox.
        Matrix.translateM(viewMatrix, 0, 0f, -1.5f, -5f)

        //        // This helps us figure out the vector for the sun or the moon.        
//        final float[] tempVec = {0f, 0f, -1f, 1f};
//        final float[] tempVec2 = new float[4];
//        
//        Matrix.multiplyMV(tempVec2, 0, viewMatrixForSkybox, 0, tempVec, 0);
//        Log.v("Testing", Arrays.toString(tempVec2));
    }

    override fun onSurfaceCreated(glUnused: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        heightmapProgram = LightingShaderProgram(context)
        heightmap = Heightmap(
            (ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.heightmap,
                null
            ) as BitmapDrawable).bitmap
        )

        skyboxProgram = SkyboxShaderProgram(context)
        skybox = Skybox()

        particleProgram = ParticleShaderProgram(context)
        particleSystem = ParticleSystem(10000)
        globalStartTime = System.nanoTime()

        val particleDirection: Vector = Vector(0f, 0.5f, 0f)
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

        particleTexture = TextureHelper.loadTexture(context, R.drawable.particle_texture)


//        skyboxTexture = TextureHelper.loadCubeMap(context, 
//            new int[] { R.drawable.left, R.drawable.right,
//                        R.drawable.bottom, R.drawable.top, 
//                        R.drawable.front, R.drawable.back}); 
        skyboxTexture = TextureHelper.loadCubeMap(
            context,
            intArrayOf(
                R.drawable.night_left, R.drawable.night_right,
                R.drawable.night_bottom, R.drawable.night_top,
                R.drawable.night_front, R.drawable.night_back
            )
        )
    }

    override fun onSurfaceChanged(glUnused: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        Matrix.perspectiveM(
            projectionMatrix,
            0,
            45f,
            width.toFloat() / height.toFloat(),
            1f,
            100f
        )
        updateViewMatrices()
    }

    override fun onDrawFrame(glUnused: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        drawHeightmap()
        drawSkybox()
        drawParticles()
    }

    private fun drawHeightmap() {
        Matrix.setIdentityM(modelMatrix, 0)


        // Expand the heightmap's dimensions, but don't expand the height as
        // much so that we don't get insanely tall mountains.        
        Matrix.scaleM(modelMatrix, 0, 100f, 10f, 100f)
        updateMvpMatrix()

        heightmapProgram.useProgram()

        /*
        heightmapProgram.setUniforms(modelViewProjectionMatrix, vectorToLight);
         */

        // Put the light positions into eye space.        
        val vectorToLightInEyeSpace = FloatArray(4)
        val pointPositionsInEyeSpace = FloatArray(12)
        Matrix.multiplyMV(vectorToLightInEyeSpace, 0, viewMatrix, 0, vectorToLight, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 0, viewMatrix, 0, pointLightPositions, 0)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 4, viewMatrix, 0, pointLightPositions, 4)
        Matrix.multiplyMV(pointPositionsInEyeSpace, 8, viewMatrix, 0, pointLightPositions, 8)

        heightmapProgram.setUniforms(
            modelViewMatrix, it_modelViewMatrix,
            modelViewProjectionMatrix, vectorToLightInEyeSpace,
            pointPositionsInEyeSpace, pointLightColors
        )
        heightmap.bindData(heightmapProgram)
        heightmap.draw()
    }

    private fun drawSkybox() {
        Matrix.setIdentityM(modelMatrix, 0)
        updateMvpMatrixForSkybox()

        GLES20.glDepthFunc(GLES20.GL_LEQUAL) // This avoids problems with the skybox itself getting clipped.
        skyboxProgram.useProgram()
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture)
        skybox.bindData(skyboxProgram)
        skybox.draw()
        GLES20.glDepthFunc(GLES20.GL_LESS)
    }

    private fun drawParticles() {
        val currentTime = (System.nanoTime() - globalStartTime) / 1000000000f

        redParticleShooter.addParticles(particleSystem, currentTime, 1)
        greenParticleShooter.addParticles(particleSystem, currentTime, 1)
        blueParticleShooter.addParticles(particleSystem, currentTime, 1)

        Matrix.setIdentityM(modelMatrix, 0)
        updateMvpMatrix()

        GLES20.glDepthMask(false)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE)

        particleProgram.useProgram()
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, particleTexture)
        particleSystem.bindData(particleProgram)
        particleSystem.draw()

        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glDepthMask(true)
    }

    /*
    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);        
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }
    */
    private fun updateMvpMatrix() {
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.invertM(tempMatrix, 0, modelViewMatrix, 0)
        Matrix.transposeM(it_modelViewMatrix, 0, tempMatrix, 0)
        Matrix.multiplyMM(
            modelViewProjectionMatrix, 0,
            projectionMatrix, 0,
            modelViewMatrix, 0
        )
    }

    private fun updateMvpMatrixForSkybox() {
        Matrix.multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0)
    }
}