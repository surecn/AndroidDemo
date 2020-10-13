package com.surecn.demo.opengles.ch07;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch07.objects.Mallet;
import com.surecn.demo.opengles.ch07.objects.Table;
import com.surecn.demo.opengles.ch07.program.ColorShaderProgram;
import com.surecn.demo.opengles.ch07.program.TextureShaderProgram;
import com.surecn.demo.opengles.utils.MatrixHelper;
import com.surecn.demo.opengles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockey3DRender implements GLSurfaceView.Renderer {

    private Context mContext;

    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table table;

    private Mallet mallet;

    private TextureShaderProgram textureProgram;

    private ColorShaderProgram colorProgram;

    private int texture;

    public AirHockey3DRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet();

        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        //视锥题角度45读，到近处平面距离为1，  到远处平面距离为10  ，Z的值 -1 ～ -10
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);

        setIdentityM(modelMatrix, 0);
        //将使用模型矩阵将平面往远处平移2.5个单位
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        //围绕x轴旋转-60度
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);


        //同时应用模型矩阵和投影矩阵， 因此将投影矩阵乘以模型矩阵
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        textureProgram.useProgram();
        textureProgram.setUniforms(projectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        colorProgram.useProgram();
        colorProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorProgram);
        mallet.draw();
    }
}
