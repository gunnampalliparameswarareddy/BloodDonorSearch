package com.main.blooddonorserch.view.loginscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.view.home.HomeNavigation;
import com.main.blooddonorserch.view.signupwithgoogle.Signup_with_Google_MobileNumber;

public class LoginScreen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth & Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.btn_google_sign_in).setOnClickListener(v -> signInWithGoogle());

        // Check if already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserExistsInFirestore(currentUser);
        }
    }

    private void signInWithGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    Toast.makeText(this, "Google Sign-In Failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
               // Log.e("LoginScreen", "Google Sign-In Failed", e);
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserExistsInFirestore(user);
                        }
                    } else {
                       // Log.e("LoginScreen", "Firebase Authentication Failed", task.getException());
                        Toast.makeText(this, "Authentication Failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserExistsInFirestore(FirebaseUser user) {
        db.collection("donors").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.getData() != null && !documentSnapshot.getData().isEmpty()) {
                        // Instead of navigating directly, confirm login
                        Toast.makeText(LoginScreen.this, "User exists. Proceeding to Home.", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    } else {
                        // Sign out from Firebase & Google, then navigate to signup
                        logoutAndNavigateToSignup();
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("LoginScreen", "Error checking user in Firestore", e);
                    Toast.makeText(LoginScreen.this, "Error checking user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void logoutAndNavigateToSignup() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            mGoogleSignInClient.revokeAccess().addOnCompleteListener(this, revokeTask -> {
                Toast.makeText(LoginScreen.this, "Account not registered. Please sign up.", Toast.LENGTH_LONG).show();
            });
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginScreen.this, HomeNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
