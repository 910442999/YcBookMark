package com.yc.bookmark.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yc.bookmark.R;
import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.MyBookMarkNoSelectListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcToastUtils;

public class MyBookmarkAddEditFolderActivity extends Activity implements View.OnClickListener {
    private MyBookMarkNoSelectListBean mEditeMyBookMarkNoSelectListBean;
    private String mType;
    private TextView mParentTextView, mSaveButton, title;
    private EditText mFolderTitle;
    private BookmarkDataUtils mBookmarkDataUtils;
    private String mParentId;
    private int mUpdataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_bookmark_add_edit_folder);
        initView();
        initData();
    }

    private void initView() {
        title = (TextView) findViewById(R.id.tv_app_title);
        ImageView iv_tv_app_title_back = (ImageView) findViewById(R.id.iv_tv_app_title_back);
        iv_tv_app_title_back.setOnClickListener(this);
        mSaveButton = (TextView) findViewById(R.id.tv_app_title_right);
        mSaveButton.setVisibility(View.VISIBLE);
        mSaveButton.setText("保存");
        mParentTextView = (TextView) findViewById(R.id.parent_folder);//位置面板中的文本
        mFolderTitle = (EditText) findViewById(R.id.folder_title);
    }

    private void initData() {

        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);

        mEditeMyBookMarkNoSelectListBean = (MyBookMarkNoSelectListBean) getIntent().getSerializableExtra("MyBookMarkListBean");
        String parentFolderName = mEditeMyBookMarkNoSelectListBean.getParentFolderName();
        mParentId = mEditeMyBookMarkNoSelectListBean.getParentId();

        mType = getIntent().getStringExtra("type");

        mParentTextView.setOnClickListener(this);
        mSaveButton.setOnClickListener(this);
        if ("addFolder".equals(mType)) {
            title.setText("新建文件夹");
        } else if ("editeFolder".equals(mType)) {
            findViewById(R.id.add_hint).setVisibility(View.GONE);
            title.setText("修改文件夹");
            mUpdataId = getIntent().getIntExtra("updataId", -1);
            mFolderTitle.setText(mEditeMyBookMarkNoSelectListBean.getName());
            mFolderTitle.setSelection(mFolderTitle.getText().length());
            //暂时设置不可启用
            mParentTextView.setEnabled(false);
        }

        mParentTextView.setText(parentFolderName);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_tv_app_title_back) {//新建文件夹中的位置文本
            finish();
        }
        if (v == mParentTextView) {//新建文件夹中的位置文本

        } else if (v == mSaveButton) {
            String title = mFolderTitle.getText().toString().trim();
            if (title.length() == 0) {
                return;
            }
            if ("addFolder".equals(mType)) {
                //                if (mModel.getBookmarkNodeDepth(mParentId.getId()) > 30) {
                //                    ToastUtils.show(this, "超过最大书签允许层数", ToastUtils.Style.TOAST_FAILED);
                //                    return;
                //                }
                //
                //                if (mModel.checkFolderTitleExistByParent(mParentId.getId(), title)) {
                //                    ToastUtils.show(this, "已存在同名文件夹", ToastUtils.Style.TOAST_HINT);
                //                    return;
                //                } else {
                //                    BookmarkId newFolder = mModel.addFolder(mParentId, mModel.getChildCount(mParentId), title);
                //                    ToastUtils.show(this, "保存成功", ToastUtils.Style.TOAST_SUCCESS);
                //                }

                //添加文件夹
                String addBookMarkFolder = mBookmarkDataUtils.addBookMark(mParentId, title, "");

                if ("添加失败".equals(addBookMarkFolder)) {
                    YcToastUtils.normal(this, "添加失败").show();
                    return;
                }
                //保存书签 , 需要区分网络和本地书签 (暂时只作本地)
                YcSPUtils.getInstance("MyBookmark").put("bookmark", addBookMarkFolder);
                YcLogUtils.eTag("tag添加文件夹", addBookMarkFolder);
                YcToastUtils.normal(this, "添加成功").show();
                setResult(RESULT_OK);
                finish();

            } else if ("editeFolder".equals(mType)) {
                //                if (mNewParentId != null && mModel.getBookmarkNodeDepth(mNewParentId.getId()) > 20) {
                //                    ToastUtils.show(this, "超过最大书签允许层数", ToastUtils.Style.TOAST_FAILED);
                //                    return;
                //                }
                //
                //                if (mNewParentId != null && mModel.checkFolderTitleExistByParent(
                //                        mNewParentId.getId(), title)) {
                //                    ToastUtils.show(this, "已存在同名文件夹", ToastUtils.Style.TOAST_HINT);
                //                    return;
                //                } else {
                //                    List<BookmarkId> bookmarkStrings = new ArrayList<>(1);
                //                    bookmarkStrings.add(mFolderId);
                //                    if (mParentId != mNewParentId && mNewParentId != null)
                //                        mModel.moveBookmarks(bookmarkStrings, mNewParentId);
                //                    mModel.setBookmarkTitle(mFolderId, title);
                //                    ToastUtils.show(this, "修改成功", ToastUtils.Style.TOAST_SUCCESS);
                //                }
                if (mEditeMyBookMarkNoSelectListBean != null) {
                    //重新设置数据
                    mEditeMyBookMarkNoSelectListBean.setName(title);
                    //                    basePresenter.editeBookMark(mEditeMyBookMarkListBean);
                    //编辑文件夹
                    String editeBookmarkFolder = mBookmarkDataUtils.editeBookMark(mUpdataId, mEditeMyBookMarkNoSelectListBean);
                    if ("编辑失败".equals(editeBookmarkFolder)) {
                        YcToastUtils.normal(this, "编辑失败").show();
                        return;
                    }

                    //保存书签 , 需要区分网络和本地书签 (暂时只作本地)
                    YcSPUtils.getInstance("MyBookmark").put("bookmark", editeBookmarkFolder);
                    YcLogUtils.eTag("tag编辑文件夹", editeBookmarkFolder);
                    YcToastUtils.normal(this, "编辑成功").show();
                    setResult(RESULT_OK);
                    finish();

                }
            }
        }
    }
}
