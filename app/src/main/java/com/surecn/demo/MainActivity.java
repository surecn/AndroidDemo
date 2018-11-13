package com.surecn.demo;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.surecn.demo.utils.CommandUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mRecyclerView;

    private List<Item> mData = new ArrayList<Item>(); {
        mData.add(new Item(0,"设置默认输入法", "Root下设置默认输入法"));
        mData.add(new Item(1,"设置默认输入法", "Root下设置默认输入法"));
    }

    // Used to load the 'native-lib' library on application startup.
    static {
        //System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MainViewHolder>() {

            @Override
            public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = (ViewGroup) getLayoutInflater().inflate(R.layout.list_item, null);
                view.setOnClickListener(MainActivity.this);
                return new MainViewHolder(view);
            }

            @Override
            public void onBindViewHolder(MainViewHolder holder, int position) {
                holder.bindData(mData.get(position));
            }

            @Override
            public int getItemCount() {
                return mData.size();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public static class Item {
        private int mIndex;
        private String mTitle;
        private String mDesc;
        public Item(int index, String title, String desc) {
            this.mIndex = index;
            this.mTitle = title;
            this.mDesc = desc;
        }
    }

    public static class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTxtTitle;

        private TextView mTxtDesc;

        public MainViewHolder(View itemView) {
            super(itemView);
            mTxtTitle = itemView.findViewById(R.id.title);
            mTxtDesc = itemView.findViewById(R.id.desc);
        }

        public void bindData(Item data) {
            itemView.setTag(data);
            mTxtTitle.setText(data.mTitle);
            mTxtDesc.setText(data.mDesc);
        }

        @Override
        public void onClick(View v) {
            Object obj = v.getTag();
            if (obj == null) {
                return;
            }
            Item item = (Item) obj;
            switch (item.mIndex) {
                case 0:{//设置默认输入法
                    /**
                     * 以root权限执行以下命令
                     * 1.启用相应输入法
                     * 2.设置默认输入法
                     */
                    CommandUtils.executeRootCommand("settings put secure enabled_input_methods 'com.sohu.inputmethod.sogou/.SogouIME:com.google.android.inputmethod.pinyin/.PinyinIME;113646356:com.samsung.inputmethod/.SamsungIME:com.xh.ime/.OpenWnnZHCN'");
                    CommandUtils.executeRootCommand("settings put secure default_input_method 'com.sohu.inputmethod.sogou/.SogouIME'");
                    break;}
            }
            Log.e("onClick", "" + v.getTag());
        }
    }

//
//    /**
//     * A native method that is implemented by the 'native-lib' native library,
//     * which is packaged with this application.
//     */
//    public native String stringFromJNI();
}
