package com.surecn.demo;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class NewActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private DrawerLayout mDrawer;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);
        setContentView(webView);

        webView.loadUrl("http://sandbox.payssion.com/pay/T528135845587386");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("zbg", "url:" + url);
            }
        });


        //final ImageView imageView = findViewById(R.id.image);

//        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int viewWidth = imageView.getWidth();
//                int viewHeight = imageView.getHeight();
//
//                Matrix matrix = new Matrix(imageView.getMatrix());
//                float scale;
//                float dx = 0, dy = 0;
//
//                Drawable drawable = getResources().getDrawable(R.mipmap.splash_screen_bg);
//
//                int imageWidth = drawable.getIntrinsicWidth();
//                int imageHeight = drawable.getIntrinsicHeight();
//
//                if (imageWidth * viewHeight > viewWidth * imageHeight) {
//                    scale = (float) viewHeight / (float) imageHeight;
//                    dx = (viewWidth - imageWidth * scale) * 0.5f;
//                } else {
//                    scale = (float) viewWidth / (float) imageWidth;
//                    dy = (viewHeight - imageHeight * scale) * 0.5f;
//                }
//
//                matrix.setScale(scale, scale);
//
//                //int top = (int) dm.heightPixels - (imageHeight * scale )/ 4);
//                matrix.postTranslate((int) (dx + 0.5f), (int)(dy + 0.5f));
//                imageView.setImageMatrix(matrix);
//
//                imageView.setClipBounds(new Rect(0, 0, viewWidth,  viewHeight));
//            }
//        });


    }
}
