package com.onzhou.opengles.base

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
abstract class AbsGLSurfaceActivity : AbsBaseActivity() {
    protected lateinit var mGLSurfaceView: GLSurfaceView
    protected lateinit var renderer: GLSurfaceView.Renderer

    protected abstract fun bindRenderer(): GLSurfaceView.Renderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        mGLSurfaceView = GLSurfaceView(this)
        setContentView(mGLSurfaceView)
        //设置版本
        mGLSurfaceView.setEGLContextClientVersion(3)
        renderer = bindRenderer()
        mGLSurfaceView.setRenderer(renderer)
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView.onResume()
    }
}