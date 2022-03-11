package com.efw.apps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.efw.apps.ui.account.Account;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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

public class MainActivity extends AppCompatActivity {

    boolean mode = false;
    private ActivityMainBinding binding;
    public static boolean timer = false;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

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
                    } else {
                        binding.imgMode.setImageDrawable(getResources().getDrawable(R.drawable.ic_moon));
                        mode = true;
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    }
                }
            }
        });

        Account.mAuth = FirebaseAuth.getInstance();
        Account.currentUser = Account.mAuth.getCurrentUser();
        signIn();
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Account.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = Account.mAuth.getCurrentUser();
                            UpdateUI(user);
//                            binding.splashText.setVisibility(View.GONE);
//                            binding.signIn.setVisibility(View.GONE);
//                            binding.spinKit.setVisibility(View.VISIBLE);
//                            editor.putString("SignIN", "Yes");
//                            editor.apply();
                        } else {
//                            UpdateUI(null);
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
            if (account.getPhotoUrl() != null) {
                Glide.with(this).load(account.getPhotoUrl().toString())
                        .thumbnail(0.5f)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.imageView2);
            }
            binding.navView.setVisibility(View.VISIBLE);
        }
    }
}