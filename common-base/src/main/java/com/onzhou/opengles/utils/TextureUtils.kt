package com.onzhou.opengles.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

public class TextureUtils {

    private static final String TAG = "TextureUtils";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureIds = new int[1];
        // 向 GPU 驱动程序申请 1 个 未使用的纹理对象名称（整型 ID）
        GLES30.glGenTextures(1, textureIds, 0);
        if (textureIds[0] == 0) {
            Log.e(TAG, "Could not generate a new OpenGL textureId object.");
            return 0;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        if (bitmap == null) {
            Log.e(TAG, "Resource ID " + resourceId + " could not be decoded.");
            GLES30.glDeleteTextures(1, textureIds, 0);
            return 0;
        }
        // 绑定纹理到OpenGL
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureIds[0]);

        // GL_LINEAR_MIPMAP_LINEAR 三线性 Mipmap 过滤。GPU 会自动在相邻的两级 Mipmap 之间进行插值，消除 LOD 切换时的“接缝线”，效果最平滑、最抗锯齿
        // GL_LINEAR 双线性过滤。将周围四个相邻纹素进行加权平均，比最近邻采样（NEAREST）模糊，但没有像素块马赛克。
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR_MIPMAP_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);

        // 将bitmap对象拷贝到GPU显存中，绑定到当前激活的纹理对象上
        // level表示指定当前上传的图片数据属于 Mipmap 链中的哪一层，0 表示基础层级，即原始分辨率（最大、最清晰）的那张图
        // border 指定纹理的边框宽度 OpenGL ES 规范中，这个值必须永远为 0。这是早期 OpenGL 1.x 时代的遗留参数，当时允许给纹理加一圈特定颜色的边框来防止插值溢出
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);

        // 以当前绑定的 2D 纹理的第 0 级（原始图）为“母本”，自动计算并填充一系列逐级缩小的纹理图像（宽高依次减半），一直生成到 1x1 像素为止，并将这些数据全部存入显存中
        GLES30.glGenerateMipmap(GLES30.GL_TEXTURE_2D);

        // 数据如果已经被加载进OpenGL,则可以回收该bitmap
        bitmap.recycle();

        // 取消绑定纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);

        return textureIds[0];
    }


    public static int loadOESTexture() {
        int[] textureIds = new int[1];
        GLES20.glGenTextures(1, textureIds, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureIds[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return textureIds[0];
    }

}
