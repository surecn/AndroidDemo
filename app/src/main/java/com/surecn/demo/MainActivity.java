package com.surecn.demo;

import android.os.Bundle;

import com.surecn.demo.domain.ActivityItem;
import com.surecn.demo.domain.CustomItem;
import com.surecn.demo.domain.DialogFragmentItem;
import com.surecn.demo.domain.FolderItem;
import com.surecn.demo.domain.Item;
import com.surecn.demo.opengles.OpenGLActivity;
import com.surecn.demo.opengles.ch04.AirHockeyActivity;
import com.surecn.demo.view.LogcatActivity;
import com.surecn.demo.opengles.AirHockey3DActivity;
import com.surecn.demo.utils.AppUtils;
import com.surecn.demo.utils.CommandUtils;
import com.surecn.demo.view.ShadowBorderFragment;
import com.surecn.demo.view.SvgaActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FolderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected List<Item> getData() {
        List<Item> mData = new ArrayList<Item>();
        mData.add(new CustomItem("设置默认输入法", "Root下设置默认输入法", new Runnable() {
            @Override
            public void run() {
                //设置默认输入法
                /**
                 * 以root权限执行以下命令
                 * 1.启用相应输入法
                 * 2.设置默认输入法
                 */
                CommandUtils.executeRootCommand("settings put secure enabled_input_methods 'com.sohu.inputmethod.sogou/.SogouIME:com.google.android.inputmethod.pinyin/.PinyinIME;113646356:com.samsung.inputmethod/.SamsungIME:com.xh.ime/.OpenWnnZHCN'");
                CommandUtils.executeRootCommand("settings put secure default_input_method 'com.sohu.inputmethod.sogou/.SogouIME'");
            }
        }));
        mData.add(new CustomItem("获取签名", "获取签名", new Runnable() {
            @Override
            public void run() {
                AppUtils.getSignMd5Str(MainActivity.this);
            }
        }));
        mData.add(new ActivityItem("OpenGLES", "OpenES demo", OpenGLActivity.class));
        mData.add(new ActivityItem("OpenES3D", "OpenES3D demo", AirHockey3DActivity.class));
        mData.add(new ActivityItem("SVGA", "SVGA demo", SvgaActivity.class));
        mData.add(new DialogFragmentItem("渐变颜色边框", "渐变颜色边框", ShadowBorderFragment.class));
        mData.add(new FolderItem("UI", "UI", UIFolderActivity.class));
        mData.add(new ActivityItem("logcat", "logcat", LogcatActivity.class));
        mData.add(new ActivityItem("new", "logcat", NewActivity.class));
        //mData.add(new ActivityItem("new", "Media", CameraActivity.class));

        return mData;
    }
}
