package com.surecn.demo.opengles.ch14;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

public class GLWallpaperService extends WallpaperService {

    private GLEngine.WallpaperGLSurfaceView glSurfaceView;

    private boolean rendererSet;

    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {

        public class WallpaperGLSurfaceView extends GLSurfaceView {
            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onWallpaperDestory() {
                super.onDetachedFromWindow();
            }
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);

            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);

            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = am.getDeviceConfigurationInfo();
            boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

            final ParticlesRender particlesRender = new ParticlesRender(GLWallpaperService.this);

            if (supportsEs2) {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(particlesRender);
//                glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
//                    float previousX, previousY;
//                    @Override
//                    public boolean onTouch(View view, MotionEvent event) {
//                        if (event != null) {
//                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                                previousX = event.getX();
//                                previousY = event.getY();
//                            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                                final float normalizedX = event.getX() - previousX;
//                                final float normalizedY = event.getY() - previousY;
//                                previousX = event.getX();
//                                previousY = event.getY();
//                                glSurfaceView.queueEvent(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        particlesRender.handleTouchDrag(normalizedX, normalizedY);
//                                    }
//                                });
//                            }
//                            return true;
//                        } else {
//                            return false;
//                        }
//                    }
//                });
                rendererSet = true;
            } else {
                return;
            }


        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestory();
        }
    }




}
