package com.efw.apps.ui.results;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.LayoutCalenadrDayBinding;
import com.efw.apps.ui.account.Account;
import com.efw.apps.ui.exercises.Date;
import com.efw.apps.ui.exercises.Day;

import java.util.ArrayList;
import java.util.Calendar;



public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    Calendar caledar = Calendar.getInstance();
    Activity activity;
    ArrayList<Day> days = new ArrayList<>();

    public CalendarAdapter(Activity activity, Date start_date, Date end_date){
        this.activity = activity;

        caledar.set(start_date.getYear(), start_date.getMonth(), start_date.getDay());
        for (int i = 0; i < Account.accountAPP.array_days_training.size()-1; i++) {
            Day day_first = Account.accountAPP.array_days_training.get(i);
            Day day_last = Account.accountAPP.array_days_training.get(i+1);

            if(day_last.getDay() - day_first.getDay() > 1
            && day_last.getMonth() == day_first.getMonth())
            {
                caledar.set(day_first.getYear(), day_first.getMonth(), day_first.getDay());
                caledar.add(Calendar.DAY_OF_MONTH, 1);
                days.add(day_first);
                while (true)
                {
                    Day day = new Day(999, caledar.get(Calendar.YEAR), caledar.get(Calendar.MONTH), caledar.get(Calendar.DAY_OF_MONTH),
                            false, false);
                    days.add(day);
                    if(day_last.getDay() - caledar.get(Calendar.DAY_OF_MONTH) == 1)
                        break;
                    caledar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            else
                days.add(day_first);
        }
    }

    @Override
    public CalendarAdapter.CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LayoutCalenadrDayBinding binding = LayoutCalenadrDayBinding.inflate(inflater, parent, false);
        return new CalendarAdapter.CalendarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(CalendarAdapter.CalendarViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.bind(days.get(position));
    }
    @Override
    public int getItemCount() {
        return days.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        LayoutCalenadrDayBinding binding;
        Calendar calendar_tmp = Calendar.getInstance();

        public CalendarViewHolder(LayoutCalenadrDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void bind(Day day) {
            calendar_tmp.set(day.getYear(), day.getMonth(), day.getDay());
            setDayOfWeek(calendar_tmp.get(Calendar.DAY_OF_WEEK));
            binding.numDay.setText(String.valueOf(day.getDay()));
            if(day.isSuccess())
                binding.checkDay.setImageDrawable(activity.getDrawable(R.drawable.ic_check));
            else if(day.getNum_day() == 999)
                binding.imgPoint.setVisibility(View.VISIBLE);
            else if(day.isRest()) {
                binding.imgPoint.setVisibility(View.VISIBLE);
                binding.imgPoint.setImageDrawable(activity.getDrawable(R.drawable.ic_rest));
            }
        }

        private void setDayOfWeek(int i)
        {
            switch (i)
            {
                case 1: binding.dayOfWeek.setText(activity.getString(R.string.sunday)); break;
                case 2: binding.dayOfWeek.setText(activity.getString(R.string.monday)); break;
                case 3: binding.dayOfWeek.setText(activity.getString(R.string.tuesday)); break;
                case 4: binding.dayOfWeek.setText(activity.getString(R.string.wednesday));break;
                case 5: binding.dayOfWeek.setText(activity.getString(R.string.thursday));break;
                case 6: binding.dayOfWeek.setText(activity.getString(R.string.friday)); break;
                case 7: binding.dayOfWeek.setText(activity.getString(R.string.saturday)); break;
            }
        }
    }
}