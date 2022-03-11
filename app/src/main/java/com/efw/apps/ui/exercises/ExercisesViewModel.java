package com.efw.apps.ui.exercises;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ExercisesViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Exercise>> data;


    public ExercisesViewModel() {

        ArrayList<Exercise> data_tmp = new ArrayList<Exercise>();
        data_tmp.add(new Exercise("Поднятие нижней губы"));
        data_tmp.add(new Exercise("Подъем подбородка"));
        data_tmp.add(new Exercise("Поцелуй жирафа"));
        data_tmp.add(new Exercise("Опускание губ"));
        data_tmp.add(new Exercise("Подтягивание щёк"));
        data_tmp.add(new Exercise("Письмо в воздухе"));
        data_tmp.add(new Exercise("Подъем головы"));
        data_tmp.add(new Exercise("Заполненный воздухом"));
        data_tmp.add(new Exercise("Выпячивание подбородка"));
        data_tmp.add(new Exercise("Ещё раз давим на подбородок"));
        data = new MutableLiveData<>();
        data.setValue(data_tmp);
    }

    public LiveData<ArrayList<Exercise>> getData() {
        return data;
    }
}