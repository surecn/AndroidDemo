package com.surecn.moat.base;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by surecn on 15/7/27.
 */
public abstract class PageDialogFragment extends AppCompatDialogFragment {

    private String mTitle;

    private final BroadcastReceiver mHomeKeyReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                    dismissAllowingStateLoss();
                }
            }
        }
    };

    public void setTitle(String title) {
        mTitle = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight);
        getActivity().registerReceiver(mHomeKeyReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TextView textView = view.findViewById(android.R.id.title);
//        textView.setText(mTitle);
        Window window = getDialog().getWindow();
        window.setWindowAnimations(android.R.anim.slide_in_left);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mHomeKeyReceiver);
        super.onDestroy();
    }
}
