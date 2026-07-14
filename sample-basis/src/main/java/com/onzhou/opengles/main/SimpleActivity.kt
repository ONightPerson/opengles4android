package com.onzhou.opengles.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.onzhou.opengles.airhockey.render.AirHockeyRenderer
import com.onzhou.opengles.base.AbsGLSurfaceActivity


class SimpleActivity : AbsGLSurfaceActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGLSurfaceView.setOnTouchListener { v, event ->
            val normalizedX = (event.x / v.width.toFloat()) * 2 - 1
            val normalizedY = -((event.y / v.height.toFloat()) * 2 - 1)

            if (event.action == MotionEvent.ACTION_DOWN) {
                mGLSurfaceView.queueEvent {
                    (renderer as AirHockeyRenderer).handleTouchPress(
                        normalizedX, normalizedY
                    )
                }
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                mGLSurfaceView.queueEvent {
                    (renderer as AirHockeyRenderer).handleTouchDrag(
                        normalizedX, normalizedY
                    )
                }
            }
            true
        }
    }

    override fun bindRenderer(): GLSurfaceView.Renderer {
        return AirHockeyRenderer(this)
    }

    companion object {
        @JvmStatic
        fun intentStart(context: Context) {
            val intent = Intent(context, SimpleActivity::class.java)
            context.startActivity(intent)
        }
    }
}
