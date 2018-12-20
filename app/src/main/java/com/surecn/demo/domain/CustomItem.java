package com.surecn.demo.domain;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:07
 */
public class CustomItem extends Item {

    private Runnable mRunnable;

    public CustomItem(String title, String desc, Runnable runnable) {
        super(title, desc);
        this.mRunnable = runnable;
    }

    public Runnable getRunnable() {
        return mRunnable;
    }
}
