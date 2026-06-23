package com.onzhou.opengles.utils;

import android.opengl.GLES30;
import android.util.Log;

/**
 * gl + 操作名 + [对象类型] + 后缀
 */
public class ShaderUtils {

    private static final String TAG = "ShaderUtils";

    /**
     * 编译顶点着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileVertexShader(String shaderCode) {
        return compileShader(GLES30.GL_VERTEX_SHADER, shaderCode);
    }

    /**
     * 编译片段着色器
     *
     * @param shaderCode
     * @return
     */
    public static int compileFragmentShader(String shaderCode) {
        return compileShader(GLES30.GL_FRAGMENT_SHADER, shaderCode);
    }

    /**
     * 编译
     *
     * @param type       顶点着色器:GLES30.GL_VERTEX_SHADER
     *                   片段着色器:GLES30.GL_FRAGMENT_SHADER
     * @param shaderCode
     * @return
     */
    private static int compileShader(int type, String shaderCode) {
        // 创建一个着色器
        final int shaderId = GLES30.glCreateShader(type);
        if (shaderId != 0) {
            // 上传源码，告诉OpenGL读入该源码，且与着色器对象关联
            GLES30.glShaderSource(shaderId, shaderCode);
            // 编译该源码
            GLES30.glCompileShader(shaderId);
            //检测状态
            final int[] compileStatus = new int[1];
            // glGetShaderiv iv表示整型向量
            GLES30.glGetShaderiv(shaderId, GLES30.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                String logInfo = GLES30.glGetShaderInfoLog(shaderId);
                Log.i(TAG, "compileShader fail: " + logInfo);
                //创建失败
                GLES30.glDeleteShader(shaderId);
                return 0;
            }
            return shaderId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 链接小程序
     *
     * @param vertexShaderId   顶点着色器
     * @param fragmentShaderId 片段着色器
     * @return
     */
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        if (programId != 0) {
            //将顶点着色器加入到程序
            GLES30.glAttachShader(programId, vertexShaderId);
            //将片元着色器加入到程序中
            GLES30.glAttachShader(programId, fragmentShaderId);
            // 链接着色器程序
            GLES30.glLinkProgram(programId);
            final int[] linkStatus = new int[1];

            GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                String logInfo = GLES30.glGetProgramInfoLog(programId);
                System.err.println(logInfo);
                GLES30.glDeleteProgram(programId);
                return 0;
            }
            return programId;
        } else {
            //创建失败
            return 0;
        }
    }

    /**
     * 验证程序片段是否有效
     *
     * @param programObjectId
     * @return
     */
    public static boolean validProgram(int programObjectId) {
        GLES30.glValidateProgram(programObjectId);
        final int[] programStatus = new int[1];
        GLES30.glGetProgramiv(programObjectId, GLES30.GL_VALIDATE_STATUS, programStatus, 0);
        return programStatus[0] != 0;
    }

}
