package com.surecn.demo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.surecn.demo.domain.Item;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:02
 */
public class MainViewHolder extends RecyclerView.ViewHolder {

    private TextView mTxtTitle;

    private TextView mTxtDesc;

    public MainViewHolder(View itemView) {
        super(itemView);
        mTxtTitle = itemView.findViewById(R.id.title);
        mTxtDesc = itemView.findViewById(R.id.desc);
    }

    public void bindData(Item data) {
        itemView.setTag(data);
        mTxtTitle.setText(data.getTitle());
        mTxtDesc.setText(data.getTitle());
    }

}
