package com.surecn.demo.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 11:04
 */
public class ShadowBorderDrawable extends Drawable {

    private Paint mPaint;

    private Paint mFillPaint;

    private int[] mColors;

    private RectF mRoundRect;

    private float mBorderWidth;

    private float mRadius;

    public ShadowBorderDrawable(@NonNull String [] colorStrings, float borderWidth, float radius, String fillColor) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(Color.parseColor(fillColor));
        mBorderWidth = borderWidth;
        mPaint.setStrokeWidth(mBorderWidth);
        mColors = new int[colorStrings.length];
        for (int i = 0, len = mColors.length; i < len; i++) {
            mColors[i] = Color.parseColor(colorStrings[i]);
        }
        mRadius = radius;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        LinearGradient lg = new LinearGradient(left, top, right, bottom, mColors, new float[]{0, 0.32f, 0.4f, 0.8f, 1f},Shader.TileMode.MIRROR);
        mPaint.setShader(lg);
        mRoundRect = new RectF(left + mBorderWidth / 2,
                top + mBorderWidth / 2,
                right - mBorderWidth / 2,
                bottom - mBorderWidth / 2
        );
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (mRoundRect == null) {
            return;
        }
        canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mFillPaint);
        canvas.drawRoundRect(mRoundRect, mRadius, mRadius, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
