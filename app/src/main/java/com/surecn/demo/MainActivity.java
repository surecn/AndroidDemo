package com.surecn.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.surecn.demo.domain.ActivityItem;
import com.surecn.demo.domain.CustomItem;
import com.surecn.demo.domain.DialogFragmentItem;
import com.surecn.demo.domain.FolderItem;
import com.surecn.demo.domain.Item;
import com.surecn.demo.live.LiveActivity;
import com.surecn.demo.mvvm.ui.MvvmActivity;
import com.surecn.demo.opengles.OpenGLActivity;
import com.surecn.demo.opengles.ch04.AirHockeyActivity;
import com.surecn.demo.view.LogcatActivity;
import com.surecn.demo.opengles.AirHockey3DActivity;
import com.surecn.demo.utils.AppUtils;
import com.surecn.demo.utils.CommandUtils;
import com.surecn.demo.view.ShadowBorderFragment;
import com.surecn.demo.view.SvgaActivity;
import com.surecn.moat.utils.log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FolderActivity {

    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    String[] permissions = new String[]{Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO};

    //2、创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
    List<String> mPermissionList = new ArrayList<>();

    private final int mRequestCode = 100;//权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.init(this, true);
        initPermission();
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
        mData.add(new ActivityItem("Live", "live", LiveActivity.class));
        mData.add(new ActivityItem("Mvvm", "mvvm", MvvmActivity.class));
        //mData.add(new ActivityItem("new", "Media", CameraActivity.class));

        return mData;
    }

    //权限判断和申请
    private void initPermission() {

        mPermissionList.clear();//清空没有通过的权限

        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }

        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, mRequestCode);
        }else{
            //说明权限都已经通过，可以做你想做的事情去
        }
    }


    //请求权限后回调的方法
    //参数： requestCode  是我们自己定义的权限请求码
    //参数： permissions  是我们请求的权限名称数组
    //参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;//有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                //showPermissionDialog();//跳转到系统设置权限页面，或者直接关闭页面，不让他继续访问
            }else{
                //全部权限通过，可以进行下一步操作。。。

            }
        }

    }

}
