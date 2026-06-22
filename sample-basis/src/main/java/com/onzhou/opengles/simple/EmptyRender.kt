package com.onzhou.opengles.simple

import android.opengl.GLES20.glClear
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by liubaozhu on 2026/6/22.
 */
class EmptyRender : GLSurfaceView.Renderer {
    companion object {
        private const val TAG = "EmptyRender"
    }
    override fun onSurfaceCreated(
        gl: GL10?,
        config: EGLConfig?
    ) {
        Log.i(TAG, "onSurfaceCreated: gl: $gl, config: $config")
        GLES30.glClearColor(1f, 0f, 0f, 1f);
    }

    override fun onSurfaceChanged(
        gl: GL10?,
        width: Int,
        height: Int
    ) {
        Log.i(TAG, "onSurfaceChanged: width: $width, height: $height")
        GLES30.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.i(TAG, "onDrawFrame: gl: $gl")
        // 显卡会将颜色缓冲区里的所有像素点统一重置为你通过 glClearColor(r, g, b, a) 预先设置好的那个颜色
        glClear(GLES30.GL_COLOR_BUFFER_BIT)
    }
}