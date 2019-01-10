package com.yc.bookmark.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yc.YcRecyclerViewBaseAdapter.base.YcBaseViewHolder;
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

public class MyBookmarkChildFolderActivity extends Activity implements View.OnClickListener, OnItemLongClickListener<MyBookMarkNoSelectListBean>, OnItemClickListener<MyBookMarkNoSelectListBean> {
    private RelativeLayout mRlSyncAndFolder;
    private RelativeLayout mRlMoveAndDelete;
    private RecyclerView mRvMyBookmarkRecyclerView;
    private MyBookMarkAdapter mMyBookMarkAdapter;
    List<MyBookMarkNoSelectListBean> mChildrenBeanList = new ArrayList<>();
    private LinearLayout mLlMyBindBookmark;
    private TextView mTvAppTitle;
    private String mParentId;
    private String mParentFolderName;
    private LinearLayout mLlAddBookmark;
    private Intent mIntent;
    private EditText mAddName;
    private EditText mAddUrl;
    private BookmarkDataUtils mBookmarkDataUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_bookmark_child_folder);

        initView();
        initData();

    }

    private void initView() {
        ImageView iv_tv_app_title_back = findViewById(R.id.iv_tv_app_title_back);
        iv_tv_app_title_back.setOnClickListener(this);
        mTvAppTitle = findViewById(R.id.tv_app_title);
        mLlAddBookmark = findViewById(R.id.ll_add_bookmark);
        TextView tv_app_title_right = findViewById(R.id.tv_app_title_right);
        tv_app_title_right.setOnClickListener(this);
        tv_app_title_right.setVisibility(View.VISIBLE);
        tv_app_title_right.setText("添加书签");
        findViewById(R.id.add).setOnClickListener(this);
        mAddName = findViewById(R.id.add_name);
        mAddUrl = findViewById(R.id.add_url);
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
        mMyBookMarkAdapter.setOnItemLongClickListener(this);
        mMyBookMarkAdapter.setOnItemClickListener(this);
        //侧滑删除
        skidRemoveItem();
    }

    public void initData() {
        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);
        Intent getIntent = getIntent();
        mParentId = getIntent.getStringExtra("parentId");
        mParentFolderName = getIntent.getStringExtra("parentFolderName");
        mTvAppTitle.setText(mParentFolderName);
        updataMyBookMarkData(mParentId, mParentFolderName);

    }

    /**
     * 根据
     *
     * @param parentId
     * @param folderName
     */
    private void updataMyBookMarkData(String parentId, String folderName) {
        mChildrenBeanList.clear();
        List<ChildrenBean> bookmarkChildrenfolderData = mBookmarkDataUtils.getBookmarkChildrenfolderData(parentId);
        YcLogUtils.eTag("tag", bookmarkChildrenfolderData != null ? bookmarkChildrenfolderData.toString() : "集合为空");
        List childNoSelectBookmarkData = mBookmarkDataUtils.getChildNoSelectBookmarkData(parentId, folderName, bookmarkChildrenfolderData);

        mChildrenBeanList.addAll(childNoSelectBookmarkData);
        mMyBookMarkAdapter.setNewData(mChildrenBeanList);
        mMyBookMarkAdapter.notifyDataSetChanged();
        showBindBookmark();
    }

    //显示绑定标签
    public void showBindBookmark() {
        if (mChildrenBeanList != null && mChildrenBeanList.size() > 0) {
            mLlMyBindBookmark.setVisibility(View.GONE);
        } else {
            mLlMyBindBookmark.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnSync) {
            //模拟删除多条数据
            int size = mChildrenBeanList.size() / 2;
            for (int i = 0; i < size; i++) {
                mChildrenBeanList.get(i).setSelected(true);
            }
            String parentId = mChildrenBeanList.get(0).getParentId();
            String removeListBookMark = mBookmarkDataUtils.removeListBookMark(parentId, mChildrenBeanList);
            if ("移除失败".equals(removeListBookMark)) {
                YcToastUtils.normal(MyBookmarkChildFolderActivity.this, "移除失败").show();
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
            YcToastUtils.normal(MyBookmarkChildFolderActivity.this, "移除列表成功").show();

        } else if (id == R.id.tv_app_title_right) {

            mLlAddBookmark.setVisibility(View.VISIBLE);

        } else if (id == R.id.add) {

            String add_name = mAddName.getText().toString().trim();
            String add_url = mAddUrl.getText().toString().trim();

            MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = new MyBookMarkNoSelectListBean();
            myBookMarkNoSelectListBean.setName(add_name);
            myBookMarkNoSelectListBean.setParentId(mParentId);
            myBookMarkNoSelectListBean.setUrl(add_url);
            myBookMarkNoSelectListBean.setParentFolderName(mParentFolderName);
            mIntent = new Intent(this, MyBookmarkAddOrEditeActivity.class);
            mIntent.putExtra("type", "addBookMark");
            mIntent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);

            startActivityForResult(mIntent, 100);//书签和文件相同的code即可

        } else if (id == R.id.btnAddFolder) {
            //新添加文件夹
            MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = new MyBookMarkNoSelectListBean();
            myBookMarkNoSelectListBean.setParentId(mParentId);
            myBookMarkNoSelectListBean.setParentFolderName(mParentFolderName);
            mIntent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
            mIntent.putExtra("type", "addFolder");
            mIntent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
            startActivityForResult(mIntent, 100);//书签和文件相同的code即可
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
                    YcToastUtils.normal(MyBookmarkChildFolderActivity.this, "移除失败").show();
                    return;
                }
                mChildrenBeanList.remove(pos);
                mMyBookMarkAdapter.setNewData(mChildrenBeanList);
                mMyBookMarkAdapter.notifyDataSetChanged();
                YcSPUtils.getInstance("MyBookmark").put("bookmark", removeBookMark);
                YcLogUtils.eTag("tag", removeBookMark);
                YcToastUtils.normal(MyBookmarkChildFolderActivity.this, "移除成功").show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            updataMyBookMarkData(mParentId, mParentFolderName);
        }
    }

}
