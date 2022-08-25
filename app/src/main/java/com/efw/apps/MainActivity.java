package com.efw.apps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efw.apps.ui.account.Account;
import com.efw.apps.ui.exercises.ExercisesFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.efw.apps.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static boolean mode = false;
    private ActivityMainBinding binding;
    public static boolean timer = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLanguage();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
        {
            binding.imgMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_moon));
        }
        else
        {
            binding.imgMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_sundim));
            mode = true;
        }



        binding.imgMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!timer) {
                    if (mode) {
                        binding.imgMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_sundim));
                        mode = false;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        Account.accountFirebase.setNight_mode(false);
                        Account.saveAccount();
                    } else {
                        binding.imgMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_moon));
                        mode = true;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Account.accountFirebase.setNight_mode(true);
                        Account.saveAccount();
                    }
                    setLanguage();
                }
            }
        });

        if (Account.currentUser.getPhotoUrl() != null) {
            Glide.with(this).load(Account.currentUser.getPhotoUrl().toString())
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imageView2);
        }

        if(Account.accountFirebase.isPremium())
        {
            binding.adView.setVisibility(View.GONE);
            Calendar now = Calendar.getInstance();
            Calendar premium = Calendar.getInstance();
            String[] str = Account.accountFirebase.getLast_date_premium().split("\\.");
            premium.set(Calendar.DAY_OF_MONTH, Integer.parseInt(str[0]));
            premium.set(Calendar.MONTH, Integer.parseInt(str[1]));
            premium.set(Calendar.YEAR, Integer.parseInt(str[2]));

            if(now.after(premium))
            {
                Account.accountFirebase.setPremium(false);
                Account.saveAccount();
            }
        }




    }

    @Override
    public void onBackPressed() {
        if(timer)
        {
            ExercisesFragment.startMenu();
            findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
            MainActivity.timer = false;
        }
        else
            super.onBackPressed();
    }

    public void setLanguage()
    {
        Locale myLocale = new Locale(Account.accountFirebase.getLanguage());

        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }
}