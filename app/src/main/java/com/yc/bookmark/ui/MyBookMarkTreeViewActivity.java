package com.yc.bookmark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import com.yc.YcRecyclerViewBaseAdapter.base.YcBaseViewHolder;
import com.yc.YcRecyclerViewBaseAdapter.bean.TreeNode;
import com.yc.YcRecyclerViewBaseAdapter.interfaces.OnItemClickListener;
import com.yc.bookmark.R;
import com.yc.bookmark.adapter.YcTreeViewAdapter;
import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.BookmarkBarBean;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;

import java.util.ArrayList;
import java.util.List;

public class MyBookMarkTreeViewActivity extends AppCompatActivity implements OnItemClickListener<TreeNode> {
    private List<TreeNode> mTreeNodeList;
    private BookmarkDataUtils mBookmarkDataUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_mark_tree_view);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mTreeNodeList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        YcTreeViewAdapter ycTreeViewAdapter = new YcTreeViewAdapter(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ycTreeViewAdapter);

        List<ChildrenBean> data = getData();
        if (data == null) {
            YcLogUtils.e("列表为空");
            return;
        }
        getHandleData(0, data);
        Log.e("tag", new Gson().toJson(mTreeNodeList));
        ycTreeViewAdapter.setNewData(mTreeNodeList);
        ycTreeViewAdapter.setOnItemClickListener(this);
    }

    private List<ChildrenBean> getData() {
        String bookmark = YcSPUtils.getInstance("MyBookmark").getString("bookmark");
        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);
        if (!YcStringUtils.isEmpty(bookmark) && mBookmarkDataUtils.IsBookmarkJson(bookmark)) {
            mBookmarkDataUtils.setBookmark(bookmark);
            List<ChildrenBean> bookmarkChildrenData = mBookmarkDataUtils.getBookmarkChildrenData();
            return bookmarkChildrenData;
        } else {
            return null;
        }

    }

    private void getHandleData(int level, List<ChildrenBean> childrenBeanList) {
        for (int i = 0; i < childrenBeanList.size(); i++) {
            //先判断是否是文件夹,不是的话直接添加文件
            ChildrenBean newChildrenBean = childrenBeanList.get(i);
            if ("folder".equals(newChildrenBean.getType())) {
                TreeNode treeNode = new TreeNode();
                treeNode.setName(newChildrenBean.getName());
                treeNode.setType(newChildrenBean.getType());
                treeNode.setId(newChildrenBean.getDate_added());
                treeNode.setLevel(level);
                mTreeNodeList.add(treeNode);
                List<ChildrenBean> children = newChildrenBean.getChildren();
                //说明子类文件夹下面还有文件夹 递归进入
                if (children != null && children.size() > 0) {
                    getHandleData(level + 1, children);
                }
            } else {
                //                TreeNode treeNode = new TreeNode();
                //                treeNode.setName(newChildrenBean.getName());
                //                treeNode.setType(newChildrenBean.getType());
                //                treeNode.setLevel(level);
                //                //添加新的数据到 父类集合
                //                mTreeNodeList.add(treeNode);
            }
        }
    }

    @Override
    public void onItemClick(YcBaseViewHolder viewHolder, TreeNode data, int position) {
        Toast.makeText(this, data.getName(), Toast.LENGTH_SHORT).show();
        Gson gson = new Gson();
        mBookmarkDataUtils.setCurrentBookmarkFolder(gson.toJson(data));

        Intent intent = new Intent();
        intent.putExtra("ParentId", data.getId());
        intent.putExtra("Name", data.getName());
        intent.putExtra("type", data.getType());
        setResult(RESULT_OK, intent);
        finish();
    }
}
