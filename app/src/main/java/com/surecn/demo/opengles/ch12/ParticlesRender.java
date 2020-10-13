package com.surecn.demo.opengles.ch12;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;

import com.surecn.demo.R;
import com.surecn.demo.opengles.ch12.objects.Heightmap;
import com.surecn.demo.opengles.ch12.objects.ParticleShooter;
import com.surecn.demo.opengles.ch12.objects.ParticleSystem;
import com.surecn.demo.opengles.ch12.objects.Skybox;
import com.surecn.demo.opengles.ch12.program.HeightmapShaderProgram;
import com.surecn.demo.opengles.ch12.program.ParticleShaderProgram;
import com.surecn.demo.opengles.ch12.program.SkyboxShaderProgram;
import com.surecn.demo.opengles.utils.Geometry;
import com.surecn.demo.opengles.utils.MatrixHelper;
import com.surecn.demo.opengles.utils.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_LEQUAL;
import static android.opengl.GLES20.GL_LESS;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDepthFunc;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:37
 */
public class ParticlesRender implements GLSurfaceView.Renderer {

    private Context mContext;

    private final float[] projectionMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    //private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrixForSkybox = new float[16];

    private final float[] tempMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private HeightmapShaderProgram heightmapProgram;
    private Heightmap heightmap;

    private ParticleShaderProgram particleProgram;
    private ParticleSystem particleSystem;
    private ParticleShooter redParticleShooter;
    private ParticleShooter greenParticleShooter;
    private ParticleShooter blueParticleShooter;
    private long globalStartTime;

    private final float angleVarianceInDegrees = 5f;
    private final float speedVariance = 1f;

    private int texture;


    private SkyboxShaderProgram skyboxProgram;
    private Skybox skybox;
    private int skyboxTexture;

    private float xRotation, yRotation;

    public ParticlesRender(Context context) {
        mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glEnable(GL_DEPTH_TEST);//打开深度缓冲区功能
        glEnable(GL_CULL_FACE);

        particleProgram = new ParticleShaderProgram(mContext);
        particleSystem = new ParticleSystem(10000);
        globalStartTime = System.nanoTime();//纳秒

        final Geometry.Vector particleDirection = new Geometry.Vector(0f, 0.5f, 0f);
        redParticleShooter = new ParticleShooter(
                new Geometry.Point(-1f, 0f, 0f),
                particleDirection,
                Color.rgb(255, 50, 5),
                angleVarianceInDegrees,
                speedVariance
        );

        greenParticleShooter = new ParticleShooter(
                new Geometry.Point(0f, 0f, 0f),
                particleDirection,
                Color.rgb(25, 255, 25),
                angleVarianceInDegrees,
                speedVariance);

        blueParticleShooter = new ParticleShooter(
                new Geometry.Point(1f, 0f, 0f),
                particleDirection,
                Color.rgb(5,50, 255),
                angleVarianceInDegrees,
                speedVariance);
//
//        glEnable(GL_BLEND);//启用混合技术
//        glBlendFunc(GL_ONE, GL_ONE);//使用累加混合模式

        texture = TextureHelper.loadTexture(mContext, R.drawable.particle_texture);

        skyboxProgram = new SkyboxShaderProgram(mContext);
        skybox = new Skybox();
        skyboxTexture = TextureHelper.loadCubeMap(mContext, new int[] {
                R.drawable.left, R.drawable.right, R.drawable.bottom,
                R.drawable.top, R.drawable.front, R.drawable.back
        });

        heightmapProgram = new HeightmapShaderProgram(mContext);
        heightmap = new Heightmap(((BitmapDrawable)mContext.getResources().getDrawable(R.drawable.heightmap)).getBitmap());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, 45, (float) width / (float)height, 1f, 100f);

//        setIdentityM(viewMatrix, 0);
//        translateM(viewMatrix, 0, 0f, -1.5f,-5f);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        updateViewMatrices();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawHeightmap();
//
//        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
//
//        redParticleShooter.addParticles(particleSystem, currentTime, 5);
//        greenParticleShooter.addParticles(particleSystem, currentTime, 5);
//        blueParticleShooter.addParticles(particleSystem, currentTime, 5);
//
//        particleProgram.useProgram();
//        particleProgram.setUniforms(viewProjectionMatrix, currentTime, texture);
//        particleSystem.bindData(particleProgram);
//        particleSystem.draw();

        drawSkybox();
        drawParticles();
    }

    private void drawHeightmap() {
        setIdentityM(modelMatrix, 0);
        scaleM(modelMatrix, 0, 100f, 10f, 100f);
        updateMvpMatrix();
        heightmapProgram.useProgram();
        heightmapProgram.setUniforms(modelViewProjectionMatrix);
        heightmap.bindData(heightmapProgram);
        heightmap.draw();
    }

    private void drawSkybox() {
//        setIdentityM(viewMatrix, 0);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        setIdentityM(modelMatrix, 0);
//        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
//        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
//        //translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        updateMvpMatrixForSkybox();

        glDepthFunc(GL_LEQUAL);//改变深度测试算法
        skyboxProgram.useProgram();
        skyboxProgram.setUniforms(modelViewProjectionMatrix, skyboxTexture);
        skybox.bindData(skyboxProgram);
        skybox.draw();
        glDepthFunc(GL_LESS);
    }

    private void drawParticles() {
        float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;

        redParticleShooter.addParticles(particleSystem, currentTime, 1);
        greenParticleShooter.addParticles(particleSystem, currentTime, 1);
        blueParticleShooter.addParticles(particleSystem, currentTime, 1);

        setIdentityM(modelMatrix, 0);
//        translateM(viewMatrix, 0, 0f, -1.5f, -5f);
//        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        updateMvpMatrix();

        glDepthMask(false);
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);

        particleProgram.useProgram();
        particleProgram.setUniforms(modelViewProjectionMatrix, currentTime, texture);
        particleSystem.bindData(particleProgram);
        particleSystem.draw();

        glDisable(GL_BLEND);
        glDepthMask(true);
    }

    private void updateViewMatrices() {
        setIdentityM(viewMatrix, 0);
        rotateM(viewMatrix, 0, -yRotation, 1f, 0f, 0f);
        rotateM(viewMatrix, 0, -xRotation, 0f, 1f, 0f);
        System.arraycopy(viewMatrix, 0, viewMatrixForSkybox, 0, viewMatrix.length);

        translateM(viewMatrix, 0, 0, -1.5f, -15f);
    }

    private void updateMvpMatrix() {
        multiplyMM(tempMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    private void updateMvpMatrixForSkybox() {
        multiplyMM(tempMatrix, 0, viewMatrixForSkybox, 0, modelMatrix, 0);
        multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, tempMatrix, 0);
    }

    public void handleTouchPress(float deltaX, float deltaY) {

    }

    public void handleTouchDrag(float deltaX, float deltaY) {
        xRotation += deltaX / 16f;
        yRotation += deltaY / 16f;

        if (yRotation < -90) {
            yRotation = -90;
        } else if (yRotation > 90) {
            yRotation = 90;
        }
        updateViewMatrices();
    }

}
