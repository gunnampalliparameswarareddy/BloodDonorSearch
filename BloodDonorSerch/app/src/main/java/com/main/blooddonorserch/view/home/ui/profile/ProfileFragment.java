package com.main.blooddonorserch.view.home.ui.profile;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.main.blooddonorserch.databinding.FragmentProfileBinding;
import com.main.blooddonorserch.viewmodel.AuthViewModel;
import com.main.blooddonorserch.R;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private AuthViewModel authViewModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.editProfile.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       // Handle edit profile button click
                                                       Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_nav_settings);
                                                   }
                                               });

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        // Observe and update UI dynamically
        authViewModel.getDonorName().observe(getViewLifecycleOwner(), name ->
                binding.profileName.setText(name != null ? name : "Not Available"));

        authViewModel.getDonorisBlocked().observe(getViewLifecycleOwner(), blockedStatus -> {
            if (blockedStatus != null) {
                boolean isBlocked = Boolean.parseBoolean(blockedStatus); // Convert String to Boolean

                if (isBlocked) {
                    binding.blockedTextView.setVisibility(View.VISIBLE);
                    binding.blockedTextView.setText("Your Account is Blocked");
                    binding.blockedTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red));
                } else {
                    binding.blockedTextView.setVisibility(View.GONE); // Hide if not blocked
                }
            } else {
                binding.blockedTextView.setVisibility(View.VISIBLE);
                binding.blockedTextView.setText("Not Available");
            }
        });


        authViewModel.getDonorBloodGroup().observe(getViewLifecycleOwner(), bloodGroup ->
                binding.profileBloodGroup.setText(bloodGroup != null ? bloodGroup : "Not Available"));

        authViewModel.getDonorGender().observe(getViewLifecycleOwner(), gender ->
                binding.profileGender.setText(gender != null ? gender : "Not Available"));

        authViewModel.getDonorBloodDonationType().observe(getViewLifecycleOwner(), donationType ->
                binding.profileBloodDonationType.setText(donationType != null ? donationType : "Not Available"));

        authViewModel.getDonorLastBloodDonationDate().observe(getViewLifecycleOwner(), donationDate ->
                binding.profileBloodDonationDate.setText(donationDate != null ? donationDate : "Not Available"));

        authViewModel.getDonorLastBloodDonationCount().observe(getViewLifecycleOwner(), donationCount ->
                binding.profileBloodDonationCount.setText(donationCount != null ?String.valueOf(donationCount) : "Not Available"));

        authViewModel.getDonorDob().observe(getViewLifecycleOwner(), dob ->
                binding.profileDob.setText(dob != null ? dob : "Not Available"));

        authViewModel.getDonorMobileNumber().observe(getViewLifecycleOwner(), mobile ->
                binding.profileMobileNumber.setText(mobile != null ? mobile : "Not Available"));

        authViewModel.getDonorEmail().observe(getViewLifecycleOwner(), email ->
                binding.profileEmail.setText(email != null ? email : "Not Available"));

        authViewModel.getDonorCity().observe(getViewLifecycleOwner(), city ->
                binding.profileCity.setText(city != null ? city : "Not Available"));

        authViewModel.getDonorState().observe(getViewLifecycleOwner(), state ->
                binding.profileState.setText(state != null ? state : "Not Available"));

        authViewModel.getDonorCountry().observe(getViewLifecycleOwner(), country ->
                binding.profileCountry.setText(country != null ? country : "Not Available"));

        authViewModel.getDonorStatus().observe(getViewLifecycleOwner(), status ->
                binding.profileStatus.setText(status != null ? (status ? "Active" : "Inactive") : "Not Available"));

        return root;
    }
}
