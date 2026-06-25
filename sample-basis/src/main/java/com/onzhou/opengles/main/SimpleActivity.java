package com.onzhou.opengles.main;

import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;

import com.onzhou.opengles.airhockey.render.AirHockeyTextureRender;
import com.onzhou.opengles.base.AbsGLSurfaceActivity;

public class SimpleActivity extends AbsGLSurfaceActivity {

    public static void intentStart(Context context) {
        Intent intent = new Intent(context, SimpleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected GLSurfaceView.Renderer bindRenderer() {
        return new AirHockeyTextureRender();
    }
}
