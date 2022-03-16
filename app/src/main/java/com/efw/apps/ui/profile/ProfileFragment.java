package com.efw.apps.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.efw.apps.R;
import com.efw.apps.databinding.FragmentProfileBinding;
import com.efw.apps.ui.account.Account;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;

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
        getReviewInfo();

        binding.userName.setText(Account.userName);
        binding.userEmail.setText(Account.userEmail);

        binding.imageView.setImageDrawable(Account.userPhoto);

        binding.contact.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"efwapps@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT   , getActivity().getString(R.string.email_text));
                try {
                    startActivity(Intent.createChooser(i, getActivity().getString(R.string.email)));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.email_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startReviewFlow();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=com.efw.apps");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent,getActivity().getResources().getString(R.string.share)));
            }
        });

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startReviewFlow() {
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.rate_success), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Error: In App Rating failed", Toast.LENGTH_SHORT).show();

        }
    }

    private void getReviewInfo() {
        reviewManager = ReviewManagerFactory.create(getActivity());
        Task<ReviewInfo> manager = reviewManager.requestReviewFlow();
        manager.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reviewInfo = task.getResult();
            } else {
                Toast.makeText(getActivity(), "Error: In App ReviewFlow failed to start", Toast.LENGTH_SHORT).show();
            }
        });
    }
}