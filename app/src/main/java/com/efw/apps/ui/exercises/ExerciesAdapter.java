package com.efw.apps.ui.exercises;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.FragmentExercisesBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class ExerciesAdapter extends RecyclerView.Adapter<ExerciesAdapter.LinearViewHolder>{
    ArrayList<Exercise> data;
    private int current = 0;
    public RecyclerView recyclerView;
    private TextToSpeech textToSpeech;
    FragmentExercisesBinding binding;

    public ExerciesAdapter(ArrayList<Exercise> data, RecyclerView recyclerView, Context context, FragmentExercisesBinding binding){
        this.data = data;
        this.recyclerView = recyclerView;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                Locale language = new Locale("ru");
                textToSpeech.setLanguage(language);
            }
        });
        this.binding = binding;
    }

    @Override
    public ExerciesAdapter.LinearViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_exersice_item, parent, false);
        return new ExerciesAdapter.LinearViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExerciesAdapter.LinearViewHolder holder, int position) {
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    class LinearViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private Button next;

        public LinearViewHolder(View itemView) {
            super(itemView);
            textView =(TextView) itemView.findViewById(R.id.name_exersice);
            next = itemView.findViewById(R.id.next_exercice_bttn);
        }

        public void bind(int pos)
        {
            textView.setText(data.get(pos).getName());
            if(pos == data.size()-1)
                next.setText("End");

            next.setOnClickListener(new View.OnClickListener() {
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
                        binding.exercicesList.setVisibility(View.GONE);
                        binding.startBttn.setVisibility(View.VISIBLE);
                        recyclerView.smoothScrollToPosition(0);
                        current = 0;
                    }
                }
            });
        }
    }
}
