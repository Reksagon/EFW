package com.efw.apps;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efw.apps.databinding.ActivitySplashBinding;
import com.efw.apps.ui.account.Account;
import com.efw.apps.ui.account.AccountAPP;
import com.efw.apps.ui.account.AccountFirebase;
import com.efw.apps.ui.exercises.Date;
import com.efw.apps.ui.exercises.Day;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mVisible = true;
        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;

        Account.mAuth = FirebaseAuth.getInstance();
        Account.currentUser = Account.mAuth.getCurrentUser();
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                    }
                });

        signIn();



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }


    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void signIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private ArrayList<Day> getDays()
    {
        Calendar calendar = Calendar.getInstance();
        ArrayList<Day> days = new ArrayList<>();
        for(int i = 1; i <= 100; i++)
        {
            Day day;
            if(i%2 != 0)
                day = new Day(i, false, false);
            else
                day = new Day(i, true, false);

            day.setDay(calendar.get(Calendar.DAY_OF_MONTH));
            day.setMonth(calendar.get(Calendar.MONTH));
            day.setYear(calendar.get(Calendar.YEAR));
            days.add(day);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return days;
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Account.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = Account.mAuth.getCurrentUser();
                            UpdateUI(user);
                            LoadData();
                        } else {
//                            UpdateUI(null);
                        }
                    }
                });
    }

    private void LoadData()
    {
        FirebaseDatabase
                .getInstance(new String(Base64.decode(Account.URL, Base64.DEFAULT)))
                .getReference()
                .child("Users/" + Account.currentUser.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            AccountFirebase accountFirebase = null;
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                accountFirebase = child.getValue(AccountFirebase.class);
                            }
                            Calendar calendar = Calendar.getInstance();
                            if(accountFirebase != null) {
                                Account.accountFirebase = accountFirebase;

                                byte[] data2 = Base64.decode(accountFirebase.getArray_days_training(), Base64.DEFAULT);
                                ArrayList<Day> days = SerializationUtils.deserialize(data2);
                                Account.accountAPP = new AccountAPP(
                                        accountFirebase.getCurrent_training_day(),
                                        accountFirebase.getLast_date_online(),
                                        accountFirebase.getCount_training(),
                                        accountFirebase.getTime_training(),
                                        accountFirebase.getStart_training_day(),
                                        accountFirebase.getLast_training_day(),
                                        accountFirebase.isPremium(),
                                        accountFirebase.isNight_mode(),
                                        accountFirebase.getLanguage(),
                                        days);

                                Date date = accountFirebase.getLast_date_online();
                                if(calendar.get(Calendar.YEAR) - date.getYear() >= 1 ||
                                        calendar.get(Calendar.MONTH) - date.getMonth() >= 1 ||
                                        calendar.get(Calendar.DAY_OF_MONTH) - date.getDay() >=2)
                                {
                                    int i = 0;
                                    for(Day day : Account.accountAPP.array_days_training)
                                    {
                                        if(day.getDay() == date.getDay() &&
                                                day.getMonth() == date.getMonth() &&
                                                day.getYear() == date.getYear()) {
                                            i++;
                                            break;
                                        }
                                        i++;
                                    }

                                    for(; i < 100; i++)
                                    {
                                        Account.accountAPP.array_days_training.get(i).setDay(calendar.get(Calendar.DAY_OF_MONTH));
                                        Account.accountAPP.array_days_training.get(i).setMonth(calendar.get(Calendar.MONTH));
                                        Account.accountAPP.array_days_training.get(i).setYear(calendar.get(Calendar.YEAR));
                                        calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    }
                                    Account.accountFirebase.setLast_training_day(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                                    calendar = Calendar.getInstance();
                                }
                                Account.accountFirebase.setLast_date_online(new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
                                Account.saveAccount();
                            }
                            else {

                                Calendar tmp = Calendar.getInstance();
                                for (int i = 0; i < 100; i++) {
                                    tmp.add(Calendar.DAY_OF_MONTH, 1);
                                }
                                boolean night_mode = false;

                                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO)
                                    night_mode = false;
                                else
                                    night_mode = true;

                                ArrayList<Day> dayArrayList = getDays();
                                byte[] data = SerializationUtils.serialize(dayArrayList);
                                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                                AccountFirebase accountFirebase_new = new AccountFirebase(
                                        base64,
                                        1,
                                        new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)),
                                        0,
                                        0,
                                        new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)),
                                        new Date(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH)),
                                        false,
                                        night_mode,
                                        "ru");

                                Account.accountFirebase = accountFirebase_new;
                                task.getResult().getRef().push().setValue(accountFirebase_new);

                                Account.accountAPP = new AccountAPP(
                                        accountFirebase_new.getCurrent_training_day(),
                                        accountFirebase_new.getLast_date_online(),
                                        accountFirebase_new.getCount_training(),
                                        accountFirebase_new.getTime_training(),
                                        accountFirebase_new.getStart_training_day(),
                                        accountFirebase_new.getLast_training_day(),
                                        accountFirebase_new.isPremium(),
                                        accountFirebase_new.isNight_mode(),
                                        accountFirebase_new.getLanguage(),
                                        dayArrayList);

                            }
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } catch (Exception ex) {
                            String str = ex.getMessage();
                            if(str.equals("Client is offline"))
                                LoadData();
                        }
                    }
                });
    }

    private void UpdateUI(FirebaseUser account)  {
        if(account != null) {
            String name = account.getDisplayName();
            String email = account.getEmail();

            Account.userName = name;
            Account.userEmail = email;
//            if (account.getPhotoUrl() != null) {
//                Glide.with(this).load(account.getPhotoUrl().toString())
//                        .thumbnail(0.5f)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(binding.imageView13);
//            }
        }
    }
}