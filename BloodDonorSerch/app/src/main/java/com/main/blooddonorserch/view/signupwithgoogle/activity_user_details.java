package com.main.blooddonorserch.view.signupwithgoogle;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.model.Donor;
import com.main.blooddonorserch.view.home.HomeNavigation;
import com.main.blooddonorserch.viewmodel.AuthViewModel;

import java.util.Calendar;

public class activity_user_details extends AppCompatActivity {
    private EditText nameEditText, cityEditText, stateEditText, countryEditText, phoneEditText, ageEditText, lastDonationDateEditText, weightEditText,recipient_name,recipient_blood_quantity,recipient_location,last_blood_donation_count;
    private Spinner bloodGroupSpinner, bloodDonationType,gender;
    private CheckBox Checkbox;
    private Button saveButton;
    private AuthViewModel authViewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private int calculatedAge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        initializeUI();
        setupSpinners();
        setupListeners();
    }

    private void initializeUI() {
        nameEditText = findViewById(R.id.nameEditText);
        cityEditText = findViewById(R.id.cityEditText);
        stateEditText = findViewById(R.id.stateEditText);
        countryEditText = findViewById(R.id.countryEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        ageEditText = findViewById(R.id.ageEditText);
        lastDonationDateEditText = findViewById(R.id.last_blood_donation_date_EditText);
        bloodGroupSpinner = findViewById(R.id.bloodGroupSpinner);
        gender = findViewById(R.id.genderType);
        bloodDonationType = findViewById(R.id.spinner_blood_donation_type);
        weightEditText = findViewById(R.id.weightEditText);
        saveButton = findViewById(R.id.saveButton);
        Checkbox = findViewById(R.id.checkBoxTerms);
        recipient_name = findViewById(R.id.recipient_name);
        recipient_blood_quantity = findViewById(R.id.recipient_blood_quantity);
        recipient_location = findViewById(R.id.recipient_location);
        last_blood_donation_count = findViewById(R.id.last_donation_count_EditText);


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void setupSpinners() {
        String[] gender_types = {"Select Gender Type","Male","Female","Others"};
        gender.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,gender_types));

        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        bloodGroupSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bloodGroups));

        String[] donationTypes = {"Select Blood Donation Type", "New Donor", "Whole Blood Donation", "Platelet Donation (Apheresis)", "Plasma Donation", "Double Red Cell Donation"};
        bloodDonationType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, donationTypes));
    }

    private void setupListeners() {
        ageEditText.setOnClickListener(v -> openDatePicker());
        lastDonationDateEditText.setOnClickListener(v -> openLastDonationDatePicker());

        bloodDonationType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String selectedType = bloodDonationType.getSelectedItem().toString();
                if ("New Donor".equals(selectedType)) {
                    lastDonationDateEditText.setVisibility(View.GONE);
                    lastDonationDateEditText.setText("N/A");
                    last_blood_donation_count.setVisibility(View.GONE);
                    recipient_name.setVisibility(View.GONE);
                    recipient_blood_quantity.setVisibility(View.GONE);
                    recipient_location.setVisibility(View.GONE);
                    recipient_name.setText("N/A");
                    recipient_blood_quantity.setText("N/A");
                    recipient_location.setText("N/A");
                    last_blood_donation_count.setText("0");
                }
                else {
                    lastDonationDateEditText.setVisibility(View.VISIBLE);
                    if ("N/A".equals(lastDonationDateEditText.getText().toString())) {
                        lastDonationDateEditText.setText(""); // Clear N/A
                    }

                    last_blood_donation_count.setVisibility(View.VISIBLE);
                    recipient_name.setVisibility(View.VISIBLE);
                    recipient_blood_quantity.setVisibility(View.VISIBLE);
                    recipient_location.setVisibility(View.VISIBLE);
                    if("0".equals(last_blood_donation_count.getText().toString()))
                    {
                        last_blood_donation_count.setText("");
                    }
                    if ("N/A".equals(recipient_name.getText().toString())) {
                        recipient_name.setText(""); // Clear N/A
                    }
                    if ("N/A".equals(recipient_blood_quantity.getText().toString())) {
                        recipient_blood_quantity.setText(""); // Clear N/A
                    }
                    if ("N/A".equals(recipient_location.getText().toString())) {
                        recipient_location.setText(""); // Clear N/A
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        saveButton.setOnClickListener(view -> saveUserDetails());
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            ageEditText.setText(day + "/" + (month + 1) + "/" + year);
            calculateAge(year, month, day);
        }, 2000, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void openLastDonationDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            lastDonationDateEditText.setText(day + "/" + (month + 1) + "/" + year);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void calculateAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        calculatedAge = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) < month || (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            calculatedAge--;
        }
    }

    private void saveUserDetails() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User authentication failed", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = currentUser.getUid();
        String name = nameEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String country = countryEditText.getText().toString().trim();
        String email = currentUser.getEmail();
        String mobile_number = phoneEditText.getText().toString().trim();
        String gendertypes = gender.getSelectedItem().toString();
        String bloodGroup = bloodGroupSpinner.getSelectedItem().toString();
        String blood_donation_Type = bloodDonationType.getSelectedItem().toString();
        String lastDonationDate = "New Donor".equals(blood_donation_Type) ? "N/A" : lastDonationDateEditText.getText().toString().trim();
        String dob = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();
        String last_blood_donation_count_value = last_blood_donation_count.getText().toString().trim();
        String recipient_Name = recipient_name.getText().toString().trim();
        String recipient_blood_Quantity = recipient_blood_quantity.getText().toString().trim();
        String recipient_Location = recipient_location.getText().toString().trim();

        // 🔹 Step 1: Validate Inputs
        if (!validateInputs(id, name, city, state, country, mobile_number, gendertypes, bloodGroup, blood_donation_Type, lastDonationDate, dob, weight,recipient_Name,recipient_blood_Quantity,recipient_Location,last_blood_donation_count_value)) {
            return; // Stop execution if validation fails
        }

        // 🔹 Step 2: Check if mobile number already exists **before proceeding**
        authViewModel.checkIfMobileExists(mobile_number).observe(this, exists -> {
            if (exists != null && exists) {
                // 🚀 Stop execution immediately
                phoneEditText.setError("Mobile number already registered. Please Sign in.");
                Toast.makeText(this, "Mobile number already registered. Please Sign in.", Toast.LENGTH_SHORT).show();

                // **Important: Remove observer after execution**
                authViewModel.checkIfMobileExists(mobile_number).removeObservers(this);
                return; // ✅ STOP HERE!
            }

            // ✅ Only proceed if mobile number does NOT exist
            authViewModel.checkIfMobileExists(mobile_number).removeObservers(this);
            proceedWithSignup(id, name, city, state, country, email, mobile_number, bloodGroup, lastDonationDate, blood_donation_Type, dob, weight, gendertypes,recipient_Name,recipient_blood_Quantity,recipient_Location);
        });
    }


    private boolean validateInputs(String id, String name, String city, String state, String country,
                                   String mobile_number, String gender, String bloodGroup,
                                   String blood_donation_Type, String lastDonationDate, String dob, String weight,
                                   String recipientName, String recipientBloodQuantity, String recipientLocation,String last_blood_donation_count_value)
    {
        if(id == null || id.isEmpty())
        {
            Toast.makeText(this, "User authentication failed", Toast.LENGTH_SHORT).show();
            nameEditText.setError("User authentication failed");
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            nameEditText.setError("Please enter your name");
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(city)) {
            cityEditText.setError("Please enter your city");
            Toast.makeText(this, "Please enter your city", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(state)) {
            stateEditText.setError("Please enter your state");
            Toast.makeText(this, "Please enter your state", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(country)) {
            countryEditText.setError("Please enter your country");
            Toast.makeText(this, "Please enter your country", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mobile_number)) {
            phoneEditText.setError("Please enter your mobile number");
            Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(mobile_number.length()  < 13)
        {
            phoneEditText.setError("Please enter a valid mobile number with country code");
            Toast.makeText(this, "Please enter a valid mobile number with country code", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bloodGroup.equals("Select Blood Group")) {
            Toast.makeText(this, "Please select your blood group", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(gender.equals("Select Gender Type"))
        {
            Toast.makeText(this,"Please select your gender type",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(lastDonationDate) && !"New Donor".equals(blood_donation_Type)) {

            lastDonationDateEditText.setError("Please select your last blood donation date");
            Toast.makeText(this, "Please select your last blood donation date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(last_blood_donation_count_value) && !"New Donor".equals(blood_donation_Type))
        {
            last_blood_donation_count.setError("Please enter your last blood donation count");
            Toast.makeText(this, "Please enter your last blood donation count", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            ageEditText.setError("Please select your date of birth");
            Toast.makeText(this, "Please select your date of birth", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(blood_donation_Type.equals("Select Blood Donation Type"))
        {
            Toast.makeText(this, "Please select your blood donation type", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(TextUtils.isEmpty(weight))
        {
            weightEditText.setError("Please enter your weight");
            Toast.makeText(this, "Please enter your weight", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!Checkbox.isChecked())
        {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        int weight_val = Integer.parseInt(weight);
        if(weight_val < 50)
        {
            weightEditText.setError("You are not eligible to donate blood. The minimum required weight is 50 kg.");
            Toast.makeText(this, "You are not eligible to donate blood. The minimum required weight is 50 kg.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(calculatedAge < 18)
        {
            ageEditText.setError("You are not eligible to donate blood. The minimum required age is 18 years.");
            Toast.makeText(this, "You are not eligible to donate blood. The minimum required age is 18 years.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(weight_val < 50 && calculatedAge < 18)
        {
            weightEditText.setError("You are not eligible to donate blood. The minimum required weight is 50 kg and the minimum required age is 18 years.");
            Toast.makeText(this, "You are not eligible to donate blood. The minimum required weight is 50 kg and the minimum required age is 18 years", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(recipientName == null || recipientName.isEmpty())
        {
            recipient_name.setError("Please enter your recipient name");
            Toast.makeText(this, "Please enter your recipient name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(recipientBloodQuantity == null || recipientBloodQuantity.isEmpty())
        {
            recipient_blood_quantity.setError("Please enter your recipient blood quantity");
            Toast.makeText(this, "Please enter your recipient blood quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(recipientLocation == null || recipientLocation.isEmpty()) {
            recipient_location.setError("Please enter your recipient location");
            Toast.makeText(this, "Please enter your recipient location", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!Checkbox.isChecked())
        {
            Toast.makeText(this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void proceedWithSignup(String id, String name, String city, String state, String country, String email,
                                   String mobile_number, String bloodGroup, String lastDonationDate,
                                   String blood_donation_Type, String dob, String weight, String gender, String recipientName,String recipientBloodQuantity,String recipientLocation) {

        String donationCountStr = last_blood_donation_count.getText().toString().trim();
        int donation_count = blood_donation_Type.equals("New Donor") ? 0 : (donationCountStr.isEmpty() ? 1 : Integer.parseInt(donationCountStr));

        Donor donor = new Donor(id, name, city, state, country, email, mobile_number, bloodGroup, lastDonationDate,
                blood_donation_Type, dob, String.valueOf(calculatedAge),
                String.valueOf(donation_count), weight, gender,recipientName,recipientBloodQuantity,recipientLocation);

        authViewModel.saveUserDetails(donor).observe(this, success -> {
            if (success) {
                navigateToHome();
            } else {
                Toast.makeText(this, "Failed to save details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()  // Sign out from Google
                .addOnCompleteListener(task -> {
                   // Log.d("Auth", "User signed out");
                    finish(); // Close the activity
                });
        //navigateToSignup();
        super.onBackPressed();  // Call this at the end if needed
    }

    private void navigateToHome() {
        Intent intent = new Intent(activity_user_details.this, HomeNavigation.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
