package com.efw.apps.ui.results;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.LayoutCalenadrDayBinding;

import java.util.ArrayList;
import java.util.Calendar;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    Calendar caledar = Calendar.getInstance();
    Activity activity;
    ArrayList<Calendar> calendars = new ArrayList<>();

    public CalendarAdapter(Activity activity){
        this.activity = activity;
        for(int i = 0; i < 25; i++)
        {
            Calendar tmp = Calendar.getInstance();
            tmp.set(caledar.get(Calendar.YEAR), caledar.get(Calendar.MONTH), caledar.get(Calendar.DAY_OF_MONTH));
            calendars.add(tmp);
            caledar.add(Calendar.DAY_OF_MONTH, 1);
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
        holder.bind(position);
    }
    @Override
    public int getItemCount() {
        return calendars.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        LayoutCalenadrDayBinding binding;

        public CalendarViewHolder(LayoutCalenadrDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            setDayOfWeek(calendars.get(pos).get(Calendar.DAY_OF_WEEK));
            binding.numDay.setText(String.valueOf(calendars.get(pos).get(Calendar.DAY_OF_MONTH)));
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