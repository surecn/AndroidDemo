package com.surecn.demo.view.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.view.opengles.utils.ShaderHelper;
import com.surecn.demo.utils.TextResourceReader;
import com.surecn.demo.utils.log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PER_FLOAT = 4;

    private static final String U_COLOR = "u_color";

    private static final String A_POSITION = "a_Position";

    private int aPositionLocation;

    private int uColorLocation;

    private final FloatBuffer vertexData;

    private Context mContext;

    private int mProgram;

    public AirHockeyRender(Context context) {
        log.e("======AirHockeyRender======");

        mContext = context;
//        float[] tableVertices = {
//                0f,  0f,
//                0f, 14f,
//                9f, 14f,
//                9f,  0f
//        };
        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f,    -0.5f,
                 0.5f,     0.5f,
                -0.5f,     0.5f,
                // Triangle 2
                -0.5f,    -0.5f,
                 0.5f,    -0.5f,
                 0.5f,     0.5f,
                // Line 1
                -0.5f,     0f,
                 0.5f,     0f,
                // Mallets
                 0f,      -0.25f,
                 0f,       0.25f
        };

        vertexData = ByteBuffer
                .allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        log.e("======onSurfaceCreated======");

        gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
//        //启用平滑着色
//        gl.glShadeModel(GL10.GL_SMOOTH);
//        //缓冲深度设置
//        gl.glClearDepthf(1.0f);
//
//        gl.glEnable(GL10.GL_DEPTH_TEST);
//
//        gl.glDepthFunc(GL10.GL_LEQUAL);
//
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
//
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (log.LOG_DEBUG) {
            ShaderHelper.validateProgram(mProgram);
        }
        glUseProgram(mProgram);

        uColorLocation = glGetUniformLocation(mProgram, U_COLOR);
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
//
//        gl.glMatrixMode(GL10.GL_PROJECTION);
//
//        gl.glLoadIdentity();
//
//        GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
//
//        gl.glMatrixMode(GL10.GL_MODELVIEW);
//
//        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        log.e("======onDrawFrame======");
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        //gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
