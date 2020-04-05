package com.surecn.demo.opengles.ch03;

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
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockeyRender implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;

    private static final int BYTES_PER_FLOAT = 4;

    private static final String U_COLOR = "u_Color";

    private static final String A_POSITION = "a_Position";

    private static final int STRIDE = 0;

    private int aPositionLocation;

    private int uColorLocation;

    private final FloatBuffer vertexData;

    private Context mContext;

    private int mProgram;

    public AirHockeyRender(Context context) {
        mContext = context;
        float[] tableVertices = {
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
                 0f,       0.25f,

                //border
                -0.4f,      -0.4f,
                -0.4f,      0.4f,

                -0.4f,      0.4f,
                 0.4f,      0.4f,

                 0.4f,      0.4f,
                 0.4f,      -0.4f,

                 0.4f,       -0.4f,
                 -0.4f,      -0.4f,
        };

        vertexData = ByteBuffer.allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //读取顶点着色器代码
        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.ch03_vertex_shader);
        //读取片段着色器代码
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext, R.raw.ch03_fragment_shader);

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
        //获取a_Color的属性位置
        uColorLocation = glGetUniformLocation(mProgram, U_COLOR);
        //获取a_Position的属性位置
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);

        //将缓冲区的位置置为0
        vertexData.position(0);
        //将定点着色器的属性与java对象绑定 //告诉opengl  aPositionLocation 的值可以从vertexData中读取数据 参数index,size,type,normalized,stride,ptr
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, 0, vertexData);
        //启用定点数组
        glEnableVertexAttribArray(aPositionLocation);

//        vertexData.position(POSITION_COMPONENT_COUNT);
//        glVertexAttribPointer(uColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
//        //将定点着色器的属性与java对象绑定
//        glEnableVertexAttribArray(uColorLocation);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        /*画桌子，两个三角形*/
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f); //更新颜色
        glDrawArrays(GL_TRIANGLES, 0, 6);//画三角形

        /*画中心分割线*/
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);//用红色画线
        glDrawArrays(GL_LINES, 6, 2);

        /*画两个木槌*/
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);//用蓝色
        glDrawArrays(GL_POINTS, 8, 1);
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);//用红色
        glDrawArrays(GL_POINTS, 9, 1);

        //画框
        glUniform4f(uColorLocation, 0.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_LINES, 10, 8);

    }
}
