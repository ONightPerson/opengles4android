/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.particles

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import com.onzhou.opengles.base.AbsGLSurfaceActivity
import com.onzhou.opengles.particles.render.ParticlesRenderer

class ParticlesActivity : AbsGLSurfaceActivity() {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private var glSurfaceView: GLSurfaceView? = null
    private var rendererSet = false

    override fun bindRenderer(): GLSurfaceView.Renderer {
        return ParticlesRenderer(this)
    }

    companion object {
        @JvmStatic
        fun intentStart(context: Context) {
            val intent = Intent(context, ParticlesActivity::class.java)
            context.startActivity(intent)
        }
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
}