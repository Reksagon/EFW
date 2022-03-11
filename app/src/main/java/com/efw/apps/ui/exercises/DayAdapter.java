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

import com.efw.apps.R;
import com.efw.apps.databinding.ExerciseDayItemBinding;
import com.efw.apps.databinding.FragmentExercisesBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHoler>{
    ArrayList<Day> data;
    Activity activity;
    FragmentExercisesBinding fragmentExercisesBinding;
    TextToSpeech textToSpeech;
    String speak_text;

    public DayAdapter(Activity activity, ArrayList<Day> data, FragmentExercisesBinding fragmentExercisesBinding, String speak_text){
        this.data = data;
        this.activity = activity;
        this.fragmentExercisesBinding = fragmentExercisesBinding;
        this.speak_text = speak_text;
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
        holder.bind(data.get(position));
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

        @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
        public void bind(Day day)
        {
            if(day.getNum_day() == 1)
            {
                binding.contentDay.setVisibility(View.GONE);
                binding.currentDay.setVisibility(View.VISIBLE);
                binding.day2.setText(activity.getString(R.string.day) + " " + String.valueOf(day.getNum_day()));
                binding.success2.setText(activity.getString(R.string.success) + " 0%");
                binding.bttnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fragmentExercisesBinding.dayList.setVisibility(View.GONE);
                        fragmentExercisesBinding.startLogo.setVisibility(View.GONE);
                        fragmentExercisesBinding.exercicesList.setVisibility(View.VISIBLE);
                        fragmentExercisesBinding.exerciceLogo.setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.nav_view).setVisibility(View.GONE);
                        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int i) {
                                Locale language = new Locale("ru");
                                textToSpeech.setLanguage(language);
                                String utteranceId = UUID.randomUUID().toString();
                                textToSpeech.speak(speak_text, TextToSpeech.QUEUE_FLUSH, null,utteranceId);
                            }
                        });
                    }
                });
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

           if(day.isRest())
           {
               binding.imgEllipse.setVisibility(View.VISIBLE);
               binding.imgCenter.setVisibility(View.VISIBLE);
               binding.imgCenter.setImageDrawable(activity.getDrawable(R.drawable.ic_rest));
               binding.success.setText(activity.getResources().getString(R.string.rest));
           }


        }
    }
}
