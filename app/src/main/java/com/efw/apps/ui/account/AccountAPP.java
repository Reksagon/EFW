package com.efw.apps.ui.account;

import com.efw.apps.ui.exercises.Date;
import com.efw.apps.ui.exercises.Day;

import java.util.ArrayList;

public class AccountAPP {
    public int current_training_day;
    public Date last_date_online;
    public int count_training;
    public int time_training;
    public Date start_training_day;
    public Date last_training_day;
    public boolean premium;
    public boolean night_mode;
    public String language;
    public ArrayList<Day> array_days_training;

    public AccountAPP(int current_training_day, Date last_date_online, int count_training, int time_training, Date start_training_day, Date last_training_day, boolean premium, boolean night_mode, String language, ArrayList<Day> array_days_training) {
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
