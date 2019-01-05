package com.yc.bookmarklibrary.bean;

import java.util.List;

public class JsonValueBean {
    /**
     * checksum : 08b2136e9c21973aa489c5a7c2c62c90
     * roots : {"bookmark_bar":{"children":[{"date_added":"13187773997209683","id":"30","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"},{"children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_added":"13187774035529070","date_modified":"13187774068993232","id":"34","name":"gg","type":"folder"}],"date_added":"13187773795709860","date_modified":"13187773997209683","id":"1","name":"书签栏","type":"folder"},"other":{"children":[],"date_added":"13187773795709885","date_modified":"0","id":"2","name":"其他书签","type":"folder"},"synced":{"children":[],"date_added":"13187774032289758","date_modified":"0","id":"36","name":"移动设备书签","type":"folder"}}
     * version : 1
     */

    private String checksum;
    private RootsBean roots;
    private int version;

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public RootsBean getRoots() {
        return roots;
    }

    public void setRoots(RootsBean roots) {
        this.roots = roots;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "JsonValueBean{" +
                "checksum='" + checksum + '\'' +
                ", roots=" + roots +
                ", version=" + version +
                '}';
    }

    public static class RootsBean {
        /**
         * bookmark_bar : {"children":[{"date_added":"13187773997209683","id":"30","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android","children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_modified":"13187774068993232"},{"children":[{"date_added":"13187774068993232","id":"35","name":"快递查询","type":"url","url":"http://m.tool.114la.com/kuaidichaxun.html?token=3ea2pf58SWw5m+odhPMKO0pimN2Yef6Hh7whAhX7P/H1i9XBUekJHsBxqEY6q43xoQ&authkey=c2a68d0c67c1aa08e7a187252fa435ced11e4bf3&client=android"}],"date_added":"13187774035529070","date_modified":"13187774068993232","id":"34","name":"gg","type":"folder"}],"date_added":"13187773795709860","date_modified":"13187773997209683","id":"1","name":"书签栏","type":"folder"}
         * other : {"children":[],"date_added":"13187773795709885","date_modified":"0","id":"2","name":"其他书签","type":"folder"}
         * synced : {"children":[],"date_added":"13187774032289758","date_modified":"0","id":"36","name":"移动设备书签","type":"folder"}
         */

        private BookmarkBarBean bookmark_bar;
        private OtherBean other;
        private SyncedBean synced;

        public BookmarkBarBean getBookmark_bar() {
            return bookmark_bar;
        }

        public void setBookmark_bar(BookmarkBarBean bookmark_bar) {
            this.bookmark_bar = bookmark_bar;
        }

        public OtherBean getOther() {
            return other;
        }

        public void setOther(OtherBean other) {
            this.other = other;
        }

        public SyncedBean getSynced() {
            return synced;
        }

        public void setSynced(SyncedBean synced) {
            this.synced = synced;
        }

        @Override
        public String toString() {
            return "RootsBean{" +
                    "bookmark_bar=" + bookmark_bar +
                    ", other=" + other +
                    ", synced=" + synced +
                    '}';
        }

        public static class OtherBean {
            /**
             * children : []
             * date_added : 13187773795709885
             * date_modified : 0
             * id : 2
             * name : 其他书签
             * type : folder
             */

            private String date_added;
            private String date_modified;
            private String id;
            private String name;
            private String type;
            private List<?> children;

            public String getDate_added() {
                return date_added;
            }

            public void setDate_added(String date_added) {
                this.date_added = date_added;
            }

            public String getDate_modified() {
                return date_modified;
            }

            public void setDate_modified(String date_modified) {
                this.date_modified = date_modified;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public List<?> getChildren() {
                return children;
            }

            public void setChildren(List<?> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "OtherBean{" +
                        "date_added='" + date_added + '\'' +
                        ", date_modified='" + date_modified + '\'' +
                        ", id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", type='" + type + '\'' +
                        ", children=" + children +
                        '}';
            }
        }

        public static class SyncedBean {
            /**
             * children : []
             * date_added : 13187774032289758
             * date_modified : 0
             * id : 36
             * name : 移动设备书签
             * type : folder
             */

            private String date_added;
            private String date_modified;
            private String id;
            private String name;
            private String type;
            private List<?> children;

            public String getDate_added() {
                return date_added;
            }

            public void setDate_added(String date_added) {
                this.date_added = date_added;
            }

            public String getDate_modified() {
                return date_modified;
            }

            public void setDate_modified(String date_modified) {
                this.date_modified = date_modified;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
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

            public List<?> getChildren() {
                return children;
            }

            public void setChildren(List<?> children) {
                this.children = children;
            }

            @Override
            public String toString() {
                return "SyncedBean{" +
                        "date_added='" + date_added + '\'' +
                        ", date_modified='" + date_modified + '\'' +
                        ", id='" + id + '\'' +
                        ", name='" + name + '\'' +
                        ", type='" + type + '\'' +
                        ", children=" + children +
                        '}';
            }
        }
    }
}