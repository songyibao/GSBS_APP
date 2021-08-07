package com.songyb.bs.classes;

public class Func {
    private int id;
    private int comments_count;
    private String name;
    private String icon;
    private String tag;
    private String available;

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getTag() {
        return tag;
    }

    public String getAvailable() {
        return available;
    }

    public int getId() {
        return id;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setAvailable(String available) {
        this.available = available;
    }
}
