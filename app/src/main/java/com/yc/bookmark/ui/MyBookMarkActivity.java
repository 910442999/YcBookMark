package com.yc.bookmark.ui;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yc.YcRecyclerViewBaseAdapter.adapter.YcBaseAdapter;
import com.yc.YcRecyclerViewBaseAdapter.base.YcBaseViewHolder;
import com.yc.YcRecyclerViewBaseAdapter.interfaces.OnItemChildClickListener;
import com.yc.YcRecyclerViewBaseAdapter.interfaces.OnItemClickListener;
import com.yc.YcRecyclerViewBaseAdapter.interfaces.OnItemLongClickListener;
import com.yc.bookmark.R;

import com.yc.bookmark.adapter.MyBookMarkAdapter;


import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.bookmarklibrary.bean.MyBookMarkNoSelectListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;
import com.yc.yclibrary.YcToastUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyBookMarkActivity extends AppCompatActivity implements View.OnClickListener, OnItemChildClickListener, OnItemLongClickListener<MyBookMarkNoSelectListBean>, OnItemClickListener<MyBookMarkNoSelectListBean> {
    private RelativeLayout mRlSyncAndFolder;
    private RelativeLayout mRlMoveAndDelete;
    private RecyclerView mRvMyBookmarkRecyclerView;
    private MyBookMarkAdapter mMyBookMarkAdapter;
    List<MyBookMarkNoSelectListBean> mChildrenBeanList = new ArrayList<>();
    private LinearLayout mLlMyBindBookmark;
    String bookmark = "";
    String folder = "";
    private BookmarkDataUtils mBookmarkDataUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_mark);
        initView();
        initData();
    }

    public void initView() {
        mRlSyncAndFolder = findViewById(R.id.rl_sync_and_folder);
        mRlMoveAndDelete = findViewById(R.id.rl_move_and_delete);
        mRvMyBookmarkRecyclerView = findViewById(R.id.rv_my_bookmark_recycler_view);
        mLlMyBindBookmark = findViewById(R.id.ll_my_bind_bookmark);

        TextView btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(this);

        TextView btnSync = findViewById(R.id.btnSync);
        btnSync.setOnClickListener(this);
        TextView btnAddFolder = findViewById(R.id.btnAddFolder);
        btnAddFolder.setOnClickListener(this);

        mMyBookMarkAdapter = new MyBookMarkAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvMyBookmarkRecyclerView.setLayoutManager(linearLayoutManager);
        mRvMyBookmarkRecyclerView.setAdapter(mMyBookMarkAdapter);
        mMyBookMarkAdapter.setOnItemChildClickListener(this);
        mMyBookMarkAdapter.setOnItemLongClickListener(this);
        mMyBookMarkAdapter.setOnItemClickListener(this);
        //侧滑删除
        skidRemoveItem();
    }

    public void initData() {
        if (mChildrenBeanList != null) {
            mChildrenBeanList.clear();
        } else {
            mChildrenBeanList = new ArrayList<>();
        }

        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);
        updataMyBookMarkData();

    }

    //是否是编辑状态 , 不是的话将所有选中清除
    public void editBookmark(boolean edit) {
        YcLogUtils.eTag("tag", "编辑书签");
        if (edit) {
            isShowEdit(true);
        } else {
            for (int i = 0; i < mChildrenBeanList.size(); i++) {
                MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = mChildrenBeanList.get(i);
                myBookMarkNoSelectListBean.setSelected(false);
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
        MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = mChildrenBeanList.get(i);
        if (view.getId() == R.id.bookmark_checkbox) {
            if (myBookMarkNoSelectListBean.isSelected()) {
                myBookMarkNoSelectListBean.setSelected(false);
                bookmark = "";
            } else {
                myBookMarkNoSelectListBean.setSelected(true);
                bookmark = "书签";
            }
            mMyBookMarkAdapter.notifyItemChanged(i);
        } else if (view.getId() == R.id.bookmark_folder_checkbox) {
            if (myBookMarkNoSelectListBean.isSelected()) {
                myBookMarkNoSelectListBean.setSelected(false);
                folder = "";
            } else {
                myBookMarkNoSelectListBean.setSelected(true);
                folder = "文件夹";
            }

            mMyBookMarkAdapter.notifyItemChanged(i);
        } else if (view.getId() == R.id.ib_bookmark_edit) {
            //书签
            if ("url".equals(myBookMarkNoSelectListBean.getType())) {
                Intent intent = new Intent(this, MyBookmarkAddOrEditeActivity.class);
                intent.putExtra("type", "editeBookMark");
                intent.putExtra("updataId", i);
                intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
                startActivityForResult(intent, 100); //书签和文件相同的code即可
            }
        } else if (view.getId() == R.id.ib_bookmark_folder_edit) {
            //文件夹
            if ("folder".equals(myBookMarkNoSelectListBean.getType())) {
                Intent intent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
                intent.putExtra("type", "editeFolder");
                intent.putExtra("updataId", i);
                intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
                startActivityForResult(intent, 100);//书签和文件相同的code即可
            }
        } else if (view.getId() == R.id.tv_bookmark) {
            YcToastUtils.normal(this, "当前连接 : " + myBookMarkNoSelectListBean.getUrl()).show();
            //            if (!mMyBookMarkAdapter.getIsEdit()) {
            //                Map<String, String> stringMap = new HashMap<>();
            //                stringMap.put("url", myBookMarkListBean.getUrl());
            //                stringMap.put("tabTitle", myBookMarkListBean.getName());
            //                CommonHelper.get().sendEventBusMessage(my_book_mark, stringMap);
            //            }
        } else if (view.getId() == R.id.tv_bookmark_folder) {
            if (!mMyBookMarkAdapter.getIsEdit()) {
                Intent intent = new Intent(this, MyBookmarkChildFolderActivity.class);
                intent.putExtra("parentId", myBookMarkNoSelectListBean.getDate_added());
                intent.putExtra("parentFolderName", myBookMarkNoSelectListBean.getParentFolderName());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemClick(YcBaseViewHolder viewHolder, MyBookMarkNoSelectListBean myBookMarkNoSelectListBean, int position) {

        if ("url".equals(myBookMarkNoSelectListBean.getType())) {
            YcToastUtils.normal(this, "当前连接 : " + myBookMarkNoSelectListBean.getUrl()).show();

        } else if ("folder".equals(myBookMarkNoSelectListBean.getType())) {
            //文件夹
            Intent intent = new Intent(this, MyBookmarkChildFolderActivity.class);
            intent.putExtra("parentId", myBookMarkNoSelectListBean.getDate_added());
            intent.putExtra("parentFolderName", myBookMarkNoSelectListBean.getParentFolderName());
            startActivityForResult(intent, 100);//书签和文件相同的code即可
        }
    }

    @Override
    public void onItemLongClick(YcBaseViewHolder viewHolder, MyBookMarkNoSelectListBean myBookMarkNoSelectListBean, int position) {

        if ("url".equals(myBookMarkNoSelectListBean.getType())) {
            //书签
            Intent intent = new Intent(this, MyBookmarkAddOrEditeActivity.class);
            intent.putExtra("type", "editeBookMark");
            intent.putExtra("updataId", position);
            intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
            startActivityForResult(intent, 100); //书签和文件相同的code即可
        } else if ("folder".equals(myBookMarkNoSelectListBean.getType())) {
            //文件夹
            Intent intent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
            intent.putExtra("type", "editeFolder");
            intent.putExtra("updataId", position);
            intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
            startActivityForResult(intent, 100);//书签和文件相同的code即可
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnSync) {
            //模拟删除多条数据
            int size = mChildrenBeanList.size() / 2;
            for (int i = 0; i < size; i++) {
                mChildrenBeanList.get(i).setSelected(true);
            }
            String parentId = mChildrenBeanList.get(0).getParentId();
            String removeListBookMark = mBookmarkDataUtils.removeListBookMark(parentId, mChildrenBeanList);
            if ("移除失败".equals(removeListBookMark)) {
                YcToastUtils.normal(MyBookMarkActivity.this, "移除失败").show();
                return;
            }

            List<MyBookMarkNoSelectListBean> childrenBeanList = new ArrayList<>();
            childrenBeanList.addAll(mChildrenBeanList);
            //模拟移除选中书签
            for (int i = 0; i < childrenBeanList.size(); i++) {
                if (childrenBeanList.get(i).isSelected()) {
                    for (int j = 0; j < mChildrenBeanList.size(); j++) {
                        if (childrenBeanList.get(i).getDate_added().equals(mChildrenBeanList.get(j).getDate_added())) {
                            mChildrenBeanList.remove(j);
                        }
                    }
                }
            }

            mMyBookMarkAdapter.setNewData(mChildrenBeanList);
            mMyBookMarkAdapter.notifyDataSetChanged();
            YcSPUtils.getInstance("MyBookmark").put("bookmark", removeListBookMark);
            YcLogUtils.eTag("tag", removeListBookMark);
            YcToastUtils.normal(MyBookMarkActivity.this, "移除列表成功").show();

        } else if (id == R.id.btnDelete) {
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
            MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = new MyBookMarkNoSelectListBean();
            myBookMarkNoSelectListBean.setParentFolderName("书签 ");
            myBookMarkNoSelectListBean.setParentId("");
            Intent intent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
            intent.putExtra("type", "addFolder");
            intent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
            startActivityForResult(intent, 100);//书签和文件相同的code即可
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            updataMyBookMarkData();
        }
    }


    private void updataMyBookMarkData() {
        mChildrenBeanList.clear();
        String bookmark = YcSPUtils.getInstance("MyBookmark").getString("bookmark");
        if (!YcStringUtils.isEmpty(bookmark) && mBookmarkDataUtils.IsBookmarkJson(bookmark)) {
            mBookmarkDataUtils.setBookmark(bookmark);
            List<ChildrenBean> bookmarkChildrenData = mBookmarkDataUtils.getBookmarkChildrenData();
            YcLogUtils.eTag("tag", bookmarkChildrenData);
            List childNoSelectBookmarkData = mBookmarkDataUtils.getChildNoSelectBookmarkData("", "书签", bookmarkChildrenData);
            mChildrenBeanList.addAll(childNoSelectBookmarkData);
            mMyBookMarkAdapter.setNewData(mChildrenBeanList);
            mMyBookMarkAdapter.notifyDataSetChanged();
            showBindBookmark();
        } else {
            YcLogUtils.e("列表为空");
        }
    }

    //显示绑定标签
    public void showBindBookmark() {
        if (mChildrenBeanList != null && mChildrenBeanList.size() > 0) {
            mLlMyBindBookmark.setVisibility(View.GONE);
        } else {
            mLlMyBindBookmark.setVisibility(View.VISIBLE);
        }
    }

    private void skidRemoveItem() {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(1, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //滑动时的一些操作
                return true;
            }

            /**
             * 处理滑动事件回调
             *
             * @param viewHolder
             * @param direction
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int pos = viewHolder.getAdapterPosition();
                String removeBookMark = mBookmarkDataUtils.removeBookMark(pos, mChildrenBeanList.get(pos));
                if ("移除失败".equals(removeBookMark)) {
                    YcToastUtils.normal(MyBookMarkActivity.this, "移除失败").show();
                    return;
                }
                mChildrenBeanList.remove(pos);
                mMyBookMarkAdapter.setNewData(mChildrenBeanList);
                mMyBookMarkAdapter.notifyDataSetChanged();
                YcSPUtils.getInstance("MyBookmark").put("bookmark", removeBookMark);
                YcLogUtils.eTag("tag", removeBookMark);
                YcToastUtils.normal(MyBookMarkActivity.this, "移除成功").show();
            }

            //处理动画
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int adapterPosition = viewHolder.getAdapterPosition();
                //判断是否是第一条 ,禁止滑动删除
                //                if (adapterPosition != 0) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    //设置滑动条目是视图跟随手指滑动 ， 否则条目静止状态
                    viewHolder.itemView.setTranslationX(dX);
                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                //                }
            }
        });
        touchHelper.attachToRecyclerView(mRvMyBookmarkRecyclerView);
    }

}
