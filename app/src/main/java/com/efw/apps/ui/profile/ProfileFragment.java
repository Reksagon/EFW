package com.efw.apps.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.efw.apps.MainActivity;
import com.efw.apps.R;
import com.efw.apps.SplashActivity;
import com.efw.apps.databinding.FragmentProfileBinding;
import com.efw.apps.ui.account.Account;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.coreorb.selectiondialogs.data.SelectableIcon;
import pl.coreorb.selectiondialogs.dialogs.IconSelectDialog;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo = null;
    private BillingClient mBillingClient;
    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String mSkuId = "sku_id_1";

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

        FirebaseDatabase
                .getInstance(new String(Base64.decode(Account.URL, Base64.DEFAULT)))
                .getReference()
                .child("mSkuId").get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DataSnapshot> task) {
                try {
                    String id = task.getResult().getValue(String.class);
                    if (id != null)
                        mSkuId = id;
                }
                catch (Exception ex)
                {
                }
            }
        });

        if(Account.accountFirebase.isPremium())
            binding.buyLay.setVisibility(View.GONE);

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
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"efwapps@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(R.string.app_name));
                i.putExtra(Intent.EXTRA_TEXT, getActivity().getString(R.string.email_text));
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

        mBillingClient = BillingClient.newBuilder(getActivity()).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
                if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
                    Account.accountFirebase.setPremium(true);
                    Account.saveAccount();
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    querySkuDetails(); //запрос о товарах

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //сюда мы попадем если что-то пойдет не так
            }
        });


        binding.buyProBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBilling(mSkuId);
            }
        });

        binding.language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new IconSelectDialog.Builder(getContext())
                        .setIcons(sampleIcons())
                        .setTitle(R.string.select_laguage)
                        .setSortIconsByName(false)
                        .setOnIconSelectedListener(new IconSelectDialog.OnIconSelectedListener() {
                            @Override
                            public void onIconSelected(SelectableIcon selectedItem) {
                                String str = selectedItem.getId();
                                setLanguage(str);
                            }
                        })
                        .build().show(getFragmentManager(), "TAG_SELECT_ICON_DIALOG");
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getActivity().getResources().getString(R.string.share_text) + " https://play.google.com/store/apps/details?id=com.efw.apps");
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getActivity().getResources().getString(R.string.share)));
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
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

    private ArrayList<SelectableIcon> sampleIcons() {
        ArrayList<SelectableIcon> selectionDialogsColors = new ArrayList<>();
        selectionDialogsColors.add(new SelectableIcon("en", "English", 0));
        selectionDialogsColors.add(new SelectableIcon("ru", "Русский", 0));
        selectionDialogsColors.add(new SelectableIcon("uk", "Українська", 0));
        selectionDialogsColors.add(new SelectableIcon("es", "Español", 0));
        selectionDialogsColors.add(new SelectableIcon("de", "Deutsch", 0));
        selectionDialogsColors.add(new SelectableIcon("ko", "한국어", 0));
        return selectionDialogsColors;
    }

    private void setLanguage(String locale)
    {
        Locale myLocale = new Locale(locale);

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getActivity().getBaseContext().getResources().updateConfiguration(config, getActivity().getBaseContext().getResources().getDisplayMetrics());

        Account.accountFirebase.setLanguage(locale);
        Account.saveAccount();
        startActivity(new Intent(getActivity(), SplashActivity.class));
    }



    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuId);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {
                if (responseCode == 0) {
                    for (SkuDetails skuDetails : skuDetailsList) {
                        mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                    }
                }
            }
        });
    }

    public void launchBilling(String skuId) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(mSkuDetailsMap.get(skuId))
                .build();
        mBillingClient.launchBillingFlow(getActivity(), billingFlowParams);

    }
}