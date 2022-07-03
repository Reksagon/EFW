package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.FragmentExercisesBinding;
import com.efw.apps.ui.account.Account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ExercisesFragment extends Fragment {

    private static FragmentExercisesBinding binding;
    private ExerciesAdapter adapter;
    private DayAdapter dayAdapter;
    ArrayList<Exercise> data = new ArrayList<Exercise>();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ExercisesViewModel dashboardViewModel =
                new ViewModelProvider(this).get(ExercisesViewModel.class);

        binding = FragmentExercisesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.progressView1.setProgress(15);
        ArrayList<Exercise> data = new ArrayList<Exercise>();
        data.add(new Exercise(getActivity().getString(R.string.ex_1),getActivity().getString(R.string.speak_text_ex1) ));
        data.add(new Exercise(getActivity().getString(R.string.ex_2), getActivity().getString(R.string.speak_text_ex2)));
        data.add(new Exercise(getActivity().getString(R.string.ex_3), getActivity().getString(R.string.speak_text_ex3)));
        data.add(new Exercise(getActivity().getString(R.string.ex_4), getActivity().getString(R.string.speak_text_ex4)));
        data.add(new Exercise(getActivity().getString(R.string.ex_5), getActivity().getString(R.string.speak_text_ex5)));
        data.add(new Exercise(getActivity().getString(R.string.ex_6), getActivity().getString(R.string.speak_text_ex6)));
        data.add(new Exercise(getActivity().getString(R.string.ex_7), getActivity().getString(R.string.speak_text_ex7)));
        data.add(new Exercise(getActivity().getString(R.string.ex_8), getActivity().getString(R.string.speak_text_ex8)));
        data.add(new Exercise(getActivity().getString(R.string.ex_9), getActivity().getString(R.string.speak_text_ex9)));
        data.add(new Exercise(getActivity().getString(R.string.ex_10), getActivity().getString(R.string.speak_text_ex10)));


        if(!Account.flag) {
            Calendar calendar = Calendar.getInstance();
            try {
                Day tmp = Account.accountAPP.array_days_training.get(Account.accountFirebase.getCurrent_training_day());
                Day current_tmp = Account.accountAPP.array_days_training.get(Account.accountFirebase.getCurrent_training_day() - 1);

                if (current_tmp.isRest() && !tmp.isSuccess()) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    if (current_tmp.getYear() == calendar.get(Calendar.YEAR) &&
                            current_tmp.getMonth() == calendar.get(Calendar.MONTH) &&
                            current_tmp.getDay() == calendar.get(Calendar.DAY_OF_MONTH)) {
                        Account.accountAPP.current_training_day += 1;
                    }
                } else if (tmp.isSuccess()) {
                    if (tmp.getYear() == calendar.get(Calendar.YEAR) &&
                            tmp.getMonth() == calendar.get(Calendar.MONTH) &&
                            tmp.getDay() == calendar.get(Calendar.DAY_OF_MONTH)) {
                        int i = Account.accountFirebase.getCurrent_training_day() - 1;
                        for (; i >= 0; i--) {
                            if (Account.accountAPP.array_days_training.get(i).isSuccess()) ;
                            break;
                        }
                        Account.accountAPP.current_training_day = i;
                    }
                }
            } catch (Exception ex) {

            }
            Account.flag = true;
        }

        adapter = new ExerciesAdapter(data, binding.exercicesList, getActivity(), binding);
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.HORIZONTAL);
        binding.exercicesList.setLayoutManager(linearLayout);
        binding.exercicesList.setAdapter(adapter);

        dayAdapter = new DayAdapter(getActivity(), Account.accountAPP.array_days_training, binding, data);
        dayAdapter.setHasStableIds(false);
        LinearLayoutManager linearLayout_day = new LinearLayoutManager(getActivity());
        linearLayout_day.setOrientation(RecyclerView.VERTICAL);
        binding.dayList.setLayoutManager(linearLayout_day);
        binding.dayList.setAdapter(dayAdapter);
        binding.dayList.smoothScrollToPosition(Account.accountAPP.current_training_day+5);

        binding.exercicesList.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        float day = 100 - Account.accountAPP.current_training_day;
        binding.progressView1.setProgress(Account.accountAPP.current_training_day);
        binding.textView2.setText(getActivity().getString(R.string.yet) + " " + (int)day + " " + getActivity().getString(R.string.yet_day));

        //binding.dayExerciceStart.setText(getActivity().getText(R.string.day) + " " + Account.accountAPP.current_training_day);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true ) {
            @Override
            @MainThread
            public void handleOnBackPressed() {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        return root;
    }

    public static void startMenu()
    {
        binding.dayList.setVisibility(View.VISIBLE);
        binding.startLogo.setVisibility(View.VISIBLE);
        //binding.exerciceLogo.setVisibility(View.GONE);
        binding.exercicesList.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}