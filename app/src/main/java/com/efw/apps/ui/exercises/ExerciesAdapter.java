package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.efw.apps.MainActivity;
import com.efw.apps.R;
import com.efw.apps.databinding.FragmentExercisesBinding;
import com.efw.apps.databinding.LayoutExersiceItemBinding;
import com.efw.apps.ui.account.Account;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ExerciesAdapter extends RecyclerView.Adapter<ExerciesAdapter.LinearViewHolder>{
    ArrayList<Exercise> data;
    public int current = 0;
    public RecyclerView recyclerView;
    private TextToSpeech textToSpeech;
    FragmentExercisesBinding fragmentExercisesBinding;
    AsyncTask<Void, Integer, Void> timer, rest;
    public int exercice_time = 60, rest_time = 30;
    public int tmp_exercise = exercice_time;

    Activity activity;
    private InterstitialAd mInterstitialAd;

    public ExerciesAdapter(ArrayList<Exercise> data, RecyclerView recyclerView, Activity context, FragmentExercisesBinding binding){
        this.data = data;
        this.recyclerView = recyclerView;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Locale language = new Locale(Account.accountFirebase.getLanguage());
                textToSpeech.setLanguage(language);
            }
        });
        activity = context;
        this.fragmentExercisesBinding = binding;

        if(!Account.accountFirebase.isPremium())
            loadAd();

    }

    void loadAd()
    {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity,"ca-app-pub-2180603710226725/1607543211", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                        loadAd();
                    }


                });
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
            if(fragmentExercisesBinding.exercicesList.getVisibility() == View.VISIBLE) {
                if (pos == 1)
                    Account.showDialog(activity, activity.getString(R.string.stund));
                else
                    Account.showDialog(activity, activity.getString(R.string.sid));
            }
            if(pos == 0 && !Account.accountFirebase.isPremium())
            {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(activity);
                    loadAd();
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }
            }
            binding.nameExersice.setText(data.get(pos).getName());
            if(pos == data.size()-1) {
                binding.nextExerciceBttn.setText("End");
                binding.previousExerciceBttn.setVisibility(View.GONE);
            }

            setAnim(pos);

            binding.skipBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rest.cancel(true);
                    rest = null;
                    timer = null;
                    binding.timerConstraint.setVisibility(View.GONE);
                    binding.skipBttn.setVisibility(View.GONE);
                    binding.nextLiner.setVisibility(View.VISIBLE);

                }
            });


            binding.startExerciceBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.startExerciceBttn.setVisibility(View.GONE);
                    MainActivity.timer = true;
                    timer = async().execute();
                    binding.bttnPause.setVisibility(View.VISIBLE);
                    binding.nextLiner.setVisibility(View.VISIBLE);
                }
            });

            binding.previousExerciceBttn.setOnClickListener(v->
            {
                if(pos != 0) {
                    current--;
                    recyclerView.smoothScrollToPosition(current);
                    String utteranceId = UUID.randomUUID().toString();
                    textToSpeech.stop();
                    textToSpeech.speak(data.get(pos-1).getSpeak_text(), TextToSpeech.QUEUE_ADD, null,utteranceId);
                    if(timer != null)
                    {
                        timer.cancel(true);
                        timer = null;
                    }
                }
            });

            binding.bttnPause.setOnClickListener(v->
            {
                if(timer != null)
                {
                    timer.cancel(true);
                    timer = null;
                    binding.bttnPause.setImageDrawable(activity.getDrawable(R.drawable.ic_play_button));
                }
                else
                {
                    timer = async().execute();
                    binding.bttnPause.setImageDrawable(activity.getDrawable(R.drawable.ic_pause));
                }
            });

            binding.nextExerciceBttn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {
                    if(timer != null)
                    {
                        timer.cancel(true);
                        timer = null;
                    }
                    if(pos != data.size()-1) {
                        current++;
                        tmp_exercise = exercice_time;
                        recyclerView.smoothScrollToPosition(current);
                        String utteranceId = UUID.randomUUID().toString();
                        if(pos == 0)
                            DayAdapter.textToSpeech.stop();

                        textToSpeech.stop();
                        textToSpeech.speak(data.get(pos+1).getSpeak_text(), TextToSpeech.QUEUE_ADD, null,utteranceId);
                    }
                    else
                    {
                        if(!Account.accountFirebase.isPremium()) {
                            if (mInterstitialAd != null) {
                                mInterstitialAd.show(activity);
                                loadAd();
                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                            }
                        }
                        fragmentExercisesBinding.exercicesList.setVisibility(View.GONE);
                       // fragmentExercisesBinding.exerciceLogo.setVisibility(View.GONE);
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
                        Account.saveAccountDays();
                        fragmentExercisesBinding.dayList.getAdapter().notifyDataSetChanged();
                    }
                }
            });


        }


        private AsyncTask<Void, Integer, Void> async()
        {
             return new AsyncTask<Void, Integer, Void>() {
                @Override
                protected void onPostExecute(Void unused) {
                    super.onPostExecute(unused);
                    //binding.nextExerciceBttn.setVisibility(View.VISIBLE);
                    binding.nameExersice.setText(activity.getString(R.string.rest));
                    binding.bttnPause.setVisibility(View.INVISIBLE);
                    binding.nextLiner.setVisibility(View.GONE);
                    //binding.animEx.setFreezesAnimation(true);
                    //((GifDrawable)binding.animEx.getBackground()).start();
                    rest = new AsyncTask<Void, Integer, Void>() {
                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            rest = null;
                            timer = null;
                            binding.timerConstraint.setVisibility(View.GONE);
                            binding.skipBttn.setVisibility(View.GONE);
                            binding.nextLiner.setVisibility(View.VISIBLE);
                            tmp_exercise = exercice_time;

                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            if (values[0] == 25)
                                binding.skipBttn.setVisibility(View.VISIBLE);

                            binding.timeTxt.setText(String.valueOf(values[0]));
                            binding.circularProgressView.animateProgressChange(values[0], 950);
                        }

                        @Override
                        protected void onCancelled() {
                            super.onCancelled();
                            cancel(true);
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
                    for( ;tmp_exercise >= 0; tmp_exercise--)
                    {
                        if(!isCancelled()) {
                            publishProgress(tmp_exercise);
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            return null;
                        }
                    }
                    return null;
                }
            };
        }

        private void setAnim(int pos)
        {
            switch (pos)
            {
                case 0:
                    Glide.with(activity)
                            .load(R.drawable.ex_1)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 1:
                    Glide.with(activity)
                            .load(R.drawable.ex_2)
                            .into(binding.animEx);
                    //.animEx.setFreezesAnimation(false);
                    break;
                case 2:
                    Glide.with(activity)
                            .load(R.drawable.ex_3)
                            .into(binding.animEx);
                   // binding.animEx.setFreezesAnimation(false);
                    break;
                case 3:
                    Glide.with(activity)
                            .load(R.drawable.ex_4)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 4:
                    Glide.with(activity)
                            .load(R.drawable.ex_5)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 5:
                    Glide.with(activity)
                            .load(R.drawable.ex_6)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 6:
                    Glide.with(activity)
                            .load(R.drawable.ex_7)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 7:
                    Glide.with(activity)
                            .load(R.drawable.ex_8)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 8:
                    Glide.with(activity)
                            .load(R.drawable.ex_9)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
                case 9:
                    Glide.with(activity)
                            .load(R.drawable.ex_10)
                            .into(binding.animEx);
                    //binding.animEx.setFreezesAnimation(false);
                    break;
            }
        }

//        private AsyncTask<Void,Void, Void> setEx(String str)
//        {
//            return new AsyncTask<Void, Void, Void>() {
//                @Override
//                protected void onPostExecute(Void unused) {
//                    super.onPostExecute(unused);
//                    binding.animEx.setVideoPath(str);
//                    binding.animEx.start();
//                    binding.animEx.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                        @Override
//                        public void onPrepared(MediaPlayer mp) {
//                            mp.setLooping(true);
//                        }
//                    });
//                }
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    try {
//                        TimeUnit.SECONDS.sleep(2);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//            };
//        }

    }
}
