package com.efw.apps.ui.account;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.efw.apps.MainActivity;
import com.efw.apps.SplashActivity;
import com.efw.apps.ui.exercises.Date;
import com.efw.apps.ui.exercises.Day;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.lang3.SerializationUtils;

import java.util.ArrayList;
import java.util.Calendar;

public class Account {
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static String userName, userEmail;
    public static Drawable userPhoto;
    public static AccountAPP accountAPP;
    public static AccountFirebase accountFirebase;
    public static String URL = "aHR0cHM6Ly9lZnctYXBwcy1kZWZhdWx0LXJ0ZGIuZmlyZWJhc2Vpby5jb20v";
    public static boolean flag = false;

    public static void saveAccount()
    {
        FirebaseDatabase
                .getInstance(new String(Base64.decode(Account.URL, Base64.DEFAULT)))
                .getReference()
                .child("Users/" + Account.currentUser.getUid()).get().addOnCompleteListener(
                new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        try {
                            for (DataSnapshot child : task.getResult().getChildren()) {
                                AccountFirebase accountFirebase_tmp = child.getValue(AccountFirebase.class);

                                byte[] data = SerializationUtils.serialize(Account.accountAPP.array_days_training);
                                String base64 = Base64.encodeToString(data, Base64.DEFAULT);

                                Account.accountFirebase.setArray_days_training(base64);
                                child.getRef().setValue(Account.accountFirebase);
                            }



                        } catch (Exception ex) {
                            String str = ex.getMessage();
                        }
                    }
                });

    }
}
