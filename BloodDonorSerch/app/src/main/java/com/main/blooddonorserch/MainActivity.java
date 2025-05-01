package com.main.blooddonorserch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.blooddonorserch.view.home.HomeNavigation;
import com.main.blooddonorserch.view.loginscreen.LoginScreen;
import com.main.blooddonorserch.view.signupwithgoogle.Signup_with_Google_MobileNumber;


public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        FirebaseApp.initializeApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        // Retrieve login state from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        // Check if the user is logged in
        //isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", false);
        if(isLoggedIn || firebaseUser != null)
        {
            gotoHomePage();
            return;
        }

        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btn = findViewById(R.id.DonorAccountSignup);
        Button btn1 = findViewById(R.id.DonorAccountDetails);
        Button btn2 = findViewById(R.id.DonorSearch);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSignupScreen();
            }
        });

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLoginScreen();
                //logoutUser();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoHomePage();
            }
        });

    }

    public void gotoHomePage()
    {
        Intent intent = new Intent(this, HomeNavigation.class);
        //Log.d("HomePage", "gotoHomePage: ");
        startActivity(intent);
    }

    public void gotoLoginScreen()
    {
        Intent log = new Intent(this, LoginScreen.class);
        startActivity(log);
    }

    public void gotoSignupScreen()
    {
        Intent sign = new Intent(this, Signup_with_Google_MobileNumber.class);
        startActivity(sign);
    }
}