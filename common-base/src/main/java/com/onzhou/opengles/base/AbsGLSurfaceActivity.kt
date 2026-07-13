package com.onzhou.opengles.base

import android.annotation.SuppressLint
import android.opengl.GLSurfaceView
import android.os.Bundle

/**
 * @anchor: andy
 * @date: 2018-11-02
 * @description:
 */
abstract class AbsGLSurfaceActivity : AbsBaseActivity() {
    private var mGLSurfaceView: GLSurfaceView? = null

    protected abstract fun bindRenderer(): GLSurfaceView.Renderer?

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        mGLSurfaceView = GLSurfaceView(this)
        setContentView(mGLSurfaceView)
        //设置版本
        mGLSurfaceView!!.setEGLContextClientVersion(3)
        val renderer = bindRenderer()
        mGLSurfaceView!!.setRenderer(renderer)
    }

    override fun onPause() {
        super.onPause()
        mGLSurfaceView!!.onPause()
    }

    override fun onResume() {
        super.onResume()
        mGLSurfaceView!!.onResume()
    }
}