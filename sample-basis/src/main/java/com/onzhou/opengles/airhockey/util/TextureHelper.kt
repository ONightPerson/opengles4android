/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material,
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose.
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 */
package com.onzhou.opengles.airhockey.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y
import android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z
import android.opengl.GLUtils
import android.util.Log

object TextureHelper {
    private const val TAG = "TextureHelper"

    /**
     * Loads a texture from a resource ID, returning the OpenGL ID for that
     * texture. Returns 0 if the load failed.
     * 
     * @param context
     * @param resourceId
     * @return
     */
    @JvmStatic
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")
            }

            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(
            context.getResources(), resourceId, options
        )

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(
                    TAG, ("Resource ID " + resourceId
                            + " could not be decoded.")
                )
            }

            GLES20.glDeleteTextures(1, textureObjectIds, 0)

            return 0
        }


        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureObjectIds[0])

        // Set filtering: a default must be set, or the texture will be
        // black.
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR_MIPMAP_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        )
        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate texture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of texture coordinates,
        // and mipmap generation will work.
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.
        bitmap.recycle()

        // Unbind from the texture.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        return textureObjectIds[0]
    }

    /**
     * Loads a cubemap texture from the provided resources and returns the
     * texture ID. Returns 0 if the load failed.
     * 
     * @param context
     * @param cubeResources
     * An array of resources corresponding to the cube map. Should be
     * provided in this order: left, right, bottom, top, front, back.
     * @return
     */
    @JvmStatic
    fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
        val textureObjectIds = IntArray(1)
        GLES20.glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")
            }
            return 0
        }
        val options = BitmapFactory.Options()
        options.inScaled = false
        val cubeBitmaps = Array(6) { index ->
            BitmapFactory.decodeResource(context.resources, cubeResources[index], options)
        }
        // Linear filtering for minification and magnification
        GLES20.glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0])

        GLES20.glTexParameteri(GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, cubeBitmaps[0], 0)
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, cubeBitmaps[1], 0)

        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, cubeBitmaps[2], 0)
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, cubeBitmaps[3], 0)

        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, cubeBitmaps[4], 0)
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, cubeBitmaps[5], 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)

        for (bitmap in cubeBitmaps) {
            bitmap.recycle()
        }

        return textureObjectIds[0]
    }
}
