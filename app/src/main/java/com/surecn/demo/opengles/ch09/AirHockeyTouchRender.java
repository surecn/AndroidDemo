package com.surecn.demo.opengles.ch09;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch09.objects.Mallet;
import com.surecn.demo.opengles.ch09.objects.Puck;
import com.surecn.demo.opengles.ch09.objects.Table;
import com.surecn.demo.opengles.ch09.program.ColorShaderProgram;
import com.surecn.demo.opengles.ch09.program.TextureShaderProgram;
import com.surecn.demo.opengles.utils.Geometry;
import com.surecn.demo.opengles.utils.MatrixHelper;
import com.surecn.demo.opengles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class AirHockeyTouchRender implements GLSurfaceView.Renderer {

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

    //木槌是否被点击
    private boolean malletPressed = false;

    //蓝色木槌的位置
    private Geometry.Point blueMalletPosition;

    //反转的矩阵
    private final float[] invertedViewProjectionMatrix = new float[16];

    //经过裁剪后桌子的边界
    private final float leftBound = -0.5f;
    private final float rightBound = 0.5f;
    private final float farBound = -0.8f;
    private final float nearBound = 0.8f;

    //蓝色木槌上次的位置
    private Geometry.Point previousBlueMalletPosition;

    //冰球的位置
    private Geometry.Point puckPosition;

    //冰球移动的方向，和速度
    private Geometry.Vector puckVector;

    public AirHockeyTouchRender(Context context) {
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

        blueMalletPosition = new Geometry.Point(0f, mallet.height / 2f, 0.4f);

        puckPosition = new Geometry.Point(0f, puck.height / 2f, 0f);
        puckVector = new Geometry.Vector(0f, 0f, 0f);

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        //视锥题角度45读，到近处平面距离为1，  到远处平面距离为10  ，Z的值 -1 ～ -10
        MatrixHelper.perspectiveM(projectionMatrix, 45, (float)width / (float)height, 1f, 10f);


        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
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
        //位置更新
        puckPosition = puckPosition.translate(puckVector);
        //左右边界检查
        if (puckPosition.x < leftBound + puck.radius
            || puckPosition.x > rightBound - puck.radius) {//过了边界就反转x分量
            puckVector = new Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        //近边或远边检查
        if (puckPosition.z < farBound + puck.radius
            || puckPosition.z > nearBound - puck.radius) {//过了边界就反转z分量
            puckVector = new Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }
        puckPosition = new Geometry.Point(clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y, clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius));

        glClear(GL_COLOR_BUFFER_BIT);

        //不需要改变可以放到onSurfaceChanged中
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

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
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
        puckVector = puckVector.scale(0.99f);

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

    public void handleTouchPress(float normalizedX, float normalizedY) {
        //将触碰点投射到一条射线上
        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
        //用包围球封装木槌
        Geometry.Sphere malletBoundingSphere = new Geometry.Sphere(new Geometry.Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z), mallet.height / 2f);
        //测试射线和球是否相交
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);

    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        if(malletPressed) {
            Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

            Geometry.Plane plane = new Geometry.Plane(new Geometry.Point(0, 0, 0), new Geometry.Vector(0, 1, 0));

            Geometry.Point touchedPoint = Geometry.intersectionPoint(ray, plane);

            previousBlueMalletPosition = blueMalletPosition;
            blueMalletPosition = new Geometry.Point(clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius), mallet.height / 2f, clamp(touchedPoint.z, 0f + mallet.radius, nearBound - mallet.radius));

            //检测木槌和冰球之间的距离
            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {//距离是否小于木槌和冰球的半径之和
                //puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
                //puckVector.scale(puckVector.cos(Geometry.vectorBetween(puckPosition, blueMalletPosition)));

            }
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    /**
     * 把触摸点转化为一条投影中的射线
     * @param normalizedX
     * @param normalizedY
     * @return
     */
    private Geometry.Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        //归一化设备坐标中的两个点 一个z为-1， 一个为1
        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];
        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        //除以反转的w，撤销透视除法的影响
        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Geometry.Point nearPointRay = new Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Geometry.Point farPointRay = new Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);

        return new Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float [] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

}
