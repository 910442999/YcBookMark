package com.yc.bookmarklibrary;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.yc.bookmarklibrary.bean.BookmarkBarBean;
import com.yc.bookmarklibrary.bean.ChildrenBean;
import com.yc.bookmarklibrary.bean.JsonValueBean;
import com.yc.bookmarklibrary.bean.MyBookMarkNoSelectListBean;
import com.yc.yclibrary.YcLogUtils;
import com.yc.yclibrary.YcSPUtils;
import com.yc.yclibrary.YcStringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BookmarkDataUtils {
    private static BookmarkDataUtils sBookmarkDataUtils;
    private static Gson gson;
    private Context mContext;

    private boolean isRecursive = true;

    public BookmarkDataUtils(Context context) {
        mContext = context;
    }

    public static synchronized BookmarkDataUtils getInstance(Context context) {
        if (sBookmarkDataUtils == null)
            synchronized (BookmarkDataUtils.class) {
                if (sBookmarkDataUtils == null) {
                    sBookmarkDataUtils = new BookmarkDataUtils(context);
                    gson = new Gson();
                }
            }
        return sBookmarkDataUtils;
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
     * 设置当前书签文件夹
     */
    public void setCurrentBookmarkFolder(String CurrentBookmarkFolder) {
        YcSPUtils.getInstance("YcBookmark").put("CurrentBookmarkFolder", CurrentBookmarkFolder);
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
     * 获取当前书签文件夹
     */
    public String getCurrentBookmarkFolder() {
        return YcSPUtils.getInstance("YcBookmark").getString("CurrentBookmarkFolder");
    }


    /**
     * 清除数据
     */
    public void clearData() {
        sBookmarkDataUtils = null;
        gson = null;
        YcSPUtils.getInstance("YcBookmark").clear();
    }

    /**
     * 获取最外层父类书签数据
     *
     * @return
     */
    public List<ChildrenBean> getBookmarkChildrenData() {
        //解析得到数据
        try {
            JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
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
     * 获取子类数据
     *
     * @param Id 父类id  如果是空的直接将父类书签列表返回
     * @return
     */
    public List<ChildrenBean> getBookmarkChildrenfolderData(String Id) {
        List<ChildrenBean> childrenParent = getBookmarkChildrenData();
        //直接将父类书签返回
        if (YcStringUtils.isEmpty(Id)) {
            //获取父类书签
            return childrenParent;
        } else {
            isRecursive = true;
            List<ChildrenBean> newChildrenChildrenBeans = recursiveBookmarkChildren(Id, childrenParent);
            return newChildrenChildrenBeans;
        }
    }


    public List<ChildrenBean> recursiveBookmarkChildren(String childrenId, List<ChildrenBean> childrenParent) {
        List<ChildrenBean> childrenChildren = new ArrayList<>();
        //书签集合列表
        for (int i = 0; i < childrenParent.size(); i++) {
            if (isRecursive) {
                ChildrenBean childrenBean = childrenParent.get(i);
                if (childrenId.equals(childrenBean.getDate_added())) {
                    isRecursive = false;
                    childrenChildren.clear();
                    childrenChildren.addAll(childrenBean.getChildren());
                    return childrenChildren;
                } else {
                    //不是匹配子类中的文件夹
                    List<ChildrenBean> childrenChildren2 = childrenBean.getChildren();
                    //说明子类文件夹下面还有文件夹 递归进入
                    if (childrenChildren2 != null && childrenChildren2.size() > 0) {
                        List<ChildrenBean> childrenBeans = recursiveBookmarkChildren(childrenId, childrenChildren2);
                        childrenChildren.clear();
                        childrenChildren.addAll(childrenBeans);
                    }
                }
            }
        }
        return childrenChildren;
    }

    /**
     * 获取子类未选中数据
     *
     * @param parentId         父类id
     * @param ParentFolderName 父类文件夹名字
     * @return
     */
    public List<MyBookMarkNoSelectListBean> getChildNoSelectBookmarkData(String parentId, String ParentFolderName, List<ChildrenBean> childrenParent) {
        List<MyBookMarkNoSelectListBean> myBookMarkNoSelectListBeans = new ArrayList<>();
        //子书签集合列表
        for (int i = 0; i < childrenParent.size(); i++) {
            //子书签
            ChildrenBean childrenParentBean = childrenParent.get(i);
            MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = new MyBookMarkNoSelectListBean();
            myBookMarkNoSelectListBean.setDate_added(childrenParentBean.getDate_added());
            myBookMarkNoSelectListBean.setName(childrenParentBean.getName());
            myBookMarkNoSelectListBean.setType(childrenParentBean.getType());
            myBookMarkNoSelectListBean.setUrl(childrenParentBean.getUrl());
            myBookMarkNoSelectListBean.setSelected(false);
            myBookMarkNoSelectListBean.setParentId(parentId);
            myBookMarkNoSelectListBean.setParentFolderName(ParentFolderName);
            //判断是否是文件夹 , 如果是文件夹需要添加 children
            if ("folder".equals(childrenParentBean.getType())) {
                myBookMarkNoSelectListBean.setChildren(childrenParentBean.getChildren());
            }
            myBookMarkNoSelectListBeans.add(myBookMarkNoSelectListBean);
        }
        return myBookMarkNoSelectListBeans;
    }

    //========================================  添加   ====================================


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


    /**
     * 添加书签
     * 备注:
     * 如果  parentId,  name,  url 都不为空 解析数据添加书签
     * 否则  都为空数据 ,添加模板书签
     *
     * @param parentId 为空则是最外层父类 , 不为空则为子类文件夹
     * @return 添加后的数据集json
     */
    public String addBookMark(String parentId, ChildrenBean childrenBean) {
        JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {

            BookmarkBarBean bookmarkBarBean = jsonValueBean.getRoots().getBookmark_bar();
            List<ChildrenBean> childrenBeanList = bookmarkBarBean.getChildren();

            //最外层父类 直接添加数据即可
            if (YcStringUtils.isEmpty(parentId)) {
                childrenBeanList.add(childrenBean);
                bookmarkBarBean.setChildren(childrenBeanList);
            } else {
                //说明是子类文件夹 (根据父类文件夹id 查找相对应的文件夹并添加数据)
                bookmarkBarBean.setChildren(recursiveAddBookmark(parentId, childrenBeanList, childrenBean));
            }
            jsonValueBean.getRoots().setBookmark_bar(bookmarkBarBean);
            String toJson = gson.toJson(jsonValueBean);
            //保存新的数据
            setBookmark(toJson);
            //添加书签
            return toJson;
        }
        return "添加失败";
    }

    /**
     * 递归查找匹配的文件夹  并添加数据
     *
     * @param parentId         父类id
     * @param childrenBeanList 要查询的集合
     * @param addChildrenBean  添加的数据集
     * @return
     */
    private List<ChildrenBean> recursiveAddBookmark(String parentId, List<ChildrenBean> childrenBeanList, ChildrenBean addChildrenBean) {
        List<ChildrenBean> newChildrenBeanList = new ArrayList<>(); //创建新的集合用来保存数据
        for (int i = 0; i < childrenBeanList.size(); i++) {
            //先判断是否是文件夹,不是的话忽略
            ChildrenBean newChildrenBean = childrenBeanList.get(i);
            if ("folder".equals(newChildrenBean.getType())) {
                //判断是否匹配当前 父类id (匹配说明是当前父类id下的)
                if (parentId.equals(newChildrenBean.getDate_added())) {
                    List<ChildrenBean> children = newChildrenBean.getChildren();
                    children.add(addChildrenBean);
                    newChildrenBean.setChildren(children);
                } else {
                    //不是匹配子类中的文件夹
                    List<ChildrenBean> children = newChildrenBean.getChildren();
                    //说明子类文件夹下面还有文件夹 递归进入
                    if (children != null && children.size() > 0) {
                        List<ChildrenBean> newChildrenChildrenBeans = recursiveAddBookmark(parentId, children, addChildrenBean);
                        newChildrenBean.setChildren(newChildrenChildrenBeans);
                    }
                }
            }
            //添加新的数据到 父类集合
            newChildrenBeanList.add(newChildrenBean);
        }
        return newChildrenBeanList;
    }

    //========================================  添加 end  ====================================


    /**
     * 编辑标签标签
     *
     * @param myBookMarkNoSelectListBean
     * @return
     */
    public String editeBookMark(int updataId, MyBookMarkNoSelectListBean myBookMarkNoSelectListBean) {
        JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
            BookmarkBarBean bookmarkBarBean = jsonValueBean.getRoots().getBookmark_bar();
            List<ChildrenBean> childrenBeanList = bookmarkBarBean.getChildren();

            //======================== 先创建新的数据bean类 等待添加  start =================================
            ChildrenBean childrenBean = new ChildrenBean();
            String date_added = myBookMarkNoSelectListBean.getDate_added();
            childrenBean.setDate_added(date_added);
            childrenBean.setName(myBookMarkNoSelectListBean.getName());
            if (!TextUtils.isEmpty(myBookMarkNoSelectListBean.getUrl())) {
                //编辑书签
                childrenBean.setUrl(myBookMarkNoSelectListBean.getUrl());
                childrenBean.setType("url");
            } else {
                //编辑文件夹
                childrenBean.setType("folder");
                childrenBean.setChildren(myBookMarkNoSelectListBean.getChildren());
            }

            //======================== 先创建新的数据bean类 等待添加  end =================================
            String parentId = myBookMarkNoSelectListBean.getParentId();
            //最外层父类 直接替换
            // 数据即可
            if (YcStringUtils.isEmpty(parentId)) {
                for (int i = 0; i < childrenBeanList.size(); i++) {
                    ChildrenBean childrenBean1 = childrenBeanList.get(i);
                    if (date_added.equals(childrenBean1.getDate_added())) {
                        childrenBeanList.set(i, childrenBean);
                    }
                }
                bookmarkBarBean.setChildren(childrenBeanList);
            } else {
                //说明是子类文件夹 (根据父类文件夹id 查找相对应的文件夹并添加数据)
                bookmarkBarBean.setChildren(recursiveEditeBookmark(parentId, updataId, childrenBeanList, childrenBean));
            }
            jsonValueBean.getRoots().setBookmark_bar(bookmarkBarBean);
            String toJson = gson.toJson(jsonValueBean);
            //保存新的数据
            setBookmark(toJson);
            //添加书签
            return toJson;

        }
        return "编辑失败";
    }

    /**
     * 递归查找匹配的文件夹  并编辑数据
     *
     * @param parentId          父类id
     * @param childrenBeanList  要查询的集合
     * @param editeChildrenBean 编辑的数据集
     * @return
     */
    private List<ChildrenBean> recursiveEditeBookmark(String parentId, int updataId, List<ChildrenBean> childrenBeanList, ChildrenBean editeChildrenBean) {
        List<ChildrenBean> newChildrenBeanList = new ArrayList<>(); //创建新的集合用来保存数据
        for (int i = 0; i < childrenBeanList.size(); i++) {
            //先判断是否是文件夹,不是的话忽略
            ChildrenBean newChildrenBean = childrenBeanList.get(i);
            if (parentId.equals(newChildrenBean.getDate_added())) {
                List<ChildrenBean> children = newChildrenBean.getChildren();
                children.set(updataId, editeChildrenBean);
                newChildrenBean.setChildren(children);
            } else {
                //不是匹配子类中的文件夹
                List<ChildrenBean> children = newChildrenBean.getChildren();
                //说明子类文件夹下面还有文件夹 递归进入
                if (children != null && children.size() > 0) {
                    List<ChildrenBean> newChildrenChildrenBeans = recursiveEditeBookmark(parentId, updataId, children, editeChildrenBean);
                    newChildrenBean.setChildren(newChildrenChildrenBeans);
                }
            }
            newChildrenBeanList.add(newChildrenBean);
        }
        return newChildrenBeanList;
    }

    /**
     * 移除单个标签
     *
     * @param removeId                   要移除的id
     * @param myBookMarkNoSelectListBean 删除的数据集
     * @return
     */
    public String removeBookMark(int removeId, MyBookMarkNoSelectListBean myBookMarkNoSelectListBean) {
        JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
            BookmarkBarBean bookmarkBarBean = jsonValueBean.getRoots().getBookmark_bar();
            List<ChildrenBean> childrenBeanList = bookmarkBarBean.getChildren();
            String parentId = myBookMarkNoSelectListBean.getParentId();
            if (YcStringUtils.isEmpty(parentId)) {
                childrenBeanList.remove(removeId);
                bookmarkBarBean.setChildren(childrenBeanList);
            } else {
                //说明是子类文件夹 (根据父类文件夹id 查找相对应的文件夹并添加数据)
                bookmarkBarBean.setChildren(recursiveRemoveBookmark(parentId, removeId, childrenBeanList, myBookMarkNoSelectListBean));
            }
            jsonValueBean.getRoots().setBookmark_bar(bookmarkBarBean);
            String toJson = gson.toJson(jsonValueBean);
            //保存新的数据
            setBookmark(toJson);
            //添加书签
            return toJson;
        }

        return "移除失败";
    }

    /**
     * 移除列表标签
     *
     * @param parentId                    父类id
     * @param myBookMarkNoSelectListBeans 删除的数据集合
     * @return
     */
    public String removeListBookMark(String parentId, List<MyBookMarkNoSelectListBean> myBookMarkNoSelectListBeans) {
        JsonValueBean jsonValueBean = gson.fromJson(getBookmark(), JsonValueBean.class);
        if (jsonValueBean != null && jsonValueBean.getRoots() != null && jsonValueBean.getRoots().getBookmark_bar() != null) {
            BookmarkBarBean bookmarkBarBean = jsonValueBean.getRoots().getBookmark_bar();
            List<ChildrenBean> childrenBeanList = bookmarkBarBean.getChildren();
            if (YcStringUtils.isEmpty(parentId)) {
                for (int j = 0; j < myBookMarkNoSelectListBeans.size(); j++) {
                    MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = myBookMarkNoSelectListBeans.get(j);
                    if (myBookMarkNoSelectListBean.isSelected()) {
                        for (int i = 0; i < childrenBeanList.size(); i++) {
                            if (myBookMarkNoSelectListBean.getDate_added().equals(childrenBeanList.get(i).getDate_added())) {
                                childrenBeanList.remove(i);
                            }
                        }
                    }
                }
                bookmarkBarBean.setChildren(childrenBeanList);
            } else {
                //说明是子类文件夹 (根据父类文件夹id 查找相对应的文件夹并添加数据)
                bookmarkBarBean.setChildren(recursiveRemoveListBookmark(parentId, childrenBeanList, myBookMarkNoSelectListBeans));
            }
            jsonValueBean.getRoots().setBookmark_bar(bookmarkBarBean);
            String toJson = gson.toJson(jsonValueBean);
            //保存新的数据
            setBookmark(toJson);
            //添加书签
            return toJson;
        }
        return "移除失败";
    }


    /**
     * 递归查找匹配的文件夹  并删除数据
     *
     * @param parentId                   父类id
     * @param removeId                   要移除的对象的id
     * @param childrenBeanList           要查询的集合
     * @param myBookMarkNoSelectListBean 删除的数据集
     * @return
     */
    private List<ChildrenBean> recursiveRemoveBookmark(String parentId, int removeId, List<ChildrenBean> childrenBeanList, MyBookMarkNoSelectListBean myBookMarkNoSelectListBean) {
        List<ChildrenBean> newChildrenBeanList = new ArrayList<>(); //创建新的集合用来保存数据
        for (int i = 0; i < childrenBeanList.size(); i++) {
            //先判断是否是文件夹,不是的话忽略
            ChildrenBean newChildrenBean = childrenBeanList.get(i);
            if (parentId.equals(newChildrenBean.getDate_added())) {
                List<ChildrenBean> children = newChildrenBean.getChildren();
                children.remove(removeId);
                newChildrenBean.setChildren(children);
            } else {
                //不是匹配子类中的文件夹
                List<ChildrenBean> children = newChildrenBean.getChildren();
                //说明子类文件夹下面还有文件夹 递归进入
                if (children != null && children.size() > 0) {
                    List<ChildrenBean> newChildrenChildrenBeans = recursiveRemoveBookmark(parentId, removeId, children, myBookMarkNoSelectListBean);
                    newChildrenBean.setChildren(newChildrenChildrenBeans);
                }
            }
            newChildrenBeanList.add(newChildrenBean);
        }
        return newChildrenBeanList;
    }

    /**
     * 递归查找匹配的文件夹  并删除集合数据
     *
     * @param parentId                    父类id
     * @param childrenBeanList            要查询的集合
     * @param myBookMarkNoSelectListBeans 删除的数据集合
     * @return
     */
    private List<ChildrenBean> recursiveRemoveListBookmark(String parentId, List<ChildrenBean> childrenBeanList, List<MyBookMarkNoSelectListBean> myBookMarkNoSelectListBeans) {
        List<ChildrenBean> newChildrenBeanList = new ArrayList<>(); //创建新的集合用来保存数据
        for (int i = 0; i < childrenBeanList.size(); i++) {
            //先判断是否是文件夹,不是的话忽略
            ChildrenBean newChildrenBean = childrenBeanList.get(i);
            if (parentId.equals(newChildrenBean.getDate_added())) {
                for (int j = 0; j < myBookMarkNoSelectListBeans.size(); j++) {
                    MyBookMarkNoSelectListBean myBookMarkNoSelectListBean = myBookMarkNoSelectListBeans.get(j);
                    if (myBookMarkNoSelectListBean.isSelected()) {
                        List<ChildrenBean> children = newChildrenBean.getChildren();
                        for (int k = 0; k < children.size(); k++) {
                            if (myBookMarkNoSelectListBean.getDate_added().equals(children.get(k).getDate_added())) {
                                children.remove(k);
                                newChildrenBean.setChildren(children);
                            }
                        }
                    }
                }
            } else {
                //不是匹配子类中的文件夹
                List<ChildrenBean> children = newChildrenBean.getChildren();
                //说明子类文件夹下面还有文件夹 递归进入
                if (children != null && children.size() > 0) {
                    List<ChildrenBean> newChildrenChildrenBeans = recursiveRemoveListBookmark(parentId, children, myBookMarkNoSelectListBeans);
                    newChildrenBean.setChildren(newChildrenChildrenBeans);
                }
            }
            newChildrenBeanList.add(newChildrenBean);
        }
        return newChildrenBeanList;
    }
}
