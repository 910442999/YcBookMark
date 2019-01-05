package com.yc.bookmarklibrary.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 书签子类
 */
public class ChildrenBean implements Serializable {
    /**
     * date_added : 13187773997209683
     * id : 30
     * name : 快递查询
     * type : url
     * url : http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android
     * children : [{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}]
     * date_modified : 13187774068993232
     */

    private String date_added;
    private String id;
    private String name;
    private String type;
    private String url;
    private String date_modified;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ChildrenBean> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBean> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "ChildrenBean{" +
                "date_added='" + date_added + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", children=" + children +
                '}';
    }
}
