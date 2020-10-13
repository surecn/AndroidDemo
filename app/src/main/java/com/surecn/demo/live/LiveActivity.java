package com.surecn.demo.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import com.surecn.demo.R;

public class LiveActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;

    private LivePushClient mLivePushClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live);
        mSurfaceView = findViewById(R.id.surface);
        mLivePushClient = new LivePushClient(mSurfaceView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivePushClient.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivePushClient.pause();
    }


}
