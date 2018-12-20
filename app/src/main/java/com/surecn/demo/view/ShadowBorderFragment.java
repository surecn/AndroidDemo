package com.surecn.demo.view;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.surecn.moat.base.PageDialogFragment;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-17
 * Time: 20:00
 */
public class ShadowBorderFragment extends PageDialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Paint p = new Paint();
        final LinearGradient lg = new LinearGradient(100,100,530,200,
                new int[]{Color.parseColor("#b1b7c9"),
                        Color.parseColor("#c1c6d6"),
                        Color.parseColor("#e1e3ec"),
                        Color.parseColor("#8f93ac"),
                        Color.parseColor("#5d6272")}, new float[]{0, 0.32f, 0.4f, 0.8f, 1f},Shader.TileMode.MIRROR);
        View view = new View(getContext());
        //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        VipFrameDrawable drawable = new VipFrameDrawable(getContext(),3);
        view.setBackground(drawable);
        //container.addView(view);
        return view;
    }
}
