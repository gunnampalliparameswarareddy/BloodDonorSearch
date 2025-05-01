package com.main.blooddonorserch.view.signupwithgoogle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.main.blooddonorserch.R;

import java.util.concurrent.TimeUnit;

public class OtpVerificationActivity extends AppCompatActivity {
    private EditText otpEditText;
    private Button verifyOtpButton;
    private TextView resendOtpTextView;
    private String verificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String phoneNumber; // Store phone number for resending OTP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        otpEditText = findViewById(R.id.otpEditText);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        resendOtpTextView = findViewById(R.id.resendOtpTextView);
        mAuth = FirebaseAuth.getInstance();

        // Get verification ID and phone number from Intent
        verificationId = getIntent().getStringExtra("VERIFICATION_ID");
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        resendingToken = getIntent().getParcelableExtra("RESEND_TOKEN");

        verifyOtpButton.setOnClickListener(view -> {
            String otp = otpEditText.getText().toString().trim();
            if (otp.length() == 6) {
                verifyOtp(otp);
            } else {
                Toast.makeText(this, "Enter a valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            }
        });

        resendOtpTextView.setOnClickListener(view -> resendOtp());
    }

    private void verifyOtp(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent intent = new Intent(OtpVerificationActivity.this, activity_user_details.class);
                        intent.putExtra("USER_ID", user.getUid());
                        intent.putExtra("PHONE", user.getPhoneNumber());
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Invalid OTP, try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void resendOtp() {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Phone number not available", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Auto-retrieval case (rare)
                                mAuth.signInWithCredential(credential)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                Intent intent = new Intent(OtpVerificationActivity.this, activity_user_details.class);
                                                intent.putExtra("USER_ID", user.getUid());
                                                intent.putExtra("PHONE", user.getPhoneNumber());
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OtpVerificationActivity.this, "OTP Resend Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newVerificationId,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                verificationId = newVerificationId;
                                resendingToken = token;
                                Toast.makeText(OtpVerificationActivity.this, "OTP Resent Successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setForceResendingToken(resendingToken)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }
}
