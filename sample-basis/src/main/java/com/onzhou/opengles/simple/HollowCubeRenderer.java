package com.onzhou.opengles.simple;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.onzhou.opengles.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class HollowCubeRenderer implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER =
            "#version 300 es\n" +
                    "layout (location = 0) in vec4 aPosition;\n" +
                    "uniform mat4 uMVPMatrix;\n" +
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * aPosition;\n" +
                    "}";

    private static final String FRAGMENT_SHADER =
            "#version 300 es\n" +
                    "precision mediump float;\n" +
                    "out vec4 fragColor;\n" +
                    "void main() {\n" +
                    "  fragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                    "}";

    private int mProgram;
    private int uMVPMatrixLocation;

    private FloatBuffer mVertexBuffer;
    private int mVertexCount;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    public volatile float mAngleX;
    public volatile float mAngleY;

    public HollowCubeRenderer() {
        initGeometry();
        Matrix.setIdentityM(mRotationMatrix, 0);
    }

    private void initGeometry() {
        List<Float> vertices = new ArrayList<>();

        // Cube edges
        float xmin = -20f, xmax = 20f;
        float ymin = -40f, ymax = 40f;
        float zmin = 0f, zmax = 50f;

        // 12 edges
        // Bottom (zmin)
        addEdge(vertices, xmin, ymin, zmin, xmax, ymin, zmin);
        addEdge(vertices, xmax, ymin, zmin, xmax, ymax, zmin);
        addEdge(vertices, xmax, ymax, zmin, xmin, ymax, zmin);
        addEdge(vertices, xmin, ymax, zmin, xmin, ymin, zmin);

        // Top (zmax)
        addEdge(vertices, xmin, ymin, zmax, xmax, ymin, zmax);
        addEdge(vertices, xmax, ymin, zmax, xmax, ymax, zmax);
        addEdge(vertices, xmax, ymax, zmax, xmin, ymax, zmax);
        addEdge(vertices, xmin, ymax, zmax, xmin, ymin, zmax);

        // Vertical
        addEdge(vertices, xmin, ymin, zmin, xmin, ymin, zmax);
        addEdge(vertices, xmax, ymin, zmin, xmax, ymin, zmax);
        addEdge(vertices, xmax, ymax, zmin, xmax, ymax, zmax);
        addEdge(vertices, xmin, ymax, zmin, xmin, ymax, zmax);

        // Ticks
        float tickLen = 1.0f;
        // X direction ticks
        for (float x = xmin; x <= xmax; x += 5f) {
            addEdge(vertices, x, ymin, zmin, x, ymin + tickLen, zmin);
            addEdge(vertices, x, ymax, zmin, x, ymax - tickLen, zmin);
            addEdge(vertices, x, ymin, zmax, x, ymin + tickLen, zmax);
            addEdge(vertices, x, ymax, zmax, x, ymax - tickLen, zmax);
        }
        // Y direction ticks
        for (float y = ymin; y <= ymax; y += 10f) {
            addEdge(vertices, xmin, y, zmin, xmin + tickLen, y, zmin);
            addEdge(vertices, xmax, y, zmin, xmax - tickLen, y, zmin);
            addEdge(vertices, xmin, y, zmax, xmin + tickLen, y, zmax);
            addEdge(vertices, xmax, y, zmax, xmax - tickLen, y, zmax);
        }
        // Z direction ticks
        for (float z = zmin; z <= zmax; z += 10f) {
            addEdge(vertices, xmin, ymin, z, xmin + tickLen, ymin, z);
            addEdge(vertices, xmax, ymin, z, xmax - tickLen, ymin, z);
            addEdge(vertices, xmin, ymax, z, xmin + tickLen, ymax, z);
            addEdge(vertices, xmax, ymax, z, xmax - tickLen, ymax, z);
        }

        mVertexCount = vertices.size() / 3;
        mVertexBuffer = ByteBuffer.allocateDirect(vertices.size() * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        for (Float v : vertices) {
            mVertexBuffer.put(v);
        }
        mVertexBuffer.position(0);
    }

    private void addEdge(List<Float> vertices, float x1, float y1, float z1, float x2, float y2, float z2) {
        vertices.add(x1);
        vertices.add(y1);
        vertices.add(z1);
        vertices.add(x2);
        vertices.add(y2);
        vertices.add(z2);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        mProgram = ShaderUtils.linkProgram(
                ShaderUtils.compileVertexShader(VERTEX_SHADER),
                ShaderUtils.compileFragmentShader(FRAGMENT_SHADER)
        );
        uMVPMatrixLocation = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        // Looking at the center (0, 0, 25) from distance.
        // We use a perspective projection to make it look 3D
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 2, 200);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        GLES30.glLineWidth(2.0f);

        GLES30.glUseProgram(mProgram);

        // View matrix: look at the center of the cube
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 120, 0, 0, 25, 0, 1, 0);

        // Model matrix: Rotate around the center (0, 0, 25)
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, 25f);
        Matrix.rotateM(mModelMatrix, 0, mAngleX, 1, 0, 0);
        Matrix.rotateM(mModelMatrix, 0, mAngleY, 0, 1, 0);
        Matrix.translateM(mModelMatrix, 0, 0, 0, -25f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES30.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mMVPMatrix, 0);

        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 0, mVertexBuffer);
        GLES30.glEnableVertexAttribArray(0);

        GLES30.glDrawArrays(GLES30.GL_LINES, 0, mVertexCount);
    }
}
