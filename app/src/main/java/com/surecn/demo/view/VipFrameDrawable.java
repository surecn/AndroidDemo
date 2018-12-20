package com.surecn.demo.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.surecn.demo.R;
import com.surecn.moat.utils.DensityUtils;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 17:18
 */
public class VipFrameDrawable extends Drawable {

    private Paint mPaint;

    private Paint mTopPaint;

    private Paint mPaintBackground;

    private int[] mColors;

    private RectF mRoundRect;

    private float mBorderWidth;

    private float mRadius;

    private int mVipLevel;

    private LinearGradient mGraTopLeft;
    private LinearGradient mGraTopRight;
    private LinearGradient mGraLeft;
    private LinearGradient mGraRight;
    private LinearGradient mGraBottom;

    private float mPaddingLeft;
    private float mPaddingTop;
    private float mPaddingRight;
    private float mPaddingBottom;
    private float mIconMarginTop;

    private RectF mRectTopLeft;
    private RectF mRectTopRight;
    private RectF mRectLeft;
    private RectF mRectRight;
    private RectF mRectBottom;

    private float mStrokeWidth;
    private float mStrokeTopWidth;

    private Drawable mIconLeft;
    private Drawable mIconCenter;
    private Drawable mIconRight;
    private Rect mRectBackground;

    public VipFrameDrawable(Context context, int vipLevel) {
        mVipLevel = vipLevel;

        mStrokeWidth = DensityUtils.dp2px(context, 6);
        mStrokeTopWidth = DensityUtils.dp2px(context, 6);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);

        mTopPaint = new Paint();
        mTopPaint.setAntiAlias(true);
        mTopPaint.setStyle(Paint.Style.STROKE);
        mTopPaint.setStrokeWidth(mStrokeTopWidth);

        mPaintBackground = new Paint();
        mPaintBackground.setColor(Color.WHITE);

        mPaddingLeft = DensityUtils.dp2px(context, 0);
        mPaddingTop = DensityUtils.dp2px(context, 10);
        mIconMarginTop = DensityUtils.dp2px(context, 4);

        mPaddingRight = DensityUtils.dp2px(context, 0);
        mPaddingBottom = 0;
        switch (vipLevel) {
            case 2:
                mIconLeft = context.getResources().getDrawable(R.mipmap.pb_bg_vip2namecard_left);
                mIconCenter = context.getResources().getDrawable(R.mipmap.pb_bg_vip2namecard_center);
                mIconRight = context.getResources().getDrawable(R.mipmap.pb_bg_vip2namecard_right);
                break;
            case 3:
                mIconLeft = context.getResources().getDrawable(R.mipmap.pb_bg_vip3namecard_left);
                mIconCenter = context.getResources().getDrawable(R.mipmap.pb_bg_vip3namecard_center);
                mIconRight = context.getResources().getDrawable(R.mipmap.pb_bg_vip3namecard_right);
                break;
        }

    }


    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        mRectTopLeft = new RectF(mPaddingLeft + mIconLeft.getIntrinsicWidth(), mPaddingTop + mStrokeWidth / 2, (float)(getBounds().centerX()) - mIconCenter.getIntrinsicWidth() / 2, mPaddingTop + mStrokeWidth / 2);
        mRectTopRight = new RectF((float)(getBounds().centerX()) + mIconCenter.getIntrinsicWidth() / 2, mPaddingTop + mStrokeWidth / 2, getBounds().right - mIconRight.getIntrinsicWidth(), mPaddingTop + mStrokeWidth / 2);
        mRectLeft= new RectF(mPaddingLeft + mStrokeWidth / 2, mIconMarginTop + mIconLeft.getIntrinsicHeight(), mPaddingLeft + mStrokeWidth / 2, getBounds().bottom - mPaddingBottom);
        mRectRight = new RectF(getBounds().right - mPaddingRight - mStrokeWidth / 2, mIconMarginTop + mIconLeft.getIntrinsicHeight(), getBounds().right - mPaddingRight - mStrokeWidth / 2, getBounds().bottom - mPaddingBottom);
        mRectBottom = new RectF(mPaddingLeft, getBounds().bottom - mPaddingBottom - mStrokeWidth / 2, getBounds().right - mPaddingRight, getBounds().bottom - mPaddingBottom - mStrokeWidth / 2);

        switch (mVipLevel) {
            case 2:
                mGraTopLeft = new LinearGradient(mRectTopLeft.left, mRectTopLeft.top, mRectTopLeft.right, mRectTopLeft.top, new int[]{
                        Color.parseColor("#686D7F"),
                        Color.parseColor("#A5A9C6"),
                        Color.parseColor("#EAEBF1"),
                        Color.parseColor("#BEC3D2")}, new float[]{0, 0.33f, 0.66f, 1f},Shader.TileMode.MIRROR);
                mGraTopRight = new LinearGradient(mRectTopRight.left, mRectTopRight.top, mRectTopRight.right, mRectTopRight.top, new int[]{
                        Color.parseColor("#73788C"),
                        Color.parseColor("#A5A9C6"),
                        Color.parseColor("#EAEBF1"),
                        Color.parseColor("#B3B8CA")
                }, new float[]{0, 0.33f, 0.66f, 1f},Shader.TileMode.MIRROR);
                mGraLeft = new LinearGradient(mRectLeft.left, mRectLeft.top, mRectLeft.left, mRectLeft.bottom, new int[]{
                        Color.parseColor("#AFB5C7"),
                        Color.parseColor("#EAEBF1"),
                        Color.parseColor("#A5A9C6"),
                        Color.parseColor("#5D6272"),
                }, new float[]{0, 0.33f, 0.66f, 1f},Shader.TileMode.MIRROR);
                mGraRight = new LinearGradient(mRectRight.left, mRectRight.top, mRectRight.left, mRectRight.bottom, new int[]{
                        Color.parseColor("#606576"),
                        Color.parseColor("#A5A9C6"),
                        Color.parseColor("#EAEBF1"),
                        Color.parseColor("#ACB2C5"),
                }, new float[]{0, 0.33f, 0.66f, 1f},Shader.TileMode.MIRROR);
                mGraBottom = new LinearGradient(mRectBottom.left, mRectBottom.top, mRectBottom.right, mRectBottom.top, new int[]{
                        Color.parseColor("#5D6272"),
                        Color.parseColor("#A5A9C6"),
                        Color.parseColor("#EAEBF1"),
                        Color.parseColor("#ACB2C5"),
                }, new float[]{0, 0.33f, 0.66f, 1f},Shader.TileMode.MIRROR);
                break;
            case 3:
                mGraTopLeft = new LinearGradient(mRectTopLeft.left, mRectTopLeft.top, mRectTopLeft.right, mRectTopLeft.top, new int[]{
                        Color.parseColor("#BD5C19"),
                        Color.parseColor("#F8D672"),
                        Color.parseColor("#FFF9C8"),
                        Color.parseColor("#EFAE31"),
                        Color.parseColor("#F6D068")}, new float[]{0, 0.25f, 0.5f, 0.75f, 1f},Shader.TileMode.MIRROR);
                mGraTopRight = new LinearGradient(mRectTopRight.left, mRectTopRight.top, mRectTopRight.right, mRectTopRight.top, new int[]{
                        Color.parseColor("#C9701F"),
                        Color.parseColor("#EFAE31"),
                        Color.parseColor("#FFF9C8"),
                        Color.parseColor("#F8D672"),
                        Color.parseColor("#F5CB61")
                }, new float[]{0, 0.25f, 0.5f, 0.75f, 1f},Shader.TileMode.MIRROR);
                mGraLeft = new LinearGradient(mRectLeft.left, mRectLeft.top, mRectLeft.left, mRectLeft.bottom, new int[]{
                        Color.parseColor("#F5CA5E"),
                        Color.parseColor("#F8D672"),
                        Color.parseColor("#FFF9C8"),
                        Color.parseColor("#EFAE31"),
                        Color.parseColor("#B65116"),
                }, new float[]{0, 0.25f, 0.5f, 0.75f, 1f},Shader.TileMode.MIRROR);
                mGraRight = new LinearGradient(mRectRight.left, mRectRight.top, mRectRight.left, mRectRight.bottom, new int[]{
                        Color.parseColor("#B85517"),
                        Color.parseColor("#EFAE31"),
                        Color.parseColor("#FFF9C8"),
                        Color.parseColor("#F8D672"),
                        Color.parseColor("#F5C95D"),
                }, new float[]{0, 0.25f, 0.5f, 0.75f, 1f},Shader.TileMode.MIRROR);
                mGraBottom = new LinearGradient(mRectBottom.left, mRectBottom.top, mRectBottom.right, mRectBottom.top, new int[]{
                        Color.parseColor("#B65116"),
                        Color.parseColor("#EFAE31"),
                        Color.parseColor("#FFF9C8"),
                        Color.parseColor("#F8D672"),
                        Color.parseColor("#F5C95D"),
                }, new float[]{0, 0.25f, 0.5f, 0.75f, 1f},Shader.TileMode.MIRROR);
                break;
        }

        mIconLeft.setBounds((int)mPaddingLeft, (int)mIconMarginTop,  (int)mPaddingLeft + mIconLeft.getIntrinsicWidth(), mIconLeft.getIntrinsicHeight() + (int)mIconMarginTop);
        mIconCenter.setBounds((int)getBounds().centerX() - mIconCenter.getIntrinsicWidth() / 2,
                0,  (int)getBounds().centerX() + mIconCenter.getIntrinsicWidth() / 2, mIconCenter.getIntrinsicHeight());
        mIconRight.setBounds((int)getBounds().right - mIconRight.getIntrinsicWidth() - (int)mPaddingRight, (int)mIconMarginTop,  (int)getBounds().right - (int)mPaddingRight, mIconLeft.getIntrinsicHeight() + (int)mIconMarginTop);

        mRectBackground = new Rect(0, (int)mPaddingTop, getBounds().right, getBounds().bottom);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (mRectTopLeft == null) {
            return;
        }
        canvas.drawRect(mRectBackground, mPaintBackground);
        mTopPaint.setShader(mGraTopLeft);
        canvas.drawLine(mRectTopLeft.left, mRectTopLeft.top, mRectTopLeft.right, mRectTopLeft.top, mTopPaint);
        mTopPaint.setShader(mGraTopRight);
        canvas.drawLine(mRectTopRight.left, mRectTopRight.top, mRectTopRight.right, mRectTopRight.top, mTopPaint);
        mPaint.setShader(mGraLeft);
        canvas.drawLine(mRectLeft.left, mRectLeft.top, mRectLeft.left, mRectLeft.bottom, mPaint);
        mPaint.setShader(mGraRight);
        canvas.drawLine(mRectRight.left, mRectRight.top, mRectRight.left, mRectRight.bottom, mPaint);
        mPaint.setShader(mGraBottom);
        canvas.drawLine(mRectBottom.left, mRectBottom.top, mRectBottom.right, mRectBottom.top, mPaint);

//        Paint p = new Paint();
//        p.setColor(Color.WHITE);
//        canvas.drawRect(mIconLeft.getBounds(), p);
        mIconLeft.draw(canvas);

        //canvas.drawRect(mIconCenter.getBounds(), p);
        mIconCenter.draw(canvas);

        //canvas.drawRect(mIconRight.getBounds(), p);
        mIconRight.draw(canvas);
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
