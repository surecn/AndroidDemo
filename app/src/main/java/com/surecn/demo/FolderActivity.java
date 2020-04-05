package com.surecn.demo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.surecn.demo.domain.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class FolderActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        setContentView(mRecyclerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        if (getIntent().hasExtra("title")) {
            actionBar.setTitle(getIntent().getStringExtra("title"));
        }

        ListAdapter listAdapter = new ListAdapter(this, getData());
        mRecyclerView.setAdapter(listAdapter);

    }

    protected abstract List<Item> getData();

}
