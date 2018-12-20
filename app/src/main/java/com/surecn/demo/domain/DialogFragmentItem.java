package com.surecn.demo.domain;


import android.support.v4.app.DialogFragment;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:01
 */
public class DialogFragmentItem extends Item {
    private Class mDialogFragmentClass;
    public DialogFragmentItem(String title, String desc, Class<? extends DialogFragment> fragment) {
        super(title, desc);
        mDialogFragmentClass = fragment;
    }

    public Class getDialogFragment() {
        return mDialogFragmentClass;
    }
}