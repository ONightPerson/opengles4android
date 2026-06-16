package com.onzhou.opengles.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.onzhou.opengles.base.AbsBaseActivity;
import com.onzhou.opengles.simple.HollowCubeSurfaceView;

public class HollowCubeActivity extends AbsBaseActivity {

    public static void intentStart(Context context) {
        Intent intent = new Intent(context, HollowCubeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HollowCubeSurfaceView surfaceView = new HollowCubeSurfaceView(this);
        setContentView(surfaceView);
    }
}
