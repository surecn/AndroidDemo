package com.surecn.demo.opengles;

import com.surecn.demo.FolderActivity;
import com.surecn.demo.domain.ActivityItem;
import com.surecn.demo.domain.Item;
import java.util.ArrayList;
import java.util.List;

public class OpenGLActivity extends FolderActivity {

    @Override
    protected List<Item> getData() {
        List<Item> mData = new ArrayList<Item>();
        mData.add(new ActivityItem("ch03", "OpenES demo", com.surecn.demo.opengles.ch03.AirHockeyActivity.class));
        mData.add(new ActivityItem("ch04", "OpenES demo", com.surecn.demo.opengles.ch04.AirHockeyActivity.class));
        mData.add(new ActivityItem("ch05", "OpenES demo", com.surecn.demo.opengles.ch05.AirHockeyActivity.class));
        mData.add(new ActivityItem("ch06", "OpenES demo", com.surecn.demo.opengles.ch06.AirHockey3DActivity.class));
        mData.add(new ActivityItem("ch07", "OpenES demo", com.surecn.demo.opengles.ch07.AirHockey3DActivity.class));
        mData.add(new ActivityItem("ch08", "OpenES demo", com.surecn.demo.opengles.ch08.AirHockey3DActivity.class));
        return mData;
    }
}
