package com.surecn.demo.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.surecn.demo.R;
import com.surecn.demo.utils.log;
import com.surecn.moat.base.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * User: surecn(surecn@163.com)
 * Date: 2019-01-29
 * Time: 12:01
 */
public class LogcatActivity extends BaseActivity {

    private TextView mViewContent;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.obj != null) {
                mViewContent.append(msg.obj.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logcat);
        mViewContent = findViewById(R.id.content);

        new Thread() {
            @Override
            public void run() {
                try {
                    ArrayList<String> commandLine = new ArrayList();
                    commandLine.add( "logcat");
                    commandLine.add( "-d");
                    commandLine.add( "-v");
                    commandLine.add( "time");
//                    commandLine.add( "-f");
//                    commandLine.add( "/sdcard/log/logcat.txt");
                    Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
                    BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
                    String line = bufferedReader.readLine();
                    while ( line != null) {
                        Message msg = handler.obtainMessage();
                        msg.obj = line + "\n";
                        msg.sendToTarget();
                        line = bufferedReader.readLine();
                    }
                } catch ( IOException e) {
                    log.e("IOException", e);
                }
            }
        }.start();
    }
}
