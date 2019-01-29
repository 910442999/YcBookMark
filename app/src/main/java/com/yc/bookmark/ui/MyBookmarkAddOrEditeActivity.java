package com.yc.bookmark.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yc.bookmark.R;
import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.bookmarklibrary.bean.MyBookMarkNoSelectListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;
import com.yc.yclibrary.YcToastUtils;

import java.util.ArrayList;
import java.util.List;

public class MyBookmarkAddOrEditeActivity extends Activity implements TextWatcher, View.OnClickListener {
    private EditText mTitleEditText;
    private EditText mUrlEditText;
    private TextView mTvAppTitleRight;
    private TextView mTvAppTitle;
    private TextView mFolderTextView;
    private LinearLayout mFolderSelectPanel;
    private String mBookmarkTitle;
    private String mBookmarkUrl;
    private String mParentId = "";
    private String mType;
    private MyBookMarkNoSelectListBean mEditeMyBookMarkNoSelectListBean;      //编辑书签的bean类
    private BookmarkDataUtils mBookmarkDataUtils;
    private int mUpdataId;
    private boolean mBackCleanUpBookMark;

    //是否移动文件夹位置
    boolean isMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_bookmark_add_or_edite);
        initView();
        initData();
    }

    private void initView() {
        mTitleEditText = (EditText) findViewById(R.id.title_text);
        mUrlEditText = (EditText) findViewById(R.id.url_text);
        ImageView iv_tv_app_title_back = (ImageView) findViewById(R.id.iv_tv_app_title_back);
        iv_tv_app_title_back.setOnClickListener(this);
        mTvAppTitle = (TextView) findViewById(R.id.tv_app_title);
        mTvAppTitleRight = (TextView) findViewById(R.id.tv_app_title_right);
        mTvAppTitleRight.setOnClickListener(this);
        mFolderTextView = (TextView) findViewById(R.id.folder_text);//位置面板中的文本
        mFolderSelectPanel = (LinearLayout) findViewById(R.id.folder_select_panel);//位置的整个布局
    }

    private void initData() {

        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);
        mEditeMyBookMarkNoSelectListBean = (MyBookMarkNoSelectListBean) getIntent().getSerializableExtra("MyBookMarkListBean");
        mBookmarkTitle = mEditeMyBookMarkNoSelectListBean.getName();
        mBookmarkUrl = mEditeMyBookMarkNoSelectListBean.getUrl();
        mFolderTextView.setText(mEditeMyBookMarkNoSelectListBean.getParentFolderName());
        mParentId = mEditeMyBookMarkNoSelectListBean.getParentId();

        String parentName = mEditeMyBookMarkNoSelectListBean.getParentFolderName();
        mType = getIntent().getStringExtra("type");
        mBackCleanUpBookMark = getIntent().getBooleanExtra("backCleanUpBookMark", false);

        if ("网络异常".equals(mBookmarkTitle) || "出错了!".equals(mBookmarkTitle))
            mBookmarkTitle = mBookmarkUrl;
        if ("addBookMark".equals(mType)) {
            mTvAppTitle.setText("添加书签");
            //获取书签列表数据(为了保证同步) , 需要区分网络和本地书签 (暂时只作本地)
            String add_bookmark = YcSPUtils.getInstance("MyBookmark").getString("bookmark");
            mBookmarkDataUtils.setBookmark(add_bookmark);

        } else if ("editeBookMark".equals(mType)) {
            mTvAppTitle.setText("编辑书签");
            mUpdataId = getIntent().getIntExtra("updataId", -1);
            mEditeMyBookMarkNoSelectListBean = (MyBookMarkNoSelectListBean) getIntent().getSerializableExtra("MyBookMarkListBean");
            mBookmarkTitle = mEditeMyBookMarkNoSelectListBean.getName();
            mBookmarkUrl = mEditeMyBookMarkNoSelectListBean.getUrl();
        }
        mFolderTextView.setText(YcStringUtils.isEmpty(parentName) ? "书签" : parentName);
        mFolderTextView.setOnClickListener(this);
        mTvAppTitleRight.setText("保存");
        mTvAppTitleRight.setVisibility(View.VISIBLE);
        mTitleEditText.setText(mBookmarkTitle);


        if (mBookmarkTitle != null) {
            mTitleEditText.setSelection(mBookmarkTitle.length());
        }

        mUrlEditText.setText(mBookmarkUrl);
        mTitleEditText.requestFocus();
        mTitleEditText.addTextChangedListener(this);
        mUrlEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        updateSaveButton();
    }

    private void updateSaveButton() {
        boolean enable = !mTitleEditText.getText().toString().isEmpty() && !mUrlEditText.getText().toString().isEmpty();
        if (enable) {
            mTvAppTitleRight.setTextColor(getResources().getColor(R.color.blue_12aaff));
        } else {
            mTvAppTitleRight.setTextColor(getResources().getColor(R.color.colorAccent));
        }
        mTvAppTitleRight.setEnabled(enable);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v == mFolderTextView) {//新建文件夹中的位置文本
            Intent mIntent = new Intent(this, MyBookMarkTreeViewActivity.class);
            startActivityForResult(mIntent, 100);
        } else if (id == R.id.tv_app_title_right) {
            final String title = mTitleEditText.getText().toString().trim();
            if (title.length() > 115) {
                YcToastUtils.normal(this, "长度不能超过115个字符").show();
                return;
            }
            String url = null;
            //            if (mIsAddToNtp || (!mIsAddToNtp && mCurrentAddToType == ADD_TO_NTP)) {
            //                url_tmp = UrlUtilities.fixupUrl(mUrlEditText.getTrimmedText());
            //            } else {
            url = mUrlEditText.getText().toString().trim();
            //            }


            if (url == null || url.isEmpty()) {
                YcToastUtils.normal(this, "请输入正确的网址").show();
                return;
            }

            //判断是添加书签还是编辑书签
            if ("addBookMark".equals(mType)) { //添加书签

                ChildrenBean childrenBean = new ChildrenBean();
                childrenBean.setDate_added(System.currentTimeMillis() + "");
                childrenBean.setName(title);
                childrenBean.setUrl(url);
                //添加文件夹
                childrenBean.setType("url");
                List<ChildrenBean> newAddFolderList = new ArrayList<>();
                childrenBean.setChildren(newAddFolderList);

                //添加书签
                String add_bookmark = mBookmarkDataUtils.addBookMark(mParentId, childrenBean);

                if ("添加失败".equals(add_bookmark)) {
                    YcToastUtils.normal(this, "添加失败").show();
                    return;
                }
                //保存书签 , 需要区分网络和本地书签 (暂时只作本地)
                YcSPUtils.getInstance("MyBookmark").put("bookmark", add_bookmark);
                YcLogUtils.eTag("tag", add_bookmark);
                YcToastUtils.normal(this, "添加成功").show();
                setResult(RESULT_OK);
                finish();
            } else if ("editeBookMark".equals(mType)) { //编辑书签
                if (mEditeMyBookMarkNoSelectListBean != null) {
                    String editeBookmark = "";
                    if (isMove) {
                        //先移除旧数据, 再添加新数据
                        String removeBookMark = mBookmarkDataUtils.removeBookMark(mUpdataId, mEditeMyBookMarkNoSelectListBean);
                        if ("移除失败".equals(removeBookMark)) {
                            YcToastUtils.normal(this, "编辑失败").show();
                            return;
                        }

                        ChildrenBean childrenBean = new ChildrenBean();
                        childrenBean.setDate_added(mEditeMyBookMarkNoSelectListBean.getDate_added());
                        childrenBean.setName(title);
                        childrenBean.setUrl(url);
                        //添加文件夹
                        childrenBean.setType(mEditeMyBookMarkNoSelectListBean.getType());
                        childrenBean.setChildren(mEditeMyBookMarkNoSelectListBean.getChildren());

                        //添加文件夹
                        editeBookmark = mBookmarkDataUtils.addBookMark(mParentId, childrenBean);

                        if ("添加失败".equals(editeBookmark)) {
                            YcToastUtils.normal(this, "编辑失败").show();
                            return;
                        }
                    } else {
                        //重新设置数据
                        mEditeMyBookMarkNoSelectListBean.setName(title);
                        mEditeMyBookMarkNoSelectListBean.setUrl(url);
                        //                    basePresenter.editeBookMark(mEditeMyBookMarkListBean);
                        //编辑文件夹
                        editeBookmark = mBookmarkDataUtils.editeBookMark(mUpdataId, mEditeMyBookMarkNoSelectListBean);
                        if ("编辑失败".equals(editeBookmark)) {
                            YcToastUtils.normal(this, "编辑失败").show();
                            return;
                        }
                    }

                    YcSPUtils.getInstance("MyBookmark").put("bookmark", editeBookmark);
                    YcLogUtils.eTag("tag", editeBookmark);
                    YcToastUtils.normal(this, "编辑成功").show();
                    setResult(RESULT_OK);
                    finish();

                }
            }

            //            if (!mIsAddToNtp) {
            //                if (mCurrentAddToType == ADD_TO_BOOKMARK) {
            //                    if (mModel.getBookmarkNodeDepth(mParentId.getId()) > 31) {
            //                        ToastUtils.show(YywBookmarkAddActivity.this, "超过最大书签允许层数", ToastUtils.Style.TOAST_FAILED);
            //                        return true;
            //                    }
            //
            //                    boolean overwrite = false;
            //                    if (mModel.checkURLExistInParent(mParentId.getId(), url)) {
            //                        final CustomAlertDialog alertDialog = new CustomAlertDialog.Builder(this).
            //                                setMessage("已存在相同书签，是否覆盖？").
            //                                setPositiveButton("覆盖", new DialogInterface.OnClickListener() {
            //                                    @Override
            //                                    public void onClick(DialogInterface dialog, int which) {
            //                                        // TODO Auto-generated method stub
            //                                        dialog.dismiss();
            //                                        if (mModel.addBookmark(mParentId, mModel.getChildCount(mParentId), title, url, true) != null) {
            //                                            ToastUtils.show(YywBookmarkAddActivity.this, "添加成功", ToastUtils.Style.TOAST_SUCCESS);
            //                                            //Toast.makeText(YywBookmarkAddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
            //                                        }
            //                                        finish();
            //                                    }
            //                                }).
            //                                setNegativeButton("取消", new DialogInterface.OnClickListener() {
            //                                    @Override
            //                                    public void onClick(DialogInterface dialog, int which) {
            //                                        // TODO Auto-generated method stub
            //                                        dialog.dismiss();
            //                                        finish();
            //                                    }
            //                                }).create();
            //                        alertDialog.show();
            //                        return true;
            //                    }
            //
            //                    if (mModel.addBookmark(mParentId, mModel.getChildCount(mParentId), title, url, overwrite) != null) {
            //                        ToastUtils.show(this, "添加成功", ToastUtils.Style.TOAST_SUCCESS);
            //                        //Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            //                    }
            //                    finish();
            //                } else if (mCurrentAddToType == ADD_TO_NTP) {
            //                    //should_finish = NewtabManager.getInstance().addItemManul(title, url,this);
            //                } else if (mCurrentAddToType == ADD_TO_DESKTOP) {
            //                    //Utils2.addShortcut(this, Intent.ShortcutIconResource.fromContext(this, R.mipmap.book_default_mark), title, Uri.parse(url));
            //                    addShortCut(url, title);
            //                } else {
            //                    assert (false);
            //                }
            //            } else {//手动输入－－添加首页
            //                //  should_finish = NewtabManager.getInstance().addItemManul(title, url,this);
            //            }


            // if (item == mDeleteButton) {
            //     // Log added for detecting delete button double clicking.
            //     Log.i(TAG, "Delete button pressed by user! isFinishing() == " + isFinishing());

            //     mModel.deleteBookmark(mBookmarkId);
            //     finish();
            //     return true;
            /// }
        } else if (id == R.id.iv_tv_app_title_back) {
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ("addBookMark".equals(mType) && mBackCleanUpBookMark)  //添加书签
            mBookmarkDataUtils.clearData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == 100 && resultCode == RESULT_OK) {
                isMove = true;
                mParentId = data.getStringExtra("ParentId");
                mFolderTextView.setText(data.getStringExtra("Name"));
            }
        }
    }
}
