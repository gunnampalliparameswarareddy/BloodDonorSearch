package com.main.blooddonorserch.view.home.ui.settings;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.databinding.FragmentSettingsBinding;
import com.main.blooddonorserch.view.home.HomeNavigation;
import com.main.blooddonorserch.viewmodel.AuthViewModel;

import java.util.Calendar;

public class SettingsFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private AuthViewModel authViewModel;
    private FragmentSettingsBinding binding;
    private String currentUserId;
    private int calculatedAge = 0;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize View Binding
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        } else {
            Log.e("FirebaseAuth", "User not logged in");
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return root;
        }

        // Blood Donation Type Spinner
        String[] blood_donation_type = {"Select Blood Donation Type", "New Donor", "Whole Blood Donation", "Platelet Donation (Apheresis)", "Plasma Donation", "Double Red Cell Donation"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, blood_donation_type);
        binding.updateBloodDonationType.setAdapter(adapter);

        // Blood Group Spinner
        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        binding.updateBloodGroup.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, bloodGroups));

        // Gender Spinner
        String[] gender = {"Select Gender", "Male", "Female", "Other"};
        binding.updateGender.setAdapter(new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item, gender));
        // Set Date Picker for DOB
        binding.updateDob.setOnClickListener(v -> openDatePicker());

        // Set Date Picker for Last Blood Donation Date
        binding.updateLastBlooddonationdateInput.setOnClickListener(v -> openLastDonationDatePicker());


        binding.updateBloodDonationType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedBloodDonationType = parent.getItemAtPosition(position).toString().trim();
                boolean isNewDonor = selectedBloodDonationType.equals("New Donor");

                int visibility = isNewDonor ? View.GONE : View.VISIBLE;


                binding.lastBlooddonationdateLayout.setVisibility(visibility);
                binding.recepientNameLayout.setVisibility(visibility);
                binding.recepientDonationQuantityLayout.setVisibility(visibility);
                binding.recepientLocationLayout.setVisibility(visibility);
                binding.updateLastBlooddonationdateInput.setVisibility(visibility);
                binding.updateLastDontationImg.setVisibility(visibility);
                binding.updateRecepientName.setVisibility(visibility);
                binding.updateRecepientNameImg.setVisibility(visibility);
                binding.updateDonatedBloodQuantity.setVisibility(visibility);
                binding.updateDonatedBloodQuantityImg.setVisibility(visibility);
                binding.updateRecepientLocation.setVisibility(visibility);
                binding.updateRecepientLocationImg.setVisibility(visibility);
                if (isNewDonor) {
                    binding.updateLastBlooddonationdateInput.setText("N/A");
                    binding.updateRecepientName.setText("N/A");
                    binding.updateDonatedBloodQuantity.setText("N/A");
                    binding.updateRecepientLocation.setText("N/A");
                }
                else
                {
                    if("N/A".equals(binding.updateLastBlooddonationdateInput.getText().toString()))
                    {
                        binding.updateLastBlooddonationdateInput.setText("");
                    }
                    if("N/A".equals(binding.updateRecepientName.getText().toString()))
                    {
                        binding.updateRecepientName.setText("");
                    }
                    if("N/A".equals(binding.updateDonatedBloodQuantity.getText().toString()))
                    {
                        binding.updateDonatedBloodQuantity.setText("");
                    }
                    if("N/A".equals(binding.updateRecepientLocation.getText().toString()))
                    {
                        binding.updateRecepientLocation.setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Save Profile Data
        binding.saveProfile.setOnClickListener(view -> saveProfileData());

        authViewModel.getDonorStatus().observe(getViewLifecycleOwner(), status -> {
            if(status != null)
            {
                if(status)
                {
                    binding.AccountStatus.setText("Deactivate");
                    binding.AccountStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.red_1));
                    binding.statusIcon.setImageResource(R.drawable.deactivate); // Change icon
                }
                else
                {
                    binding.AccountStatus.setText("Activate");
                    binding.AccountStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green_1));
                    binding.statusIcon.setImageResource(R.drawable.activate); // Change icon
                }
            }
        });

        //Account Status
        binding.AccountStatus.setOnClickListener(v -> {
            boolean isActive = binding.AccountStatus.getText().toString().equals("Activate");

            if (isActive) {
                // Change to Deactivate state
                binding.AccountStatus.setText("Deactivate");
                binding.AccountStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.red_1));
                binding.statusIcon.setImageResource(R.drawable.deactivate); // Change icon
                authViewModel.EnableAccount(firebaseAuth.getCurrentUser().getUid(),this.getContext());
            } else {
                // Change to Activate state
                binding.AccountStatus.setText("Activate");
                binding.AccountStatus.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.green_1));
                binding.statusIcon.setImageResource(R.drawable.activate); // Change icon
                authViewModel.DisableAccount(firebaseAuth.getCurrentUser().getUid(),this.getContext());
            }
        });
        //Delete Account
        binding.deleteAccount.setOnClickListener(v ->{
            showDeleteConfirmationDialog();
        });

        return root;
    }

    // DatePicker for Date of Birth
    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = 2000; // Default year
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String dob = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            binding.updateDob.setText(dob);
            calculateAge(selectedYear, selectedMonth, selectedDay);
        }, year, month, day).show();
    }

    // Calculate Age from DOB
    private void calculateAge(int year, int month, int day) {
        Calendar today = Calendar.getInstance();
        calculatedAge = today.get(Calendar.YEAR) - year;
        if (today.get(Calendar.MONTH) < month || (today.get(Calendar.MONTH) == month && today.get(Calendar.DAY_OF_MONTH) < day)) {
            calculatedAge--;
        }
    }

    // DatePicker for Last Blood Donation Date
    private void openLastDonationDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            String donationDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
            binding.updateLastBlooddonationdateInput.setText(donationDate);
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void saveProfileData() {

        boolean isDataUpdated = false;

        if (!binding.updateName.getText().toString().trim().isEmpty()) {
            authViewModel.updateName(currentUserId, binding.updateName.getText().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (!binding.updateGender.getSelectedItem().toString().trim().equals("Select Gender")) {
            authViewModel.updateGender(currentUserId, binding.updateGender.getSelectedItem().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (!binding.updateBloodGroup.getSelectedItem().toString().trim().equals("Select Blood Group")) {
            authViewModel.updateBloodGroup(currentUserId, binding.updateBloodGroup.getSelectedItem().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (!binding.updateDob.getText().toString().trim().isEmpty()) {
            if (calculatedAge < 18) {
                Toast.makeText(requireContext(), "You are not eligible to donate blood", Toast.LENGTH_SHORT).show();
                return;
            } else {
                authViewModel.updateDOB(currentUserId, binding.updateDob.getText().toString().trim(), calculatedAge, this.getContext());
                isDataUpdated = true;
            }
        }

        /* Mobile Number Validation */
        final boolean[] isDataUpdatedWrapper = {isDataUpdated};
        if (!binding.updateMobilenumber.getText().toString().trim().isEmpty()) {
            if (binding.updateMobilenumber.getText().toString().trim().length() != 13) {
                binding.updateMobilenumber.setError("Enter Valid Mobile Number with Country Code");
                Toast.makeText(requireContext(), "Enter Valid Mobile Number with Country Code", Toast.LENGTH_SHORT).show();
                return;
            }

            String mobile_number = binding.updateMobilenumber.getText().toString().trim();
            LiveData<Boolean> mobileExistsLiveData = authViewModel.checkIfMobileExists(mobile_number);

            mobileExistsLiveData.observe(getViewLifecycleOwner(), exists -> {
                if (exists != null && exists) {
                    binding.updateMobilenumber.setError("Mobile number already registered. It won't update.");
                    Toast.makeText(requireContext(), "Mobile number already registered. It won't update.", Toast.LENGTH_SHORT).show();
                } else {
                    authViewModel.updateMobileNumber(currentUserId, mobile_number, getContext());
                    isDataUpdatedWrapper[0] = true; // Modify the wrapper array instead
                }

                // Ensure observer is removed to avoid memory leaks
                mobileExistsLiveData.removeObservers(getViewLifecycleOwner());

                // ✅ Proceed only after validation completes
                updateRemainingProfileData(isDataUpdatedWrapper[0]);
            });

            return; // Stop further execution until observer completes
        }

        // If mobile number is not being updated, proceed with other updates
        updateRemainingProfileData(isDataUpdated);
    }

    // ✅ This method ensures navigation happens only after mobile number validation is complete
    private void updateRemainingProfileData(boolean isDataUpdated) {
        if (!binding.updateBloodDonationType.getSelectedItem().toString().trim().equals("Select Blood Donation Type")) {
            String selectedBloodDonationType = binding.updateBloodDonationType.getSelectedItem().toString().trim();
            String lastBloodDonationDate = binding.updateLastBlooddonationdateInput.getText().toString().trim();
            String recepientName = binding.updateRecepientName.getText().toString().trim();
            String donatedBloodQuantity = binding.updateDonatedBloodQuantity.getText().toString().trim();
            String recepientLocation = binding.updateRecepientLocation.getText().toString().trim();

            if (!selectedBloodDonationType.equals("New Donor") && lastBloodDonationDate.isEmpty() && recepientName.isEmpty() && donatedBloodQuantity.isEmpty() && recepientLocation.isEmpty()) {
                binding.updateLastBlooddonationdateInput.setError("Enter Last Blood Donation Date");
                binding.updateRecepientName.setError("Enter Recepient Name");
                binding.updateDonatedBloodQuantity.setError("Enter Donated blood quantity in ml");
                binding.updateRecepientLocation.setError("Enter Recepient Address");
                return;
            }
            if(!selectedBloodDonationType.equals("New Donor") && !lastBloodDonationDate.isEmpty() && recepientName.isEmpty() )
            {
                binding.updateRecepientName.setError("Enter Recepient Name");
                return;
            }
            if(!selectedBloodDonationType.equals("New Donor") && !lastBloodDonationDate.isEmpty() && !recepientName.isEmpty() && donatedBloodQuantity.isEmpty() )
            {
                binding.updateDonatedBloodQuantity.setError("Enter Donated blood quantity in ml");
                return;
            }
            if(!selectedBloodDonationType.equals("New Donor") && !lastBloodDonationDate.isEmpty() && !recepientName.isEmpty() && !donatedBloodQuantity.isEmpty() && recepientLocation.isEmpty() )
            {
                binding.updateRecepientLocation.setError("Enter Recepient Address");
                return;
            }

            authViewModel.getDonorLastBloodDonationCount().observe(getViewLifecycleOwner(), countValue -> {
                int lastCount = (countValue != null) ? Integer.parseInt(countValue) : 1;
                if (!selectedBloodDonationType.equals("New Donor")) {
                    lastCount++;  // Increment donation count only if not a new donor
                }
                String count = String.valueOf(lastCount);

                authViewModel.updateLastBloodDonationDate(
                        currentUserId,
                        selectedBloodDonationType.equals("New Donor") ? "N/A" : lastBloodDonationDate,
                        selectedBloodDonationType,
                        recepientName,
                        donatedBloodQuantity,
                        recepientLocation,
                        count,
                        this.getContext()
                );
            });
            isDataUpdated = true;
        }

        if (!binding.updateCity.getText().toString().trim().isEmpty()) {
            authViewModel.updateCity(currentUserId, binding.updateCity.getText().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (!binding.updateState.getText().toString().trim().isEmpty()) {
            authViewModel.updateState(currentUserId, binding.updateState.getText().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (!binding.updateCountry.getText().toString().trim().isEmpty()) {
            authViewModel.updateCountry(currentUserId, binding.updateCountry.getText().toString().trim(), this.getContext());
            isDataUpdated = true;
        }

        if (isDataUpdated) {
            Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            // ✅ Navigate to Profile Fragment after a short delay
            new android.os.Handler().postDelayed(() -> {
                requireActivity().getSupportFragmentManager().popBackStack();
            }, 1000);
        }
    }



    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to Delete Account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        authViewModel.DeleteAccount(firebaseAuth.getCurrentUser().getUid(), requireContext());
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
