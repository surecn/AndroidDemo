package com.surecn.demo.domain;

import com.surecn.demo.FolderActivity;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:01
 */
public class FolderItem extends Item {
    private Class<FolderActivity> mActivityClass;
    public FolderItem(String title, String desc, Class<? extends FolderActivity> activityClass) {
        super(title, desc);
    }

    public Class<FolderActivity> getActivityClass() {
        return mActivityClass;
    }
}
