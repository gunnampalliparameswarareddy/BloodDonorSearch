package com.main.blooddonorserch.view.signupwithgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.viewmodel.AuthViewModel;

import java.util.concurrent.TimeUnit;

public class Signup_with_Google_MobileNumber extends AppCompatActivity {
    private static final String TAG = "SignupGoogleMobile";
    private static final int RC_SIGN_UP = 101;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton googleSignInButton;
    private AuthViewModel authViewModel;

    private FirebaseAuth mAuth;

    private EditText phoneEditText;
    private Button sendOtpButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_with_google_mobile_number);

//        phoneEditText = findViewById(R.id.phoneEditText);
//        sendOtpButton = findViewById(R.id.sendOtpButton);
        mAuth = FirebaseAuth.getInstance();

        /**********************************signup with google*******************************/
        googleSignInButton = findViewById(R.id.googleSignInButton);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Handle Google Sign-In
        googleSignInButton.setOnClickListener(view -> googleSignIn());

        // Observe authentication success
        authViewModel.getAuthSuccessLiveData().observe(this, success -> {
            if (success) {
                navigateToUserDetails();
            }
            else
            {
                Toast.makeText(this, "Authentication Failed User ID alreadt Exist", Toast.LENGTH_SHORT).show();
            }
        });

        // Observe authentication errors
        authViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        /************************* signup with google******************/


    }

    /********************************Google Signup Logic***********************/
    // Google Sign-In
    private void googleSignIn() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_UP);
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_UP) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authViewModel.authenticateWithGoogle(account);
            } catch (ApiException e) {
               // Log.e(TAG, "Google Sign-In Failed: " + e.getStatusCode());
                Toast.makeText(this, "Google Sign-In Failed. Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /********************************Google Signup Logic***********************/

    private void navigateToUserDetails() {
        Intent intent = new Intent(Signup_with_Google_MobileNumber.this, activity_user_details.class);
        intent.putExtra("email", authViewModel.getCurrentUser().getEmail());
        startActivity(intent);
        finish();
    }
}

