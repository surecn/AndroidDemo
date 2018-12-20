package com.surecn.demo.domain;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:00
 */
public class Item {
    private String mTitle;

    private String mDesc;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setmDesc(String desc) {
        this.mDesc = desc;
    }

    public Item(String title, String desc) {
        this.mTitle = title;
        this.mDesc = desc;
    }


}
