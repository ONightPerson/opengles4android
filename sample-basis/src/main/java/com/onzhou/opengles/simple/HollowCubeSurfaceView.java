package com.onzhou.opengles.simple;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HollowCubeSurfaceView extends GLSurfaceView {

    private final HollowCubeRenderer mRenderer;

    private float mPreviousX;
    private float mPreviousY;

    public HollowCubeSurfaceView(Context context) {
        this(context, null);
    }

    public HollowCubeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        mRenderer = new HollowCubeRenderer();
        setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreviousX = x;
                mPreviousY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                mRenderer.mAngleX += dy * 0.5f;
                mRenderer.mAngleY += dx * 0.5f;
                requestRender();

                mPreviousX = x;
                mPreviousY = y;
                break;
        }
        return true;
    }
}
