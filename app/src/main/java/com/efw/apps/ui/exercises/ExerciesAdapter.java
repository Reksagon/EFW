package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.MainActivity;
import com.efw.apps.R;
import com.efw.apps.databinding.ExerciseDayItemBinding;
import com.efw.apps.databinding.FragmentExercisesBinding;
import com.efw.apps.databinding.LayoutExersiceItemBinding;
import com.efw.apps.ui.account.Account;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExerciesAdapter extends RecyclerView.Adapter<ExerciesAdapter.LinearViewHolder>{
    ArrayList<Exercise> data;
    private int current = 0;
    public RecyclerView recyclerView;
    private TextToSpeech textToSpeech;
    FragmentExercisesBinding fragmentExercisesBinding;
    AsyncTask<Void, Integer, Void> timer, rest;
    public int exercice_time = 1, rest_time = 1;

    Activity activity;

    public ExerciesAdapter(ArrayList<Exercise> data, RecyclerView recyclerView, Activity context, FragmentExercisesBinding binding){
        this.data = data;
        this.recyclerView = recyclerView;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Locale language = new Locale("ru");
                textToSpeech.setLanguage(language);
            }
        });
        activity = context;
        this.fragmentExercisesBinding = binding;
    }

    @Override
    public ExerciesAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutExersiceItemBinding binding = LayoutExersiceItemBinding.inflate(inflater, parent, false);
        return new ExerciesAdapter.LinearViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ExerciesAdapter.LinearViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private Button next;
        LayoutExersiceItemBinding binding;

        public LinearViewHolder(LayoutExersiceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            textView =(TextView) itemView.findViewById(R.id.name_exersice);
            next = itemView.findViewById(R.id.next_exercice_bttn);
        }

        public void bind(int pos)
        {
            binding.nameExersice.setText(data.get(pos).getName());
            if(pos == data.size()-1)
                binding.nextExerciceBttn.setText("End");

            binding.skipBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rest.cancel(true);
                    rest = null;
                    timer = null;
                    binding.timerConstraint.setVisibility(View.GONE);
                    binding.skipBttn.setVisibility(View.GONE);
                    binding.nextExerciceBttn.setVisibility(View.VISIBLE);
                }
            });

            binding.startExerciceBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.startExerciceBttn.setVisibility(View.GONE);
                    MainActivity.timer = true;
                    timer = new AsyncTask<Void, Integer, Void>() {
                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            //binding.nextExerciceBttn.setVisibility(View.VISIBLE);
                            binding.nameExersice.setText(activity.getString(R.string.rest));
                            rest = new AsyncTask<Void, Integer, Void>() {
                                @Override
                                protected void onPostExecute(Void unused) {
                                    super.onPostExecute(unused);
                                    rest = null;
                                    timer = null;
                                    binding.timerConstraint.setVisibility(View.GONE);
                                    binding.skipBttn.setVisibility(View.GONE);
                                    binding.nextExerciceBttn.setVisibility(View.VISIBLE);
                                }

                                @Override
                                protected void onProgressUpdate(Integer... values) {
                                    if (values[0] == 25)
                                        binding.skipBttn.setVisibility(View.VISIBLE);

                                    binding.timeTxt.setText(String.valueOf(values[0]));
                                    binding.circularProgressView.animateProgressChange(values[0], 950);
                                }

                                @Override
                                protected Void doInBackground(Void... voids) {
                                    for(int i = rest_time; i >= 0; i--)
                                    {
                                        if(!isCancelled()) {
                                            publishProgress(i);
                                            try {
                                                TimeUnit.SECONDS.sleep(1);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        else
                                        {
                                            break;
                                        }
                                    }
                                    return null;
                                }
                            }.execute();
                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            if(values[0] == 60)
                            {
                                binding.timeTxt.setText("1:00");
                                binding.circularProgressView.setProgress(values[0]);
                                binding.circularProgressView.animateProgressChange(values[0], 100);
                            }
                            else
                            {
                                binding.timeTxt.setText(String.valueOf(values[0]));
                                binding.circularProgressView.animateProgressChange(values[0], 950);
                            }
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            for(int i = exercice_time; i >= 0; i--)
                            {
                                publishProgress(i);
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            return null;
                        }
                    }.execute();
                }
            });

            binding.nextExerciceBttn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    if(pos != data.size()-1) {
                        current++;
                        recyclerView.smoothScrollToPosition(current);
                        String utteranceId = UUID.randomUUID().toString();
                        textToSpeech.speak(data.get(pos+1).getName(), TextToSpeech.QUEUE_FLUSH, null,utteranceId);
                    }
                    else
                    {

                        fragmentExercisesBinding.exercicesList.setVisibility(View.GONE);
                        fragmentExercisesBinding.exerciceLogo.setVisibility(View.GONE);
                        fragmentExercisesBinding.illustration.setVisibility(View.VISIBLE);
                        fragmentExercisesBinding.okayBttn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                MainActivity.timer = false;
                                activity.findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
                                recyclerView.smoothScrollToPosition(0);
                                current = 0;
                                fragmentExercisesBinding.dayList.setVisibility(View.VISIBLE);
                                fragmentExercisesBinding.startLogo.setVisibility(View.VISIBLE);
                                fragmentExercisesBinding.illustration.setVisibility(View.GONE);
                            }
                        });

                        Calendar calendar = Calendar.getInstance();
                        Account.accountAPP.array_days_training.get(Account.accountAPP.current_training_day-1).setSuccess(true);
                        Account.accountAPP.array_days_training.get(Account.accountAPP.current_training_day-1).setDate_success(
                                new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                        );
                        Account.accountFirebase.setCount_training(Account.accountFirebase.getCount_training()+1);
                        Account.accountFirebase.setCurrent_training_day(Account.accountAPP.current_training_day+1);
                        Account.accountFirebase.setTime_training(Account.accountFirebase.getTime_training() + 10);
                        Account.saveAccount();
                        fragmentExercisesBinding.dayList.getAdapter().notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
