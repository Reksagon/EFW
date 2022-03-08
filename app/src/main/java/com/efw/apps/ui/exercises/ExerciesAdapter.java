package com.efw.apps.ui.exercises;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;

import java.util.ArrayList;

public class ExerciesAdapter extends RecyclerView.Adapter<ExerciesAdapter.LinearViewHolder>{
    ArrayList<Exercise> data;

    public ExerciesAdapter(ArrayList<Exercise> data){
        this.data = data;
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

        public LinearViewHolder(View itemView) {
            super(itemView);
            textView =(TextView) itemView.findViewById(R.id.name_exersice);
        }

        public void bind(int pos)
        {
            textView.setText(data.get(pos).getName());
        }
    }
}
