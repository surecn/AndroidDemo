package com.surecn.demo;

import com.surecn.demo.domain.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * User: surecn(surecn@163.com)
 * Date: 2018-12-19
 * Time: 09:26
 */
public class UIFolderActivity extends FolderActivity {
    @Override
    protected List<Item> getData() {
        return new ArrayList<>();
    }
}
