package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.databinding.FragmentExercisesBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExercisesFragment extends Fragment {

    private FragmentExercisesBinding binding;
    private ExerciesAdapter adapter;
    private DayAdapter dayAdapter;
    TextToSpeech textToSpeech = null;

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExercisesViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.progressView1.setProgress(15);
        ArrayList<Exercise> data = new ArrayList<Exercise>();
        data.add(new Exercise("Поднятие нижней губы"));
        data.add(new Exercise("Подъем подбородка"));
        data.add(new Exercise("Поцелуй жирафа"));
        data.add(new Exercise("Опускание губ"));
        data.add(new Exercise("Подтягивание щёк"));
        data.add(new Exercise("Письмо в воздухе"));
        data.add(new Exercise("Подъем головы"));
        data.add(new Exercise("Заполненный воздухом"));
        data.add(new Exercise("Выпячивание подбородка"));
        data.add(new Exercise("Ещё раз давим на подбородок"));

        binding.startBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startBttn.setVisibility(View.GONE);
                binding.exercicesList.setVisibility(View.VISIBLE);
                textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        Locale language = new Locale("ru");
                        textToSpeech.setLanguage(language);
                        String utteranceId = UUID.randomUUID().toString();
                        textToSpeech.speak(data.get(0).getName(), TextToSpeech.QUEUE_FLUSH, null,utteranceId);
                    }
                });
            }
        });

        adapter = new ExerciesAdapter(data, binding.exercicesList, getActivity(), binding);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.HORIZONTAL);
        binding.exercicesList.setLayoutManager(linearLayout);
        binding.exercicesList.setAdapter(adapter);

        dayAdapter = new DayAdapter(getActivity(), getDays());
        dayAdapter.setHasStableIds(false);
        LinearLayoutManager linearLayout_day = new LinearLayoutManager(getActivity());
        linearLayout_day.setOrientation(RecyclerView.VERTICAL);
        binding.dayList.setLayoutManager(linearLayout_day);
        binding.dayList.setAdapter(dayAdapter);


        binding.exercicesList.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        return root;
    }

    private ArrayList<Day> getDays()
    {
        ArrayList<Day> days = new ArrayList<>();
        for(int i = 1; i <= 100; i++)
        {
            if(i%2 == 0)
                days.add(new Day(i, false, false));
            else
                days.add(new Day(i, true, false));
        }

        return days;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}