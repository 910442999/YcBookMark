package com.yc.bookmarklibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 书签父类
 */
public class BookmarkBarBean implements Serializable {

    /**
     * children : [{"date_added":"13187773997209683","id":"30","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"},{"children":[],"date_added":"13187774035529070","date_modified":"13187774035529075","id":"34","name":"gg","type":"folder"}]
     * date_added : 13187773795709860
     * date_modified : 13187773997209683
     * id : 1
     * name : 书签栏
     * type : folder
     */

    private String date_added;
    private String date_modified;
    private String id;
    private String name;
    private String type;
    private List<ChildrenBean> children;

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "BookmarkBarBean{" +
                "date_added='" + date_added + '\'' +
                ", date_modified='" + date_modified + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", children=" + children +
                '}';
    }
}
