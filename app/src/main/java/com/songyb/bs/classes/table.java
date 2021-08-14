package com.songyb.bs.classes;

public class table {
    private int date;
    private int length;
    private String name;
    private int order;
    private String week;
    private boolean is_now_week;
    public void setDate(int date) {
        this.date = date;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public void setIs_now_week(boolean is_now_week) {
        this.is_now_week = is_now_week;
    }

    public int getDate() {
        return date;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public String getWeek() {
        return week;
    }

    public boolean isIs_now_week() {
        return is_now_week;
    }
}
