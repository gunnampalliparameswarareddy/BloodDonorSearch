package com.main.blooddonorserch.view.home.ui.history;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.model.DonationHistory;
import com.main.blooddonorserch.model.Donor;
import com.main.blooddonorserch.view.loginscreen.LoginScreen;
import com.main.blooddonorserch.viewmodel.AuthViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<DonationHistory> donationHistoryList = new ArrayList<>();
    private AuthViewModel authViewModel;
    private FirebaseAuth mAuth;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        historyAdapter = new HistoryAdapter(donationHistoryList);
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(historyAdapter);
        // ✅ Disable default item animation to avoid expanding spaces
        recyclerView.setItemAnimator(null);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            authViewModel.fetchDonationHistory(userId).observe(getViewLifecycleOwner(), historyList -> {
                if (historyList != null && !historyList.isEmpty()) {
                    if (!historyList.equals(donationHistoryList)) { // Prevent redundant updates
                        donationHistoryList.clear();
                        donationHistoryList.addAll(historyList);
                        historyAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(requireContext(), "No donation history found.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "User not logged in. Redirecting to login screen.", Toast.LENGTH_SHORT).show();

            // Redirect to LoginScreen and finish current activity
            Intent intent = new Intent(requireActivity(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish(); // Ensure fragment doesn't stay in back stack
        }
    }


}