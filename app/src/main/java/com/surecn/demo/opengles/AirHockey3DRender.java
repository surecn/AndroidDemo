package com.surecn.demo.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.BuildConfig;
import com.surecn.demo.R;
import com.surecn.demo.utils.TextResourceReader;
import com.surecn.demo.opengles.utils.ShaderHelper;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockey3DRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 4;

    private static final int COLOR_COMPONENT_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final String A_COLOR = "a_Color";

    private static final String A_POSITION = "a_Position";

    private static final String U_MATRIX = "u_Matrix";

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private int aPositionLocation;

    private int aColorLocation;

    private int uMatrixLocation;

    private final FloatBuffer vertexData;

    private final float[] projectionMatrix = new float[16];

    private Context mContext;

    private int mProgram;

    public AirHockey3DRender(Context context) {
        mContext = context;
//        float[] tableVertices = {
//                // Triangle 1
//                -0.5f,    -0.5f,
//                 0.5f,     0.5f,
//                -0.5f,     0.5f,
//                // Triangle 2
//                -0.5f,    -0.5f,
//                 0.5f,    -0.5f,
//                 0.5f,     0.5f,
//                // Line 1
//                -0.5f,     0f,
//                 0.5f,     0f,
//                // Mallets
//                 0f,      -0.25f,
//                 0f,       0.25f
//        };
        float[] tableVertices = {
                // Triangle 1
                0f,         0f,     0f,     1.5f,     1f,     1f,     1f,
                -0.5f,     -0.8f,   0f,     1f,   0.7f,   0.7f,   0.7f,
                0.5f,      -0.8f,   0f,     1f,   0.7f,   0.7f,   0.7f,
                0.5f,       0.8f,   0f,     2f,   0.7f,   0.7f,   0.7f,
                -0.5f,      0.8f,   0f,     2f,   0.7f,   0.7f,   0.7f,
                -0.5f,     -0.8f,   0f,     1f,   0.7f,   0.7f,   0.7f,
//                // Triangle 2
//                -0.5f,    -0.5f,    1f,     0f,     0f,
//                0.5f,    -0.5f,     1f,     0f,     0f,
//                0.5f,     0.5f,     1f,     0f,     0f,
                // Line 1
                -0.5f,     0f,      0f,     1.5f,      1f,     0f,     0f,
                0.5f,      0f,      0f,     1.5f,       1f,     0f,     0f,
                // Mallets
                0f,      -0.25f,    0f,     1.25f,    0f,     0f,     1f,
                0f,       0.25f,    0f,     1.75f,    1f,     0f,     0f
        };

        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmengShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        mProgram = ShaderHelper.linkProgram(vertexShader, fragmengShader);

        if (BuildConfig.DEBUG) {
            ShaderHelper.validateProgram(mProgram);
        }

        glUseProgram(mProgram);

        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //将定点着色器的属性与java对象绑定
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //将定点着色器的属性与java对象绑定
        glEnableVertexAttribArray(aColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        final float aspectRatio = width > height ? (float)width / (float)height : (float)height / (float)width;
        if (width > height) {
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

//        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);
//
//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);
//
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
