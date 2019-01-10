package com.yc.bookmark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.yc.bookmark.ui.MyBookMarkActivity;
import com.yc.bookmark.ui.MyBookmarkAddEditFolderActivity;
import com.yc.bookmark.ui.MyBookmarkAddOrEditeActivity;
import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.MyBookMarkNoSelectListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcToastUtils;
import com.yc.yclibrary.YcUtils;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BookmarkDataUtils mBookmarkDataUtils;
    private EditText mAddName;
    private EditText mAddUrl;
    private Intent mIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YcUtils.init(this);

        findViewById(R.id.add_empty).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.folder).setOnClickListener(this);
        findViewById(R.id.my_bookmark_list).setOnClickListener(this);

        mAddName = findViewById(R.id.add_name);
        mAddUrl = findViewById(R.id.add_url);
        mBookmarkDataUtils = BookmarkDataUtils.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_empty:
                mBookmarkDataUtils.setBookmark("");
                String add_empty = mBookmarkDataUtils.getBookmark();
                YcSPUtils.getInstance("MyBookmark").put("bookmark", add_empty);
                YcLogUtils.eTag("tag", add_empty);

                YcToastUtils.normal(this, "添加成功 : " + add_empty, Toast.LENGTH_LONG).show();
                break;

            case R.id.add:
                String add_name = mAddName.getText().toString().trim();
                String add_url = mAddUrl.getText().toString().trim();
                MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = new MyBookMarkNoSelectListBean();
                myBookMarkNoSelectListBean.setName(add_name);
                myBookMarkNoSelectListBean.setParentId("");
                myBookMarkNoSelectListBean.setUrl(add_url);
                myBookMarkNoSelectListBean.setParentFolderName("");
                mIntent = new Intent(this, MyBookmarkAddOrEditeActivity.class);
                mIntent.putExtra("type", "addBookMark");
                mIntent.putExtra("backCleanUpBookMark", true); //返回时清理数据
                mIntent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean);
                startActivity(mIntent);
                break;
            case R.id.folder:

                //新添加文件夹
                MyBookMarkNoSelectListBean myBookMarkNoSelectListBean2 = new MyBookMarkNoSelectListBean();
                myBookMarkNoSelectListBean2.setParentFolderName("书签 ");
                myBookMarkNoSelectListBean2.setParentId("");
                mIntent = new Intent(this, MyBookmarkAddEditFolderActivity.class);
                mIntent.putExtra("type", "addFolder");
                mIntent.putExtra("MyBookMarkListBean", (Serializable) myBookMarkNoSelectListBean2);
                startActivityForResult(mIntent, 100);//书签和文件相同的code即可


                break;

            case R.id.my_bookmark_list:
                mIntent = new Intent(this, MyBookMarkActivity.class);
                startActivity(mIntent);
                break;


        }
    }
}
