package com.surecn.demo.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch07.objects.Mallet;
import com.surecn.demo.opengles.ch07.objects.Table;
import com.surecn.demo.opengles.ch07.ColorShaderProgram;
import com.surecn.demo.opengles.ch07.TextureShaderProgram;
import com.surecn.demo.opengles.utils.MatrixHelper;
import com.surecn.demo.opengles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int COLOR_COMPONENT_COUNT = 3;

    private static final int BYTES_PER_FLOAT = 4;

    private static final String A_COLOR = "a_Color";

    private static final String A_POSITION = "a_Position";

    private static final String U_MATRIX = "u_Matrix";

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private int aPositionLocation;

    private int aColorLocation;

    private int uMatrixLocation;

    //private final FloatBuffer vertexData;

    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Context mContext;

    private int mProgram;

    private Table table;

    private Mallet mallet;

    private TextureShaderProgram textureShaderProgram;

    private ColorShaderProgram colorShaderProgram;

    private int texture;

    public AirHockeyRender(Context context) {
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
                0f,         0f,     1f,     1f,     1f,
                -0.5f,     -0.8f,   0.7f,   0.7f,   0.7f,
                0.5f,      -0.8f,   0.7f,   0.7f,   0.7f,
                0.5f,       0.8f,   0.7f,   0.7f,   0.7f,
                -0.5f,      0.8f,   0.7f,   0.7f,   0.7f,
                -0.5f,     -0.8f,   0.7f,   0.7f,   0.7f,
//                // Triangle 2
//                -0.5f,    -0.5f,    1f,     0f,     0f,
//                0.5f,    -0.5f,     1f,     0f,     0f,
//                0.5f,     0.5f,     1f,     0f,     0f,
                // Line 1
                -0.5f,     0f,      1f,     0f,     0f,
                0.5f,     0f,       1f,     0f,     0f,
                // Mallets
                0f,      -0.25f,    0f,     0f,     1f,
                0f,       0.25f,    1f,     0f,     0f
        };

//        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
//                .order(ByteOrder.nativeOrder()).asFloatBuffer();
//        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

//        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_vertex_shader);
//        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.simple_fragment_shader);
//
//        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
//        int fragmengShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);
//
//        mProgram = ShaderHelper.linkProgram(vertexShader, fragmengShader);
//
//        if (BuildConfig.DEBUG) {
//            ShaderHelper.validateProgram(mProgram);
//        }
//
//        glUseProgram(mProgram);
//
//        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
//        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
//        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);
//
//        vertexData.position(0);
//        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
//        //将定点着色器的属性与java对象绑定
//        glEnableVertexAttribArray(aPositionLocation);
//
//        vertexData.position(POSITION_COMPONENT_COUNT);
//        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
//        //将定点着色器的属性与java对象绑定
//        glEnableVertexAttribArray(aColorLocation);

        table = new Table();
        mallet = new Mallet();

        textureShaderProgram = new TextureShaderProgram(mContext);
        colorShaderProgram = new ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
//        final float aspectRatio = width > height ? (float)width / (float)height : (float)height / (float)width;
//        if (width > height) {
//            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
//        } else {
//            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
//        }

        MatrixHelper.perspectiveM(projectionMatrix, 60, (float)width / (float)height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -4f);
        //rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
//
//        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
//
//        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
//
////        glUniform4f(uColorLocation, 1.0f, 0f, 0f, 1.0f);
//        glDrawArrays(GL_LINES, 6, 2);
////
////        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
//        glDrawArrays(GL_POINTS, 8, 1);
////
////        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
//        glDrawArrays(GL_POINTS, 9, 1);

        textureShaderProgram.useProgram();
        textureShaderProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureShaderProgram);
        table.draw();

        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();
    }
}
