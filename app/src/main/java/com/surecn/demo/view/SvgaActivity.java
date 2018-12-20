package com.surecn.demo.view;

import android.app.Activity;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.opensource.svgaplayer.SVGAImageView;
import com.opensource.svgaplayer.SVGAParser;
import com.opensource.svgaplayer.SVGAVideoEntity;
import com.surecn.moat.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-17
 * Time: 09:58
 */
public class SvgaActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SVGAImageView imageView = new SVGAImageView(this);
        SVGAParser parser = new SVGAParser(this);
        parser.parse("zhandouji_6s", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
//                SVGADrawable drawable = new SVGADrawable(videoItem);
//                imageView.setImageDrawable(drawable);
//                imageView.startAnimation();
            }
            @Override
            public void onError() {
            }
        });

        parser.parse("demo.svga", new SVGAParser.ParseCompletion() {
            @Override
            public void onComplete(@NotNull SVGAVideoEntity videoItem) {
//                SVGADrawable drawable = new SVGADrawable(videoItem);
//                imageView.setImageDrawable(drawable);
//                imageView.startAnimation();
            }
            @Override
            public void onError() {
            }
        });

        setContentView(imageView);

    }
}
