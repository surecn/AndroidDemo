package com.surecn.demo.domain;

import android.support.v7.app.AppCompatActivity;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:01
 */
public class ActivityItem extends Item {
    private Class mActivity;
    public ActivityItem(String title, String desc, Class<? extends AppCompatActivity> activityClass) {
        super(title, desc);
        mActivity = activityClass;
    }

    public Class getActivity() {
        return mActivity;
    }
}
