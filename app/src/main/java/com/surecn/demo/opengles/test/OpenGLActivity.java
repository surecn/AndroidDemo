package com.surecn.demo.opengles.test;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.surecn.moat.base.BaseActivity;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-12
 * Time: 15:35
 */
public class OpenGLActivity extends BaseActivity {

    private GLSurfaceView mGLSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLSurfaceView = new GLSurfaceView(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(new OpenGLRender());
        setContentView(mGLSurfaceView);
    }
}
