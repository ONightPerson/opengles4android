/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.skybox

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast

class SkyBoxActivity : Activity() {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private var glSurfaceView: GLSurfaceView? = null
    private var rendererSet = false

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        glSurfaceView = GLSurfaceView(this)

        // Check if the system supports OpenGL ES 2.0.
        val activityManager =
            getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val supportsEs2: Boolean = isSupportsEs2(activityManager)

        val particlesRenderer = ParticlesRenderer(this)

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView!!.setEGLContextClientVersion(2)

            // Assign our renderer.
            glSurfaceView!!.setRenderer(particlesRenderer)
            rendererSet = true
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since 
             * we're not doing anything, the app will crash if the device 
             * doesn't support OpenGL ES 2.0. If we publish on the market, we 
             * should also add the following to AndroidManifest.xml:
             * 
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             * 
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(
                this, "This device does not support OpenGL ES 2.0.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        glSurfaceView!!.setOnTouchListener(object : OnTouchListener {
            var previousX: Float = 0f
            var previousY: Float = 0f

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        previousX = event.getX()
                        previousY = event.getY()
                    } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        val deltaX = event.getX() - previousX
                        val deltaY = event.getY() - previousY

                        previousX = event.getX()
                        previousY = event.getY()

                        glSurfaceView!!.queueEvent(object : Runnable {
                            override fun run() {
                                particlesRenderer.handleTouchDrag(
                                    deltaX, deltaY
                                )
                            }
                        })
                    }

                    return true
                } else {
                    return false
                }
            }
        })

        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()

        if (rendererSet) {
            glSurfaceView!!.onPause()
        }
    }

    override fun onResume() {
        super.onResume()

        if (rendererSet) {
            glSurfaceView!!.onResume()
        }
    }

    companion object {
        private fun isSupportsEs2(activityManager: ActivityManager): Boolean {
            val configurationInfo = activityManager
                .getDeviceConfigurationInfo()
            // Even though the latest emulator supports OpenGL ES 2.0,
            // it has a bug where it doesn't set the reqGlEsVersion so
            // the above check doesn't work. The below will detect if the
            // app is running on an emulator, and assume that it supports
            // OpenGL ES 2.0.
            return configurationInfo.reqGlEsVersion >= 0x20000 || Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith(
                "unknown"
            ) || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains(
                "Android SDK built for x86"
            )
        }

        fun intentStart(context: Context) {
            val intent = Intent(context, SkyBoxActivity::class.java)
            context.startActivity(intent)
        }
    }
}