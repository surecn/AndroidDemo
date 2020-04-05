package com.surecn.demo.opengles.ch05;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.BuildConfig;
import com.surecn.demo.R;
import com.surecn.demo.opengles.utils.ShaderHelper;
import com.surecn.demo.utils.TextResourceReader;

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
import static android.opengl.GLES20.glUniform4fv;
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
public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;//每个顶点的向量素

    private static final int COLOR_COMPONENT_COUNT = 3;//每个颜色的向量素

    private static final int BYTES_PER_FLOAT = 4;

    private static final String A_COLOR = "a_Color";

    private static final String A_POSITION = "a_Position";

    private static final String U_MATRIX = "u_Matrix";

    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private int aPositionLocation;

    private int aColorLocation;

    private int uMatrixLocation;

    private final float[] projectionMatrix = new float[16];

    private final FloatBuffer vertexData;

    private Context mContext;

    private int mProgram;

    public AirHockeyRender(Context context) {
        mContext = context;
        float[] tableVerticesWithTriangles = {
                //Order of coordinates: X, Y, R, G, B

                // Triangle Fan
                    0f,      0f,    1f,     1f,     1f,
                -0.5f,    -0.8f,    0.7f,   0.7f,   0.7f,
                 0.5f,    -0.8f,    0.7f,   0.7f,   0.7f,
                 0.5f,     0.8f,    0.7f,   0.7f,   0.7f,
                -0.5f,     0.8f,    0.7f,   0.7f,   0.7f,
                -0.5f,    -0.8f,    0.7f,   0.7f,   0.7f,
                // Line 1
                -0.5f,     0f,      1f,     0f,     0f,
                 0.5f,     0f,      0f,     1f,     0f,
                // Mallets
                 0f,      -0.4f,   0f,     0f,     1f,
                 0f,       0.4f,   1f,     0f,     0f
        };
        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //读取顶点着色器代码
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.ch05_vertex_shader);
        //读取片段着色器代码
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.ch05_fragment_shader);

        //编译定点着色器代码
        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        //编译片段着色器代码
        int fragmengShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        //链接定点着色器和片段着色器,生成program
        mProgram = ShaderHelper.linkProgram(vertexShader, fragmengShader);

        if (BuildConfig.DEBUG) {
            //验证program
            ShaderHelper.validateProgram(mProgram);
        }

        //使用指定的opengl程序
        glUseProgram(mProgram);

        /*----获取GLSL中的变量位置-----------------------------*/
        //获取a_Position的属性位置
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);
        //获取a_Color的属性位置
        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        //获取u_Matrix矩阵的位置
        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);

        //将缓冲区的位置置为0
        vertexData.position(0);
        //将定点着色器的属性与java对象绑定 //告诉opengl  aPositionLocation 的值可以从vertexData中读取数据 参数index,size,type,normalized,stride,ptr
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //启用定点数组
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);//从该位置开始读取颜色值，每次COLOR_COMPONENT_COUNT个元素，每两个颜色之间的间隔为STRIDE个字节
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        //将定点着色器的属性与java对象绑定
        glEnableVertexAttribArray(aColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        final float aspectRatio = width > height ? (float) width / (float) height : (float) height / (float) width;
        //创建并获取正交投影矩阵
        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
        } else {
            // Portrait or square
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        /*画桌子，两个三角形*/
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);//画三角形  读取顶点数据并且绘制

        /*画中心分割线*/
        glDrawArrays(GL_LINES, 6, 2);

        /*画两个木槌*/
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);

    }
}
