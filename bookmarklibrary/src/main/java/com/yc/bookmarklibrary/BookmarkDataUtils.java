package com.yc.bookmarklibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.yc.bookmarklibrary.bean.BookmarkBarBean;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.bookmarklibrary.bean.JsonValueBean;
import com.yc.bookmarklibrary.bean.MyBookMarkListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDataUtils {
    private Gson gson = new Gson();
    private Context mContext;

    public BookmarkDataUtils(Context context) {
        mContext = context;
    }

    /**
     * 设置数据
     *
     * @param bookmark 如果书签是空的,则添加模板书签后再保存
     */
    public void setBookmark(String bookmark) {
        if (!IsBookmarkJson(bookmark)) {
            //添加模板书签
            bookmark = getBookMarkTemplateJson();
        }
        YcSPUtils.getInstance("YcBookmark").put("bookmark", bookmark);

    }

    /**
     * 检查是否是书签json格式
     *
     * @param bookmark
     * @return
     */
    public boolean IsBookmarkJson(String bookmark) {
        if (!YcStringUtils.isEmpty(bookmark)) {
            try {
                new JsonParser().parse(bookmark);
                JsonValueBean jsonValueBean = gson.fromJson(bookmark, JsonValueBean.class);
                if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * 获取数据
     */
    public String getBookmark() {
        return YcSPUtils.getInstance("YcBookmark").getString("bookmark");
    }


    /**
     * 清除数据
     */
    public void clearData() {
        YcSPUtils.getInstance("YcBookmark").clear();
    }

    /**
     * 解析并保存书签数据
     *
     * @param json_value
     * @return
     */
    public List<ChildrenBean> getBookmarkChildrenData(String json_value) {
        //解析得到数据
        try {
            JsonValueBean jsonValueBean = gson.fromJson(json_value, JsonValueBean.class);
            if (jsonValueBean != null) {
                if (jsonValueBean.getRoots() != null) {
                    //获取到书签的bean 前面判断都没什么用,能获取到即可
                    BookmarkBarBean bookmark_bar = jsonValueBean.getRoots().getBookmark_bar();
                    //得到父类中的书签集合
                    List<ChildrenBean> children = bookmark_bar.getChildren();
                    return children;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 获取子类未选中数据
     *
     * @param parentId       父类id
     * @param childrenParent 子类集合
     * @return
     */
    public List<MyBookMarkListBean> getChildNoSelectBookmarkData(String parentId, String ParentFolderName, List<ChildrenBean> childrenParent) {

        List<MyBookMarkListBean> myBookMarkListBeans = new ArrayList<>();
        //子书签集合列表
        for (int i = 0; i < childrenParent.size(); i++) {
            //子书签
            ChildrenBean childrenParentBean = childrenParent.get(i);
            MyBookMarkListBean myBookMarkListBean = new MyBookMarkListBean();
            myBookMarkListBean.setDate_added(childrenParentBean.getDate_added());
            myBookMarkListBean.setName(childrenParentBean.getName());
            myBookMarkListBean.setType(childrenParentBean.getType());
            myBookMarkListBean.setUrl(childrenParentBean.getUrl());
            myBookMarkListBean.setSelected(false);
            myBookMarkListBean.setParentId(parentId);
            myBookMarkListBean.setParentFolderName(ParentFolderName);
            //判断是否是文件夹 , 如果是文件夹需要添加 children
            if ("folder".equals(childrenParentBean.getType())) {
                myBookMarkListBean.setChildren(childrenParentBean.getChildren());
            }
            myBookMarkListBeans.add(myBookMarkListBean);

        }
        return myBookMarkListBeans;
    }

    //========================================  添加   ====================================

    //通过原数据新添加书签的json数据
    public String getNewAddBookMarkJson(String parentId, String name, String url, JsonValueBean jsonValueBean) {
        BookmarkBarBean bookmarkBarBean = jsonValueBean.getRoots().getBookmark_bar();
        List<ChildrenBean> newChildrenBeanList = bookmarkBarBean.getChildren();
        ChildrenBean childrenBean = new ChildrenBean();
        childrenBean.setDate_added(System.currentTimeMillis() + "");
        childrenBean.setName(name);
        if (!TextUtils.isEmpty(url)) {
            //添加书签
            childrenBean.setUrl(url);
            childrenBean.setType("url");
        } else {
            //添加文件夹
            childrenBean.setType("folder");
            List<ChildrenBean> newAddFolderList = new ArrayList<>();
            childrenBean.setChildren(newAddFolderList);
        }

        newChildrenBeanList.add(childrenBean);
        bookmarkBarBean.setChildren(newChildrenBeanList);
        jsonValueBean.getRoots().setBookmark_bar(bookmarkBarBean);
        return new Gson().toJson(jsonValueBean);
    }

    /**
     * 添加书签
     * 备注:
     * 如果  parentId,  name,  url 都不为空 解析数据添加书签
     * 否则  都为空数据 ,添加模板书签
     *
     * @param parentId
     * @param name
     * @param url
     * @return
     */
    public String addBookMark(String parentId, String name, String url) {
        String json_value = "添加失败";
        if (!YcStringUtils.isEmpty(parentId) && !YcStringUtils.isEmpty(name) && !YcStringUtils.isEmpty(url)) {
            JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
            if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
                //添加书签
                json_value = getNewAddBookMarkJson(parentId, name, url, jsonValueBean);
            }
        } else {
            //添加模板书签
            json_value = getBookMarkTemplateJson();
        }
        return json_value;
    }

    /**
     * 得到空书签模板Json
     *
     * @return
     */
    public String getBookMarkTemplateJson() {
        if (mContext == null) {
            return "";
        }
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = mContext.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open("book_mark_template.json")));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
            stringBuilder.delete(0, stringBuilder.length());
        }
        return stringBuilder.toString();
    }
    //========================================  添加 end  ====================================


    /**
     * 编辑标签标签
     *
     * @param myBookMarkListBean
     * @return
     */
    public String editBookMark(MyBookMarkListBean myBookMarkListBean) {
        String bookmark = getBookmark();
        JsonValueBean jsonValueBean = gson.fromJson(bookmark, JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
            List<ChildrenBean> childrenBeanList = jsonValueBean.getRoots().getBookmark_bar().getChildren();
            if (childrenBeanList != null && childrenBeanList.size() > 0) {
                for (int i = 0; i < childrenBeanList.size(); i++) {
                    if (myBookMarkListBean.getDate_added().equals(childrenBeanList.get(i).getDate_added())) {

                        ChildrenBean childrenBean = new ChildrenBean();
                        childrenBean.setUrl(myBookMarkListBean.getUrl());
                        childrenBean.setName(myBookMarkListBean.getName());
                        childrenBean.setType(myBookMarkListBean.getType());
                        childrenBean.setDate_added(myBookMarkListBean.getDate_added());
                        //判断是否是文件夹 , 如果是文件夹需要添加 children
                        if ("folder".equals(myBookMarkListBean.getType())) {
                            childrenBean.setChildren(myBookMarkListBean.getChildren());
                        }
                        childrenBeanList.set(i, childrenBean);
                        jsonValueBean.getRoots().getBookmark_bar().setChildren(childrenBeanList);
                    }
                }
            }
        }
        return gson.toJson(jsonValueBean);
    }

    /**
     * 移除标签
     *
     * @param myBookMarkListBeans
     * @return
     */
    public String removeBookMark(List<MyBookMarkListBean> myBookMarkListBeans) {
        String bookmark = YcSPUtils.getInstance("Bookmark").getString("bookmark");
        JsonValueBean jsonValueBean = gson.fromJson(bookmark, JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
            List<ChildrenBean> childrenBeanList = jsonValueBean.getRoots().getBookmark_bar().getChildren();
            if (childrenBeanList != null && childrenBeanList.size() > 0) {

                for (int j = 0; j < myBookMarkListBeans.size(); j++) {
                    MyBookMarkListBean myBookMarkListBean = myBookMarkListBeans.get(j);
                    if (myBookMarkListBean.isSelected()) {
                        for (int i = 0; i < childrenBeanList.size(); i++) {
                            if (myBookMarkListBean.getDate_added().equals(childrenBeanList.get(i).getDate_added())) {
                                childrenBeanList.remove(i);
                            }
                        }
                    }
                }
                jsonValueBean.getRoots().getBookmark_bar().setChildren(childrenBeanList);
            }
        }
        return gson.toJson(jsonValueBean);
    }
}
