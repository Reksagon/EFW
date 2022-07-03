package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.MainActivity;
import com.efw.apps.R;
import com.efw.apps.databinding.ExerciseDayItemBinding;
import com.efw.apps.databinding.FragmentExercisesBinding;
import com.efw.apps.ui.account.Account;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHoler>{
    ArrayList<Day> data;
    Activity activity;
    FragmentExercisesBinding fragmentExercisesBinding;
    public static TextToSpeech textToSpeech;
    ArrayList<Exercise> data_exercise;

    public DayAdapter(Activity activity, ArrayList<Day> data, FragmentExercisesBinding fragmentExercisesBinding,
                      ArrayList<Exercise> data_exercise){
        this.data = data;
        this.activity = activity;
        this.fragmentExercisesBinding = fragmentExercisesBinding;
        this.data_exercise = data_exercise;
    }

    @Override
    public DayAdapter.DayViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ExerciseDayItemBinding binding = ExerciseDayItemBinding.inflate(inflater, parent, false);
        return new DayAdapter.DayViewHoler(binding);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(DayAdapter.DayViewHoler holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(data.get(position), position);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class DayViewHoler extends RecyclerView.ViewHolder{

        private ExerciseDayItemBinding binding;

        public DayViewHoler(ExerciseDayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        public void bind(Day day, int position)
        {

            if(day.getNum_day() == Account.accountAPP.current_training_day)
            {

                binding.contentDay.setVisibility(View.GONE);
                binding.currentDay.setVisibility(View.VISIBLE);
                binding.day2.setText(activity.getString(R.string.day) + " " + String.valueOf(day.getNum_day()));
                if(day.isSuccess())
                {
                    binding.success2.setText(activity.getString(R.string.success) + " 100%");
                    binding.bttnStart.setVisibility(View.GONE);
                }else {
                    if(!day.isRest())
                        binding.success2.setText(activity.getString(R.string.success) + " 0%");
                    else
                        binding.success2.setText(activity.getResources().getString(R.string.rest));

                    binding.bttnStart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            fragmentExercisesBinding.dayList.setVisibility(View.GONE);
                            fragmentExercisesBinding.startLogo.setVisibility(View.GONE);
                            fragmentExercisesBinding.exercicesList.setVisibility(View.VISIBLE);
                            //fragmentExercisesBinding.exerciceLogo.setVisibility(View.VISIBLE);
                            activity.findViewById(R.id.nav_view).setVisibility(View.GONE);
                            textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int i) {
                                    MainActivity.timer = true;
                                    Locale language = new Locale(Account.accountFirebase.getLanguage());
                                    textToSpeech.setLanguage(language);
                                    String utteranceId = UUID.randomUUID().toString();
                                    ExerciesAdapter exerciesAdapter = (ExerciesAdapter) fragmentExercisesBinding.exercicesList.getAdapter();
                                    textToSpeech.speak(data_exercise.get(exerciesAdapter.current).getSpeak_text(), TextToSpeech.QUEUE_ADD, null, utteranceId);
                                    if(exerciesAdapter.current == 1)
                                        Account.showDialog(activity, activity.getString(R.string.stund));
                                    else
                                        Account.showDialog(activity, activity.getString(R.string.sid));
                                }
                            });
                        }
                    });
                }
                return;
            }

           binding.day.setText(activity.getResources().getString(R.string.day) + " " + String.valueOf(day.getNum_day()));
           if(day.isSuccess())
           {
               binding.success.setText(activity.getResources().getString(R.string.success));
           }
           else
           {
               binding.success.setText(activity.getResources().getString(R.string.success_no));
               binding.imgEllipse.setVisibility(View.GONE);
               binding.imgCenter.setVisibility(View.GONE);
           }

           if(day.isRest() && !day.isSuccess())
           {
               binding.imgEllipse.setVisibility(View.VISIBLE);
               binding.imgCenter.setVisibility(View.VISIBLE);
               binding.imgCenter.setImageDrawable(activity.getDrawable(R.drawable.ic_rest));
               binding.success.setText(activity.getResources().getString(R.string.rest));
           }


        }
    }
}
