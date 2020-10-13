package com.surecn.demo.opengles.ch08;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch08.objects.Mallet;
import com.surecn.demo.opengles.ch08.objects.Puck;
import com.surecn.demo.opengles.ch08.objects.Table;
import com.surecn.demo.opengles.ch08.program.ColorShaderProgram;
import com.surecn.demo.opengles.ch08.program.TextureShaderProgram;
import com.surecn.demo.opengles.utils.MatrixHelper;
import com.surecn.demo.opengles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockey3DRender implements GLSurfaceView.Renderer {

    private Context mContext;

    private final float[] viewMatrix = new float[16];

    private final float[] viewProjectionMatrix = new float[16];

    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table table;

    private Mallet mallet;

    private Puck puck;

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
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        textureProgram = new TextureShaderProgram(mContext);
        colorProgram = new ColorShaderProgram(mContext);

        texture = TextureHelper.loadTexture(mContext, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        //视锥题角度45读，到近处平面距离为1，  到远处平面距离为10  ，Z的值 -1 ～ -10
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);


        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

//        setIdentityM(modelMatrix, 0);
//        //将使用模型矩阵将平面往远处平移2.5个单位
//        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
//        //围绕x轴旋转-60度
//        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
//
//
//        //同时应用模型矩阵和投影矩阵， 因此将投影矩阵乘以模型矩阵
//        final float[] temp = new float[16];
//        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
//        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        positionTableInscene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        //设置木槌的位置
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        //设置木槌的位置
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInscene() {
        setIdentityM(modelMatrix, 0);
        //桌子绕x轴旋转90度
        rotateM(modelMatrix, 0, -90, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0,  viewProjectionMatrix, 0, modelMatrix, 0);
    }

}
