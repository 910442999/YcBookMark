package com.yc.bookmark.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;


import com.yc.YcRecyclerViewBaseAdapter.adapter.YcMultiBaseAdapter;
import com.yc.YcRecyclerViewBaseAdapter.base.YcBaseViewHolder;
import com.yc.bookmark.R;
import com.yc.bookmarklibrary.bean.MyBookMarkListBean;
import com.yc.yclibrary.YcConstUtils;
import com.yc.yclibrary.YcGlideUtils;

import java.net.URL;

/**
 * Created by yc on 2018/8/21.
 */

public class MyBookMarkAdapter extends YcMultiBaseAdapter<MyBookMarkListBean> {
    private boolean isEdit = false;
    private Context mContext;

    public MyBookMarkAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void convert(YcBaseViewHolder holder, MyBookMarkListBean data, int position, int viewType) {
        if (viewType == 0) { //是书签
            if (isEdit) {
                holder.setVisibility(R.id.bookmark_checkbox, View.VISIBLE);
                holder.addOnClickListener(R.id.bookmark_checkbox);
                holder.setVisibility(R.id.ib_bookmark_edit, View.VISIBLE);
                holder.addOnClickListener(R.id.ib_bookmark_edit);
            } else {
                holder.setVisibility(R.id.bookmark_checkbox, View.GONE);
                holder.setVisibility(R.id.ib_bookmark_edit, View.GONE);
                holder.addOnClickListener(R.id.tv_bookmark);
            }
            holder.setBgResource(R.id.iv_bookmark_checkbox, data.isSelected() ? R.drawable.checked : R.drawable.bookmark_checkbox_uncheck);
            String url1 = null;
            try {
                URL url = new URL(data.getUrl());
                if (url != null) {
                    url1 = url.getProtocol() + "://" + url.getHost() + "/favicon.ico";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ImageView view = (ImageView) holder.getView(R.id.bookmark_image);
            YcGlideUtils.loadImageView(mContext, url1, view, R.drawable.ic_web_fav, R.drawable.ic_web_fav, YcConstUtils.ALL);
            holder.setText(R.id.tv_bookmark, data.getName());
        } else { //是文件夹
            if (isEdit) {
                holder.setVisibility(R.id.bookmark_folder_checkbox, View.VISIBLE);
                holder.addOnClickListener(R.id.bookmark_folder_checkbox);
                holder.setVisibility(R.id.ib_bookmark_folder_edit, View.VISIBLE);
                holder.addOnClickListener(R.id.ib_bookmark_folder_edit);
            } else {
                holder.setVisibility(R.id.bookmark_folder_checkbox, View.GONE);
                holder.setVisibility(R.id.ib_bookmark_folder_edit, View.GONE);
                holder.addOnClickListener(R.id.tv_bookmark_folder);
            }
            holder.setBgResource(R.id.iv_bookmark_folder_checkbox, data.isSelected() ? R.drawable.checked : R.drawable.bookmark_checkbox_uncheck);
            holder.setText(R.id.tv_bookmark_folder, data.getName());
        }
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == 0) {
            return R.layout.my_book_mark_item_layout;
        }
        return R.layout.my_book_mark_folder_item_layout;
    }

    @Override
    protected int getViewType(int i, MyBookMarkListBean myBookMarkListBean) {
        String type = myBookMarkListBean.getType();
        if ("url".equals(type)) {
            return 0;
        }
        return 1;
    }

    //是编辑
    public void isEdit(boolean edit) {
        isEdit = edit;
    }

    //获得编辑状态
    public boolean getIsEdit() {
        return isEdit;
    }
}
