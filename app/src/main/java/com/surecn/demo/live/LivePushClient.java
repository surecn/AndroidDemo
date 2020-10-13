package com.surecn.demo.live;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.surecn.demo.media.VideoEncoder;
import com.surecn.demo.utils.log;

import java.io.IOException;
import java.util.List;

public class LivePushClient implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;

    private Camera mCamera;

    private AudioRecord mAudioRecord;

    private Integer mRecordBufferSize;

    private boolean mSurfaceCreated = false;

    private boolean mExitAudioRecord = false;

    private VideoEncoder videoEncoder;

    private int mWidth;

    private int mHeight;

    public LivePushClient(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;

        init();
    }

    private void init() {
        this.mWidth = 480;
        this.mHeight = 640;
        // 使用SurfaceHolder设置屏幕高亮，注意：所有的View都可以设置 设置屏幕高亮
        mSurfaceView.getHolder().setKeepScreenOn(true);

        // 使用SurfaceHolder设置把画面或缓存 直接显示出来
        mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(this);

    }

    public void resume() {
        openCamera();
        startAudioRecord();
    }

    public void pause() {
        stopCamera();
    }

    private void openCamera() {
        if (!mSurfaceCreated || mCamera != null) {
            return;
        }
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("orientation", "portrait");
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
//        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
//        for(Camera.Size size :sizes){
//            log.e("===size" + size.width + " :" + size.height);
//            mWidth = size.width;
//            mHeight = size.height;
//        }

        parameters.setPreviewSize(this.mWidth, this.mHeight);
        //        parameters.setPreviewFormat(ImageFormat.NV21);
        parameters.setPictureFormat(ImageFormat.YV12);
        parameters.setPreviewFormat(ImageFormat.YV12);

        mCamera.setDisplayOrientation(90);
        mCamera.setParameters(parameters);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                videoEncoder.putVideoData(data);
            }
        });

        videoEncoder = new VideoEncoder(mWidth, mHeight);
    }

    private void stopCamera() {
        if(mCamera != null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void startAudioRecord() {
        if (mAudioRecord != null) {
            return;
        }
        //获取每一帧的字节流大小
        mRecordBufferSize = AudioRecord.getMinBufferSize(8000
                , AudioFormat.CHANNEL_IN_MONO
                , AudioFormat.ENCODING_PCM_16BIT);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, mRecordBufferSize);

        new Thread() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                byte[] bytes = new byte[mRecordBufferSize];
                while (!mExitAudioRecord) {
                    mAudioRecord.read(bytes, 0, mRecordBufferSize);
                }
                mAudioRecord.stop();
            }
        }.start();
    }

    private void stopAduioRecord() {
        mExitAudioRecord = true;
        mAudioRecord = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceCreated = true;
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

}
