package com.efw.apps.ui.results;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.efw.apps.R;
import com.efw.apps.databinding.FragmentResultsBinding;
import com.efw.apps.ui.account.Account;
import com.efw.apps.ui.exercises.ExerciesAdapter;

public class ResultsFragment extends Fragment {

    private FragmentResultsBinding binding;
    private CalendarAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ResultsViewModel homeViewModel =
                new ViewModelProvider(this).get(ResultsViewModel.class);

        binding = FragmentResultsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new CalendarAdapter(getActivity(), Account.accountFirebase.getStart_training_day(), Account.accountFirebase.getLast_training_day());
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(RecyclerView.HORIZONTAL);
        binding.calendarResults.setLayoutManager(linearLayout);
        binding.calendarResults.setAdapter(adapter);

        binding.countTraining.setText(String.valueOf(Account.accountFirebase.getCount_training()));
        binding.timeTraining.setText(setTime(Account.accountFirebase.getTime_training()));

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

    private String setTime(int time)
    {
        String str = "";
        int hour = time/ 60;
        str += String.valueOf(hour) + getActivity().getString(R.string.hour) + " ";
        int minute = time - (hour * 60);
        str += String.valueOf(minute) + getActivity().getString(R.string.minute);

        return str;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}