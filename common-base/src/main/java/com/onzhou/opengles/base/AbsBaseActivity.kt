package com.onzhou.opengles.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Created by liubaozhu on 2026/6/22.
 */
open class AbsBaseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            hideBar(BarHide.FLAG_HIDE_BAR)
        }
    }
}