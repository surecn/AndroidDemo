package com.surecn.demo.view.opengles;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.surecn.moat.base.BaseActivity;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-13
 * Time: 10:43
 */
public class AirHockeyActivity extends BaseActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new AirHockeyRender(this));
        setContentView(mGLSurfaceView);
    }

}
