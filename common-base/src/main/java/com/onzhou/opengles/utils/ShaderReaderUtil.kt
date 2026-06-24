package com.onzhou.opengles.utils

import android.util.Log
import com.onzhou.opengles.core.AppCore

/**
 * Created by liubaozhu on 2026/6/23.
 */
object ShaderReaderUtil {
    private const val TAG = "ShaderReaderUtil"

    @JvmStatic
    fun readResource(resId: Int): String {
        val builder = StringBuilder()
        try {
            AppCore.getInstance().resources.openRawResource(resId).bufferedReader().use {
                var line: String?
                while (it.readLine().also { line = it } != null) {
                    builder.append(line).append('\n')
                }
            }
        } catch (e: Throwable) {
            Log.i(TAG, "readResource: $e")
        }
        return builder.toString()
    }
}