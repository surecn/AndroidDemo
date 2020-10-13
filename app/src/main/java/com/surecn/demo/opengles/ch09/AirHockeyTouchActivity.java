package com.surecn.demo.opengles.ch09;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-13
 * Time: 10:43
 */
public class AirHockeyTouchActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);

        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo = am.getDeviceConfigurationInfo();
        boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        final AirHockeyTouchRender airHockeyTouchRender = new AirHockeyTouchRender(this);

        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            glSurfaceView.setRenderer(airHockeyTouchRender);
            glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event != null) {
                        final float normalizedX = (event.getX() / (float) view.getWidth()) * 2 - 1;
                        final float normalizedY = -((event.getY() / (float) view.getHeight()) * 2 - 1);
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    airHockeyTouchRender.handleTouchPress(normalizedX, normalizedY);
                                }
                            });
                        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    airHockeyTouchRender.handleTouchDrag(normalizedX, normalizedY);
                                }
                            });
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            rendererSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0", Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rendererSet) glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (rendererSet) glSurfaceView.onPause();
    }
}
