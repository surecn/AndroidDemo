package com.surecn.demo;

import android.app.Application;

import com.surecn.demo.utils.log;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-13
 * Time: 19:02
 */
public class DemoApplicaiton extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        log.init("zbg", true);
    }
}
