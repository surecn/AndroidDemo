package com.surecn.demo.mvvm.ui;

import android.app.Activity;
import android.app.Service;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.surecn.demo.R;
import com.surecn.demo.mvvm.entity.User;
import com.surecn.demo.mvvm.viewmodel.MvvmViewModel;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MvvmActivity extends AppCompatActivity implements View.OnClickListener {

    private MvvmViewModel mMvvmViewModel;

    private TextView mViewResult;

    private EditText mViewUser;

    private EditText mViewPass;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvvm);
        initView();
        initViewModel();
    }

    private void initView() {
        mViewResult = findViewById(R.id.result);
        mViewUser = findViewById(R.id.user);
        mViewPass = findViewById(R.id.pass);
        findViewById(R.id.register).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);
    }

    private void initViewModel() {
        mMvvmViewModel = ViewModelProviders.of(this, new MvvmViewModel.Factory(getApplication())).get(MvvmViewModel.class);
        mMvvmViewModel.getResultLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String result) {
                mViewResult.setText(result);
            }
        });
    }

    private final OkHttpClient client = new OkHttpClient();

    public void test() throws Exception {
        // 创建Request
        Request request = new Request.Builder()
                .url("http://www.baidu.com/")
                .build();

        // 获取到结果
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        System.out.println(response.body().string());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register: {
                String username = mViewUser.getText().toString();
                String pass = mViewPass.getText().toString();
                User user = new User();
                user.setUserName(username);
                user.setPassword(pass);
                mMvvmViewModel.register(user);
                break;
            }
            case R.id.login: {
                String username = mViewUser.getText().toString();
                String pass = mViewPass.getText().toString();
                mMvvmViewModel.login(username, pass);
                break;
            }
        }
    }
}
