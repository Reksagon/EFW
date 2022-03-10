package com.efw.apps.ui.exercises;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.ExerciseDayItemBinding;

import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHoler>{
    ArrayList<Day> data;
    Activity activity;


    public DayAdapter(Activity activity, ArrayList<Day> data){
        this.data = data;
        this.activity = activity;
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

        private TextView day_txt, success, day_txt2, success2;
        private ImageView ellipse, center;
        private ExerciseDayItemBinding binding;

        public DayViewHoler(ExerciseDayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            day_txt =  itemView.findViewById(R.id.day);
            success = itemView.findViewById(R.id.success);
            ellipse = itemView.findViewById(R.id.img_ellipse);
            center = itemView.findViewById(R.id.img_center);
            day_txt2 =  itemView.findViewById(R.id.day2);
            success2 = itemView.findViewById(R.id.success2);
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
            }

           day_txt.setText(activity.getResources().getString(R.string.day) + " " + String.valueOf(day.getNum_day()));
           if(day.isSuccess())
           {
               success.setText(activity.getResources().getString(R.string.success));
           }
           else
           {
               success.setText(activity.getResources().getString(R.string.success_no));
               ellipse.setVisibility(View.GONE);
               center.setVisibility(View.GONE);
           }

           if(day.isRest())
           {
               ellipse.setVisibility(View.VISIBLE);
               center.setVisibility(View.VISIBLE);
               center.setImageDrawable(activity.getDrawable(R.drawable.ic_rest));
               success.setText(activity.getResources().getString(R.string.rest));
           }
        }
    }
}
