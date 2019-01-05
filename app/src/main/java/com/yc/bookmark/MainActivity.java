package com.yc.bookmark;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.yc.bookmark.ui.MyBookMarkActivity;
import com.yc.bookmarklibrary.BookmarkDataUtils;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;
import com.yc.yclibrary.YcUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BookmarkDataUtils mBookmarkDataUtils;
    private EditText mAddName;
    private EditText mAddUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YcUtils.init(this);

        findViewById(R.id.add_empty).setOnClickListener(this);
        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.my_bookmark_list).setOnClickListener(this);

        mAddName = findViewById(R.id.add_name);
        mAddUrl = findViewById(R.id.add_url);
        mBookmarkDataUtils = new BookmarkDataUtils(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_empty:
                String add_empty = YcSPUtils.getInstance("MyBookmark").getString("bookmark");
                mBookmarkDataUtils.setBookmark(add_empty);
                YcLogUtils.eTag("tag", mBookmarkDataUtils.getBookmark());

                break;

            case R.id.add:

                String add_name = mAddName.getText().toString().trim();
                String add_url = mAddUrl.getText().toString().trim();

                //获取书签 , 需要区分网络和本地书签 (暂时只作本地)
                String add_bookmark = YcSPUtils.getInstance("MyBookmark").getString("bookmark");
                mBookmarkDataUtils.setBookmark(add_bookmark);

                //添加书签
                add_bookmark = mBookmarkDataUtils.addBookMark("1", add_name, add_url);

                //保存书签 , 需要区分网络和本地书签 (暂时只作本地)
                YcSPUtils.getInstance("MyBookmark").put("bookmark", add_bookmark);

                mBookmarkDataUtils.setBookmark(add_bookmark);
                YcLogUtils.eTag("tag", add_bookmark);
                break;

            case R.id.my_bookmark_list:
                Intent intent = new Intent(this, MyBookMarkActivity.class);
                startActivity(intent);
                break;


        }
    }
}
