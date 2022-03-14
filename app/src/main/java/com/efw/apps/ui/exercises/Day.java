package com.efw.apps.ui.exercises;

import java.io.Serializable;

public class Day implements Serializable {
    private int num_day;
    private boolean rest;
    private boolean success;
    private Date date_success = null;
    private int day, month, year;

    public Date getDate_success() {
        return date_success;
    }

    public void setDate_success(Date date_success) {
        this.date_success = date_success;
    }

    public Day(int num_day, boolean rest, boolean success) {
        this.num_day = num_day;
        this.rest = rest;
        this.success = success;
    }

    public Day(int num_day, int year, int month, int day,  boolean rest, boolean success) {
        this.num_day = num_day;
        this.rest = rest;
        this.success = success;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getNum_day() {
        return num_day;
    }

    public void setNum_day(int num_day) {
        this.num_day = num_day;
    }

    public boolean isRest() {
        return rest;
    }

    public void setRest(boolean rest) {
        this.rest = rest;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
