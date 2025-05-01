package com.main.blooddonorserch.view.home.ui.about;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.blooddonorserch.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_terms_policies#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_terms_policies extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_terms_policies() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_terms_policies.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_terms_policies newInstance(String param1, String param2) {
        fragment_terms_policies fragment = new fragment_terms_policies();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terms_policies, container, false);

        // Set Terms & Policies Text
        TextView termsText = view.findViewById(R.id.termsText);
        termsText.setText(getTermsAndPolicies());

        return view;
    }

    // Method to return Terms and Policies
    private String getTermsAndPolicies() {

        String termsAndPolicies =
                "Privacy Policy\n" +
                        "--------------------\n" +
                        "1. We collect Name, Blood Group, Location and Contact Details for connecting donors with recipients.\n" +
                        "2. Your data is securely stored and not shared without consent.\n" +
                        "3. You may request data deletion by contacting support.\n" +
                        "4. Recipient information is collected solely for donation history tracking.\n\n" +

                        "Terms of Use\n" +
                        "--------------------\n" +
                        "1. The app is a platform to connect donors with recipients; we do not guarantee blood availability.\n" +
                        "2. Users must provide accurate and truthful information.\n" +
                        "3. Misuse of donor information is strictly prohibited.\n\n" +

                        "Medical Disclaimer\n" +
                        "--------------------\n" +
                        "1. This app does not replace professional medical advice.\n" +
                        "2. Blood donation eligibility:\n" +
                        "   - Minimum age: 18 years\n" +
                        "   - Minimum weight: 50kg\n" +
                        "   - No recent illness or infections\n" +
                        "3. Consult a medical professional before donating.\n\n" +

                        "Donation Type & Visibility\n" +
                        "--------------------\n" +
                        "1. New donors will be visible immediately to recipients.\n" +
                        "2. Donors who have recently donated will be temporarily hidden based on donation type:\n" +
                        "   - Whole Blood Donation: Hidden for 3 months (Male) and 4 months (Female & Others)\n" +
                        "   - Platelet Donation (Apheresis): Hidden for 2 weeks\n" +
                        "   - Plasma Donation: Hidden for 1 month\n" +
                        "   - Double Red Cell Donation: Hidden for 4 months\n" +
                        "3. Once the waiting period ends, the donor’s profile will automatically become visible again.\n\n" +

                        "Account Enable/Disable & Visibility\n" +
                        "--------------------\n" +
                        "1. Users can enable or disable their account anytime from settings.\n" +
                        "2. When the account is disabled, recipients cannot view the donor’s details.\n" +
                        "3. There is no time limit; users can enable their account whenever they wish.\n" +
                        "4. An enabled account is visible to recipients looking for donors.\n\n" +

                        "Account Deletion\n" +
                        "--------------------\n" +
                        "1. Users may request account deletion via the app settings or by contacting support.\n" +
                        "2. Upon deletion, all personal data will be permanently removed from our servers.\n" +
                        "3. Deactivated accounts can be reactivated anytime by enabling them in settings.\n\n" +

                        "Protect the Community: Report Fake Donors\n"+
                        "----------------------\n"+
                        "1. Report fake donors if they demand money or misuse emergencies.\n" +
                        "2. Contact **blooddonorsearchservice@gmail.com** to report such cases. \n " +
                        "3. Their account will be permanently hidden, and their data will no longer be visible to recipients.\n\n"+

                        "By using this app and registering as a donor, you acknowledge and agree to these terms.";

        return termsAndPolicies;
    }
}