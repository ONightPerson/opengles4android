package com.onzhou.opengles.sample

import android.os.Bundle
import android.view.View
import com.onzhou.opengles.base.AbsBaseActivity
import com.onzhou.opengles.main.CameraSurfaceActivity
import com.onzhou.opengles.main.ColorActivity
import com.onzhou.opengles.main.FilterActivity
import com.onzhou.opengles.main.HollowCubeActivity
import com.onzhou.opengles.main.NativeWindowActivity
import com.onzhou.opengles.main.SimpleActivity
import com.onzhou.opengles.main.TextureActivity
import com.onzhou.opengles.particles.ParticlesActivity
import com.onzhou.opengles.skybox.SkyBoxActivity

/**
 * @anchor: andy
 * @date: 2019-03-27
 * @description:
 */
class SampleActivity : AbsBaseActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengles_sample)
    }

    fun onBasisClick(view: View?) {
        SimpleActivity.intentStart(this)
    }

    fun onHollowCubeClick(view: View?) {
        HollowCubeActivity.intentStart(this)
    }

    fun onParticlesClick(view: View?) {
        ParticlesActivity.intentStart(this)
    }

    fun onSkyboxClick(view: View?) {
        SkyBoxActivity.intentStart(this)
    }

    fun onColorClick(view: View?) {
        ColorActivity.intentStart(this)
    }

    /**
     * 基于JNI实现的OpenGLES相关操作
     * 
     * @param view
     */
    fun onNativeClick(view: View?) {
        NativeWindowActivity.intentStart(this)
    }

    /**
     * 图片纹理处理
     * 
     * @param view
     */
    fun onTextureClick(view: View?) {
        TextureActivity.intentStart(this)
    }

    /**
     * 黑白相机实现
     * 
     * @param view
     */
    fun onCameraClick(view: View?) {
        CameraSurfaceActivity.intentStart(this)
    }

    /**
     * 滤镜实现
     * 
     * @param view
     */
    fun onFilterClick(view: View?) {
        FilterActivity.intentStart(this)
    }
}
