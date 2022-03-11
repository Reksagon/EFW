package com.efw.apps.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.efw.apps.R;
import com.efw.apps.databinding.FragmentProfileBinding;
import com.efw.apps.ui.account.Account;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Account.userPhoto == null) {
            ImageView imageView = getActivity().findViewById(R.id.imageView2);
            Account.userPhoto = imageView.getDrawable();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.userName.setText(Account.userName);
        binding.userEmail.setText(Account.userEmail);

        binding.imageView.setImageDrawable(Account.userPhoto);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}