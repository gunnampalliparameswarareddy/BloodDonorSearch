package com.main.blooddonorserch.view.home.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.main.blooddonorserch.databinding.FragmentHomeBinding;
import com.main.blooddonorserch.model.Donor;
import com.main.blooddonorserch.viewmodel.AuthViewModel;
import com.main.blooddonorserch.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ArrayList<Donor> donorTableArrayList = new ArrayList<>();
    private ArrayList<Donor> allDonorsList = new ArrayList<>(); // Store full donor list
    private MyCustomAdapter adapter;
    private AuthViewModel authViewModel;
    private String searchText = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Setup Spinner
        String[] items = {"Select Option", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        binding.homeBloodGroupIdInput.setAdapter(spinnerAdapter);

        // Search Button Click Listener
        binding.searchByAddressBtn.setOnClickListener(v -> {
            searchText = binding.searchByAddress.getText().toString().trim();
            filterDonors(binding.homeBloodGroupIdInput.getSelectedItem().toString().trim());
        });

        // RecyclerView Setup
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Initialize Adapter
        adapter = new MyCustomAdapter(donorTableArrayList);
        recyclerView.setAdapter(adapter);

        // Observe Donor Data
        authViewModel.getAllDonors().observe(getViewLifecycleOwner(), donorTables -> {
            if (donorTables != null) {
                allDonorsList.clear();
                allDonorsList.addAll(donorTables);
                filterDonors(binding.homeBloodGroupIdInput.getSelectedItem().toString().trim());
            }
        });

        // Spinner Change Listener for Filtering
        binding.homeBloodGroupIdInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDonors(binding.homeBloodGroupIdInput.getSelectedItem().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        return root;
    }

    private void filterDonors(String bloodGroup) {
        List<Donor> filteredList = new ArrayList<>();

        for (Donor donor : allDonorsList) {
            boolean matchesBloodGroup = bloodGroup.equals("Select Option") || donor.getDonor_blood_group().equalsIgnoreCase(bloodGroup);
            boolean matchesAddress = searchText == null || searchText.isEmpty() ||
                    donor.getDonor_city().equalsIgnoreCase(searchText) ||
                    donor.getDonor_state().equalsIgnoreCase(searchText) ||
                    donor.getDonor_country().equalsIgnoreCase(searchText);
            // Check if donor is eligible for donation
            String isBlocked = donor.getIsBlocked();
            boolean isBlockedBoolean = isBlocked != null && isBlocked.equalsIgnoreCase("true");
            if(!isBlockedBoolean)
            {
                if (matchesBloodGroup && matchesAddress && donor.getDonor_status()) {
                    // Check last donation date eligibility
                    if (isEligibleForDonation(donor.getDonor_last_blood_donation_date(), donor.getBlood_donation_type(),donor.getGender())) {
                        filteredList.add(donor);
                    }
                }
            }
        }

        // Only update the adapter if the filtered list has changed
        if (!donorTableArrayList.equals(filteredList)) {
            donorTableArrayList.clear();
            donorTableArrayList.addAll(filteredList);
            adapter.setDonor(donorTableArrayList);
        }
    }

    private boolean isEligibleForDonation(String lastDonationDate, String donationType,String gender) {
        if (lastDonationDate == null || lastDonationDate.isEmpty() || lastDonationDate.equals("N/A")) {
            return true; // If no donation date is recorded, assume eligible
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date lastDate = sdf.parse(lastDonationDate);
            Date today = new Date();

            if (lastDate == null) return false;

            // Calculate days since last donation
            long daysSinceLastDonation = TimeUnit.MILLISECONDS.toDays(today.getTime() - lastDate.getTime());

            // Define minimum wait periods for different donation types
            int requiredDays = getRequiredWaitDays(donationType,gender);

            return daysSinceLastDonation >= requiredDays;
        } catch (ParseException e) {
            //Log.e("DateDifference", "Error parsing date: " + lastDonationDate, e);
            return false;
        }
    }


    private static final Map<String, Integer> DONATION_MALE_WAIT_DAYS = Map.of(
            "whole blood donation", 90,
            "platelet donation (apheresis)", 14,
            "plasma donation", 31,
            "double red cell donation", 112
    );

    private static final Map<String,Integer> DONATION_FEMALE_WAIT_DAYS = Map.of(
            "whole blood donation", 120,
            "platelet donation (apheresis)", 14,
            "plasma donation", 31,
            "double red cell donation", 112
    );
    private int getRequiredWaitDays(String donationType,String gender) {
        if (donationType == null || donationType.trim().isEmpty()) return 90; // Default 90 days

        String formattedType = donationType.trim().toLowerCase(); // Trim spaces and convert to lowercase

        if ("Male".equalsIgnoreCase(gender)) {  // ✅ Safe null check
            return DONATION_MALE_WAIT_DAYS.getOrDefault(formattedType, 90);
        } else {
            return DONATION_FEMALE_WAIT_DAYS.getOrDefault(formattedType, 90);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
