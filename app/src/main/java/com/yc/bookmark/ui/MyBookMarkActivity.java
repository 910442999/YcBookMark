package com.yc.bookmark.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.YcRecyclerViewBaseAdapter.adapter.YcBaseAdapter;
import com.yc.YcRecyclerViewBaseAdapter.interfaces.OnItemChildClickListener;
import com.yc.bookmark.R;

import com.yc.bookmark.adapter.MyBookMarkAdapter;


import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.bookmarklibrary.bean.MyBookMarkListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcStringUtils;
import com.yc.yclibrary.YcToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookMarkActivity extends AppCompatActivity implements View.OnClickListener, OnItemChildClickListener {
    private RelativeLayout mRlSyncAndFolder;
    private RelativeLayout mRlMoveAndDelete;
    private RecyclerView mRvMyBookmarkRecyclerView;
    private MyBookMarkAdapter mMyBookMarkAdapter;
    List<MyBookMarkListBean> mChildrenBeanList = new ArrayList<>();
    private LinearLayout mLlMyBindBookmark;
    String bookmark = "";
    String folder = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_mark);
    }

    public void initView(View view) {
        mRlSyncAndFolder = view.findViewById(R.id.rl_sync_and_folder);
        mRlMoveAndDelete = view.findViewById(R.id.rl_move_and_delete);
        mRvMyBookmarkRecyclerView = view.findViewById(R.id.rv_my_bookmark_recycler_view);
        mLlMyBindBookmark = view.findViewById(R.id.ll_my_bind_bookmark);

        TextView btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);
        TextView btnAddFolder = view.findViewById(R.id.btnAddFolder);
        btnAddFolder.setOnClickListener(this);
        mMyBookMarkAdapter = new MyBookMarkAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvMyBookmarkRecyclerView.setLayoutManager(linearLayoutManager);
        mRvMyBookmarkRecyclerView.setAdapter(mMyBookMarkAdapter);
        mMyBookMarkAdapter.setOnItemChildClickListener(this);
    }

    public void initData() {
        if (mChildrenBeanList != null) {
            mChildrenBeanList.clear();
        } else {
            mChildrenBeanList = new ArrayList<>();
        }
    }

    //是否是编辑状态 , 不是的话将所有选中清除
    public void editBookmark(boolean edit) {
        YcLogUtils.eTag("tag", "编辑书签");
        if (edit) {
            isShowEdit(true);
        } else {
            for (int i = 0; i < mChildrenBeanList.size(); i++) {
                MyBookMarkListBean myBookMarkListBean = mChildrenBeanList.get(i);
                myBookMarkListBean.setSelected(false);
            }
            isShowEdit(false);
        }
        mMyBookMarkAdapter.setNewData(mChildrenBeanList);
        mMyBookMarkAdapter.notifyDataSetChanged();

    }

    //是否是编辑 并且是否显示底部新建文件夹  或者 是删除
    public void isShowEdit(boolean edit) {
        mMyBookMarkAdapter.isEdit(edit);
        mRlSyncAndFolder.setVisibility(edit ? View.GONE : View.VISIBLE);
        mRlMoveAndDelete.setVisibility(edit ? View.VISIBLE : View.GONE);
    }

    //条目子view的点击事件
    @Override
    public void onItemChildClick(YcBaseAdapter ycBaseAdapter, View view, int i) {
        MyBookMarkListBean myBookMarkListBean = mChildrenBeanList.get(i);
        if (view.getId() == R.id.bookmark_checkbox) {
            if (myBookMarkListBean.isSelected()) {
                myBookMarkListBean.setSelected(false);
                bookmark = "";
            } else {
                myBookMarkListBean.setSelected(true);
                bookmark = "书签";
            }
            mMyBookMarkAdapter.notifyItemChanged(i);
        } else if (view.getId() == R.id.bookmark_folder_checkbox) {
            if (myBookMarkListBean.isSelected()) {
                myBookMarkListBean.setSelected(false);
                folder = "";
            } else {
                myBookMarkListBean.setSelected(true);
                folder = "文件夹";
            }

            mMyBookMarkAdapter.notifyItemChanged(i);
        } else if (view.getId() == R.id.ib_bookmark_edit) {
            //书签
            if ("url".equals(myBookMarkListBean.getType())) {
                Intent intent = new Intent(this, MyBookmarkAddOrEditeActivity.class);
                intent.putExtra("type", "editeBookMark");
                intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkListBean);
                startActivityForResult(intent, 100); //书签和文件相同的code即可
            }
        } else if (view.getId() == R.id.ib_bookmark_folder_edit) {
            //文件夹
            if ("folder".equals(myBookMarkListBean.getType())) {
                Intent intent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
                intent.putExtra("type", "editeFolder");
                intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkListBean);
                startActivityForResult(intent, 100);//书签和文件相同的code即可
            }
        } else if (view.getId() == R.id.tv_bookmark) {
            //            if (!mMyBookMarkAdapter.getIsEdit()) {
            //                Map<String, String> stringMap = new HashMap<>();
            //                stringMap.put("url", myBookMarkListBean.getUrl());
            //                stringMap.put("tabTitle", myBookMarkListBean.getName());
            //                CommonHelper.get().sendEventBusMessage(my_book_mark, stringMap);
            //            }
        } else if (view.getId() == R.id.tv_bookmark_folder) {
            if (!mMyBookMarkAdapter.getIsEdit()) {
                Intent intent = new Intent(this, MyBookmarkChildFolderActivity.class);
                intent.putExtra("bookMarkChildFolder", (Serializable) myBookMarkListBean);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnDelete) {
            String message = "删除选中";
            if (!YcStringUtils.isEmpty(bookmark) && !YcStringUtils.isEmpty(folder)) {
                message = message + bookmark + "和" + folder;
            } else if (!YcStringUtils.isEmpty(bookmark)) {
                message = message + bookmark;
            } else if (!YcStringUtils.isEmpty(folder)) {
                message = message + folder;
            }

            YcToastUtils.normal(this, message).show();

            showBindBookmark();

        } else if (id == R.id.btnAddFolder) {
            //新添加文件夹
            MyBookMarkListBean myBookMarkListBean = new MyBookMarkListBean();
            myBookMarkListBean.setParentFolderName("书签 ");
            Intent intent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
            intent.putExtra("type", "addFolder");
            intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkListBean);
            startActivityForResult(intent, 100);//书签和文件相同的code即可
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            if (requestCode == 100) {
                if (data.getBooleanExtra("editeBookMark", false) || data.getBooleanExtra("addFolder", false) || data.getBooleanExtra("editeFolder", false)) {
                    //                List<ChildrenBean> childrenBeanList = (List<ChildrenBean>) data.getSerializableExtra("editeBookMark");
                    //                    try {
                    //                        List<ChildrenBean> children = BookmarkDataUtils.getJsonValueBean().getRoots().getBookmark_bar().getChildren();
                    //                        isShowEdit(false);
                    //                        CommonHelper.get().sendEventBusMessage(Constants.edit_book_mark);
                    //                        updataMyBookMarkData(children);
                    //                    } catch (Exception e) {
                    //                        e.printStackTrace();
                    //                    }
                }
            }
        }
    }


//    private void updataMyBookMarkData(List apiResult) {
//        mChildrenBeanList.clear();
//        List childNoSelectBookmarkData = BookmarkDataUtils.getChildNoSelectBookmarkData("1", "书签", apiResult);
//        //        mChildrenBeanList.addAll(apiResult);
//        mChildrenBeanList.addAll(childNoSelectBookmarkData);
//
//        mMyBookMarkAdapter.setNewData(mChildrenBeanList);
//        mMyBookMarkAdapter.notifyDataSetChanged();
//        showBindBookmark();
//    }

    //显示绑定标签
    public void showBindBookmark() {
        if (mChildrenBeanList != null && mChildrenBeanList.size() > 0) {
            mLlMyBindBookmark.setVisibility(View.GONE);
        } else {
            mLlMyBindBookmark.setVisibility(View.VISIBLE);
        }
    }
}
