package com.surecn.demo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.surecn.demo.domain.ActivityItem;
import com.surecn.demo.domain.CustomItem;
import com.surecn.demo.domain.DialogFragmentItem;
import com.surecn.demo.domain.FolderItem;
import com.surecn.demo.domain.Item;
import com.surecn.moat.base.PageDialogFragment;
import android.support.v7.app.AppCompatActivity;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-18
 * Time: 10:09
 */
public class ListAdapter extends RecyclerView.Adapter<MainViewHolder> implements View.OnClickListener {

    private List<Item> mData;

    private AppCompatActivity mContext;

    public ListAdapter(AppCompatActivity context, List<Item> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (ViewGroup) mContext.getLayoutInflater().inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        Object obj = v.getTag();
        if (obj == null) {
            return;
        }
        Log.e("onClick", "" + ((Item)(v.getTag())).getTitle());
        if (obj instanceof FolderItem) {
            FolderItem folderItem = (FolderItem) obj;
            Intent intent = new Intent(mContext, FolderActivity.class);
            intent.putExtra("title", folderItem.getTitle());
            mContext.startActivity(intent);
        } else if (obj instanceof ActivityItem) {
            ActivityItem activityItem = (ActivityItem) obj;
            Intent intent = new Intent(mContext, activityItem.getActivity());
            intent.putExtra("title", activityItem.getTitle());
            mContext.startActivity(intent);
        } else if (obj instanceof DialogFragmentItem) {
            try {
                DialogFragmentItem activityItem = (DialogFragmentItem) obj;
                PageDialogFragment fragment = (PageDialogFragment) activityItem.getDialogFragment().newInstance();
                fragment.setTitle(activityItem.getTitle());
                fragment.show(mContext.getSupportFragmentManager(), fragment.getClass().getName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        } else if (obj instanceof CustomItem) {
            CustomItem customItem = (CustomItem) obj;
            customItem.getRunnable().run();
        }

    }
}
