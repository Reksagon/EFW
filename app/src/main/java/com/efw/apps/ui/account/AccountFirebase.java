package com.efw.apps.ui.account;

import com.efw.apps.ui.exercises.Date;
import com.efw.apps.ui.exercises.Day;

import java.io.Serializable;

public class AccountFirebase implements Serializable {
    int current_training_day;
    Date last_date_online;
    int count_training;
    int time_training;
    Date start_training_day;
    Date last_training_day;
    boolean premium;
    boolean night_mode;
    String language;
    String array_days_training;

    public int getCurrent_training_day() {
        return current_training_day;
    }

    public void setCurrent_training_day(int current_training_day) {
        this.current_training_day = current_training_day;
    }

    public Date getLast_date_online() {
        return last_date_online;
    }

    public void setLast_date_online(Date last_date_online) {
        this.last_date_online = last_date_online;
    }

    public int getCount_training() {
        return count_training;
    }

    public void setCount_training(int count_training) {
        this.count_training = count_training;
    }

    public int getTime_training() {
        return time_training;
    }

    public void setTime_training(int time_training) {
        this.time_training = time_training;
    }

    public Date getStart_training_day() {
        return start_training_day;
    }

    public void setStart_training_day(Date start_training_day) {
        this.start_training_day = start_training_day;
    }

    public Date getLast_training_day() {
        return last_training_day;
    }

    public void setLast_training_day(Date last_training_day) {
        this.last_training_day = last_training_day;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isNight_mode() {
        return night_mode;
    }

    public void setNight_mode(boolean night_mode) {
        this.night_mode = night_mode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getArray_days_training() {
        return array_days_training;
    }

    public void setArray_days_training(String array_days_training) {
        this.array_days_training = array_days_training;
    }

    public AccountFirebase()
    {

    };

    public AccountFirebase(String array_days_training, int current_training_day, Date last_date_online, int count_training, int time_training, Date start_training_day, Date last_training_day, boolean premium, boolean night_mode, String language) {
        this.current_training_day = current_training_day;
        this.last_date_online = last_date_online;
        this.count_training = count_training;
        this.time_training = time_training;
        this.start_training_day = start_training_day;
        this.last_training_day = last_training_day;
        this.premium = premium;
        this.night_mode = night_mode;
        this.language = language;
        this.array_days_training = array_days_training;
    }
}
