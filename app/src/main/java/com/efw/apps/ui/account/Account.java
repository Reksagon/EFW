package com.efw.apps.ui.account;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Account {
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    public static String userName, userEmail;
    public static Drawable userPhoto;
}
