package com.yc.bookmarklibrary.bean;

/**
 * 获取书签bean类  已抽离出 BookmarkBarBean 和 ChildrenBean 类
 */
public class MyBookMarkBaseBean {

    /**
     * state : 1
     * code : 0
     * message :
     * data : {"json_value":{"checksum":"08b2136e9c21973aa489c5a7c2c62c90","roots":{"bookmark_bar":{"children":[{"date_added":"13187773997209683","id":"30","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"},{"children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_added":"13187774035529070","date_modified":"13187774068993232","id":"34","name":"gg","type":"folder"}],"date_added":"13187773795709860","date_modified":"13187773997209683","id":"1","name":"书签栏","type":"folder"},"other":{"children":[],"date_added":"13187773795709885","date_modified":"0","id":"2","name":"其他书签","type":"folder"},"synced":{"children":[],"date_added":"13187774032289758","date_modified":"0","id":"36","name":"移动设备书签","type":"folder"}},"version":1}}
     */

    private int state;
    private int code;
    private String message;
    private DataBean data;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MyBookMarkBaseBean{" +
                "state=" + state +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public static class DataBean {
        /**
         * json_value : {"checksum":"08b2136e9c21973aa489c5a7c2c62c90","roots":{"bookmark_bar":{"children":[{"date_added":"13187773997209683","id":"30","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android","children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_modified":"13187774068993232"},{"children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_added":"13187774035529070","date_modified":"13187774068993232","id":"34","name":"gg","type":"folder"}],"date_added":"13187773795709860","date_modified":"13187773997209683","id":"1","name":"书签栏","type":"folder"},"other":{"children":[],"date_added":"13187773795709885","date_modified":"0","id":"2","name":"其他书签","type":"folder"},"synced":{"children":[],"date_added":"13187774032289758","date_modified":"0","id":"36","name":"移动设备书签","type":"folder"}},"version":1}
         */

        private JsonValueBean json_value;

        public JsonValueBean getJson_value() {
            return json_value;
        }

        public void setJson_value(JsonValueBean json_value) {
            this.json_value = json_value;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "json_value=" + json_value +
                    '}';
        }

    }
}
