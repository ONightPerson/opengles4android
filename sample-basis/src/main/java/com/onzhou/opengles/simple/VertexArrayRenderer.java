package com.onzhou.opengles.simple;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import com.onzhou.opengles.shader.R;
import com.onzhou.opengles.utils.ShaderReaderUtil;
import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author : andy
 * @since : 2018-11-02
 * 顶点数组对象
 * 一句话总结依赖关系：VAO 依赖于 VBO（记录数据在哪），但 VBO 不依赖于 VAO（没有 VAO，数据也能被直接使用）
 * OpenGL ES 3.0 强制要求至少有一个 VAO 处于活动状态（默认 ID 为 0 也算）。只用 VBO”的写法，其实操作的是系统默认的 VAO（ID=0）
 * 这就是为什么它能跑起来
 */
public class VertexArrayRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "VertexBufferRenderer";

    private static final int VERTEX_POS_INDEX = 0;

    private final FloatBuffer vertexBuffer;

    private static final int VERTEX_POS_SIZE = 3;

    private static final int VERTEX_STRIDE = VERTEX_POS_SIZE * 4;

    private int mProgram;

    /**
     * 点的坐标
     */
    private float[] vertexPoints = new float[]{
            0.0f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f
    };

    /**
     * 缓冲数组
     */
    private int[] vaoIds = new int[1];

    private int[] vboIds = new int[1];

    public VertexArrayRenderer() {
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertexPoints);
        vertexBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景颜色
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        //编译
        final int vertexShaderId = ShaderUtils.compileVertexShader(ShaderReaderUtil.INSTANCE.readResource(R.raw.vertex_array_shader));
        final int fragmentShaderId = ShaderUtils.compileFragmentShader(ShaderReaderUtil.INSTANCE.readResource(R.raw.fragment_array_shader));
        //鏈接程序片段
        mProgram = ShaderUtils.linkProgram(vertexShaderId, fragmentShaderId);

        // 1. 生成 VAO ID（拣货单编号）
        GLES30.glGenVertexArrays(1, vaoIds, 0);

        // 2. 绑定 VAO（拿出这张拣货单，开始记录）
        GLES30.glBindVertexArray(vaoIds[0]);

        // 【重点】接下来的所有设置（bind, pointer, enable）都会被“记录”进这个 VAO！
        //1. 生成1个缓冲ID
        GLES30.glGenBuffers(1, vboIds, 0);
        //2. 向顶点坐标数据缓冲送入数据把顶点数组复制到缓冲中
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vboIds[0]);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexPoints.length * 4, vertexBuffer, GLES30.GL_STATIC_DRAW);

        //3. 将顶点位置数据送入渲染管线
        GLES30.glVertexAttribPointer(VERTEX_POS_INDEX, VERTEX_POS_SIZE, GLES30.GL_FLOAT, false, VERTEX_STRIDE, 0);
        //启用顶点位置属性
        GLES30.glEnableVertexAttribArray(VERTEX_POS_INDEX);

        //4. 解绑VAO
        GLES30.glBindVertexArray(0);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        //使用程序片段
        GLES30.glUseProgram(mProgram);

        //5. 绑定VAO
        GLES30.glBindVertexArray(vaoIds[0]);

        //6. 开始绘制三角形
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 3);

        //7. 解绑VAO
        GLES30.glBindVertexArray(0);

    }
}
