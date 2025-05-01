package com.main.blooddonorserch.Repository;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.main.blooddonorserch.MainActivity;
import com.main.blooddonorserch.model.DonationHistory;
import com.main.blooddonorserch.model.Donor;
import com.main.blooddonorserch.R;
import com.main.blooddonorserch.view.loginscreen.LoginScreen;
import com.main.blooddonorserch.view.signupwithgoogle.activity_user_details;
import com.main.blooddonorserch.viewmodel.AuthViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthRepository {

    private GoogleSignInClient googleSignInClient;
    private MutableLiveData<List<Donor>> donorsLiveData = new MutableLiveData<>();

    //private final CollectionReference donorCollection;


    private static final String TAG = "AuthRepository";
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private final MutableLiveData<Boolean> authSuccessLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AuthRepository(Application application) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();

        String webClientId = application.getApplicationContext().getString(R.string.default_web_client_id);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)  // Correct way to get Web Client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(application, gso);
    }

    /***************************************************** Sign Up Logic ************************************/
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser donor = auth.getCurrentUser();
                        if (donor != null) {
                            checkUserExists(donor);
                        }
                    } else {
                        Log.e(TAG, "Google Authentication Failed", task.getException());
                        errorLiveData.postValue("Google Authentication Failed");
                        authSuccessLiveData.postValue(false);
                    }
                });
    }

    private void checkUserExists(FirebaseUser donor) {
       // Log.d(TAG, "Checking if user exists with email: " + donor.getEmail());

        database.collection("donors")
                .document(donor.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                        if(documentSnapshot.exists()) {
                            // Check if the document actually contains valid user details
                            Map<String, Object> userData = documentSnapshot.getData();
                            if (userData != null && userData.containsKey("donor_email") && userData.containsKey("donor_name")) {
                               // Log.d(TAG, "User already exists in Firestore.");
                                errorLiveData.postValue("User already exists. Please log in.");
                                authSuccessLiveData.postValue(false);
                                FirebaseAuth.getInstance().signOut();
                            } else {
                                // Allow signup if no valid data is found
                               // Log.d(TAG, "No valid user data found, allowing signup.");
                                authSuccessLiveData.postValue(true);
                            }
                        }
                        else {
                           // Log.d(TAG, "User does NOT exist. Allow signup.");
                            authSuccessLiveData.postValue(true);
                        }

                })
                .addOnFailureListener(e -> {
                    //Log.e(TAG, "Error checking user in Firestore", e);
                    errorLiveData.postValue("Database error. Try again.");
                    authSuccessLiveData.postValue(false);
                });
    }


public LiveData<Boolean> saveUserDetails(Donor donor) {
    MutableLiveData<Boolean> result = new MutableLiveData<>();
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    DocumentReference donorRef = database.collection("donors").document(donor.getId());

    Map<String, Object> donorMap = new HashMap<>();
    donorMap.put("id", donor.getId());
    donorMap.put("donor_name", donor.getDonor_name());
    donorMap.put("donor_city", donor.getDonor_city());
    donorMap.put("donor_state", donor.getDonor_state());
    donorMap.put("donor_country", donor.getDonor_country());
    donorMap.put("donor_email", donor.getDonor_email());
    donorMap.put("donor_mobile_number", donor.getDonor_mobile_number());
    donorMap.put("donor_blood_group", donor.getDonor_blood_group());
    donorMap.put("donor_last_blood_donation_date", donor.getDonor_last_blood_donation_date());
    donorMap.put("donor_dob", donor.getDonor_dob());
    donorMap.put("age", donor.getDonor_Age());
    donorMap.put("donor_status", donor.getDonor_status());
    donorMap.put("donation_count", donor.getDonation_count());
    donorMap.put("blood_donation_type", donor.getBlood_donation_type());
    donorMap.put("gender", donor.getGender());
    donorMap.put("isBlocked", donor.getIsBlocked());

    // 🆕 Create a new history entry
    Map<String, Object> donationEntry = new HashMap<>();
    donationEntry.put("donationType", donor.getBlood_donation_type());
    donationEntry.put("donationDate", donor.getDonor_last_blood_donation_date());
    donationEntry.put("recipientName", donor.getRecipient_name());
    donationEntry.put("bloodQuantity", donor.getBlood_quantity());
    donationEntry.put("location", donor.getRecipient_location());

    // 🔹 Step 1: Merge donor details (create if not exists)
    donorRef.set(donorMap, SetOptions.merge())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 🔹 Step 2: Append to donation history array
                    donorRef.update("donationHistory", FieldValue.arrayUnion(donationEntry))
                            .addOnSuccessListener(aVoid -> result.setValue(true))
                            .addOnFailureListener(e -> result.setValue(false));
                } else {
                    result.setValue(false);
                }
            });

    return result;
}

    public LiveData<Boolean> checkMobileNumberExists(String mobileNumber) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        database.collection("donors")
                .whereEqualTo("donor_mobile_number", mobileNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        result.postValue(true); // ✅ Mobile number exists
                    } else {
                        result.postValue(false); // ✅ Mobile number does NOT exist
                    }
                })
                .addOnFailureListener(e -> {
                    result.postValue(false); // Assume number does NOT exist on failure
                });
        return result;
    }


    public MutableLiveData<Boolean> getAuthSuccessLiveData() {
        return authSuccessLiveData;
    }

    public MutableLiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    /*****************************************************************************************/

    /*******************************************fetch data of each input*************************/

    private  final MutableLiveData<String> donorisBlocked = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorisBlocked()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String isBlocked = documentSnapshot.getString("isBlocked");
                        donorisBlocked.postValue(isBlocked);
                    }
                    else {
                       // Log.d("Firestore", "No such document");
                        donorisBlocked.postValue(null);
                    }
                }
                )
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor blood group", e);
                    donorisBlocked.postValue(null);
                });

        return donorisBlocked;
    }

    private final MutableLiveData<String> donorNameLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorName() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String donorName = documentSnapshot.getString("donor_name");
                        donorNameLiveData.postValue(donorName); // Use postValue for background thread updates
                    } else {
                        //Log.d("Firestore", "No such document");
                        donorNameLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor name", e);
                    donorNameLiveData.postValue(null); // Handle errors
                });

        return donorNameLiveData; // Returning LiveData that will update when Firestore fetches data
    }


    private final MutableLiveData<String> donorCityLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorCity() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String donorCity = documentSnapshot.getString("donor_city");
                        donorCityLiveData.postValue(donorCity); // Use postValue for background thread safety
                    } else {
                      //  Log.d("Firestore", "No such document");
                        donorCityLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor city", e);
                    donorCityLiveData.postValue(null); // Handle failure case
                });

        return donorCityLiveData; // Returns LiveData that updates when Firestore fetches data
    }


    private final MutableLiveData<String> donorStateLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorState() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String donorState = documentSnapshot.getString("donor_state");
                        donorStateLiveData.postValue(donorState); // Use postValue for safe UI updates
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorStateLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor state", e);
                    donorStateLiveData.postValue(null); // Handle failure scenario
                });

        return donorStateLiveData; // Ensures UI observes the updated value when Firestore completes fetch
    }


    private final MutableLiveData<String> donorCountryLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorCountry() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String donorCountry = documentSnapshot.getString("donor_country");
                        donorCountryLiveData.postValue(donorCountry); // Use postValue for thread safety
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorCountryLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor country", e);
                    donorCountryLiveData.postValue(null); // Handle failure scenario
                });

        return donorCountryLiveData; // UI observes updated value after Firestore fetch completes
    }


    private final MutableLiveData<String> donorEmailLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorEmail() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String donorEmail = documentSnapshot.getString("donor_email");
                        donorEmailLiveData.postValue(donorEmail); // Use postValue for thread safety
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorEmailLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor email", e);
                    donorEmailLiveData.postValue(null); // Handle failure scenario
                });

        return donorEmailLiveData; // UI observes updated value after Firestore fetch completes
    }


    private final MutableLiveData<String> donorMobileNumberLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorMobileNumber() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String mobileNumber = documentSnapshot.getString("donor_mobile_number");
                        donorMobileNumberLiveData.postValue(mobileNumber); // Use postValue for thread safety
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorMobileNumberLiveData.postValue(null); // Set null if document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor mobile number", e);
                    donorMobileNumberLiveData.postValue(null);
                });

        return donorMobileNumberLiveData;
    }

    private final MutableLiveData<String> donorGenderLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseGenderType()
    {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        String gender = documentSnapshot.getString("gender");
                        donorGenderLiveData.postValue(gender);
                    }
                    else {
                       // Log.d("Firestore", "No such document");
                        donorGenderLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor gender", e);
                    donorGenderLiveData.postValue(null);
                });
        return donorGenderLiveData;
    }

// ===============================

    private final MutableLiveData<String> donorBloodGroupLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorBloodGroup() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e("Firestore", "User not logged in or null");
            donorBloodGroupLiveData.postValue("Not Available");
            return donorBloodGroupLiveData;
        }

        String userId = user.getUid();
        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String bloodGroup = documentSnapshot.getString("donor_blood_group");
                        donorBloodGroupLiveData.postValue(bloodGroup);
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorBloodGroupLiveData.postValue("Not Available");
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor blood group", e);
                    donorBloodGroupLiveData.postValue("Not Available");
                });

        return donorBloodGroupLiveData;
    }

    private final MutableLiveData<String> donorLastBloodDonationDateLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorLastBloodDonationDate() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e("Firestore", "User not logged in or null");
            donorLastBloodDonationDateLiveData.setValue("Not Available");
            return donorLastBloodDonationDateLiveData;
        }

        String userId = user.getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String lastDonationDate = documentSnapshot.getString("donor_last_blood_donation_date");
                        if (lastDonationDate != null) {
                            donorLastBloodDonationDateLiveData.setValue(lastDonationDate);
                            //Log.d("Firestore", "Fetched Last Blood Donation Date: " + lastDonationDate);
                        } else {
                            Log.w("Firestore", "Donation date is null");
                            donorLastBloodDonationDateLiveData.setValue("Not Available");
                        }
                    } else {
                        //Log.w("Firestore", "No such document exists for user: " + userId);
                        donorLastBloodDonationDateLiveData.setValue("Not Available");
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor last blood donation date", e);
                    donorLastBloodDonationDateLiveData.setValue("Not Available");
                });

        return donorLastBloodDonationDateLiveData;
    }


    // Fetch Donor DOB
    private final MutableLiveData<String> donorDobLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorDob() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        donorDobLiveData.postValue(documentSnapshot.getString("donor_dob"));
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorDobLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor DOB", e);
                    donorDobLiveData.postValue(null);
                });

        return donorDobLiveData;
    }

    // Fetch Donor Age
    private final MutableLiveData<String> donorAgeLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorAge() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        donorAgeLiveData.postValue(documentSnapshot.getString("age"));
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorAgeLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor age", e);
                    donorAgeLiveData.postValue(null);
                });

        return donorAgeLiveData;
    }

    // Fetch Donor Status (Boolean)
    private final MutableLiveData<Boolean> donorStatusLiveData = new MutableLiveData<>();

    public LiveData<Boolean> getFirebaseDonorStatus() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean status = documentSnapshot.getBoolean("donor_status");
                        donorStatusLiveData.postValue(status != null ? status : false);
                    } else {
                       // Log.d("Firestore", "No such document");
                        donorStatusLiveData.postValue(false);
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor status", e);
                    donorStatusLiveData.postValue(false);
                });

        return donorStatusLiveData;
    }

    // Fetch Donor Blood Donation Type
    private final MutableLiveData<String> donorBloodDonationTypeLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorBloodDonationType() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        donorBloodDonationTypeLiveData.postValue(documentSnapshot.getString("blood_donation_type"));
                    } else {
                        //Log.d("Firestore", "No such document");
                        donorBloodDonationTypeLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                   // Log.e("Firestore", "Error fetching donor blood donation type", e);
                    donorBloodDonationTypeLiveData.postValue(null);
                });

        return donorBloodDonationTypeLiveData;
    }

    // Fetch Donor Blood Donation Count
    private final MutableLiveData<String> donorBloodDonationCountLiveData = new MutableLiveData<>();

    public LiveData<String> getFirebaseDonorBloodDonationCount() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        database.collection("donors").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        donorBloodDonationCountLiveData.postValue(documentSnapshot.getString("donation_count"));
                    } else {
                        //Log.d("Firestore", "No such document");
                        donorBloodDonationCountLiveData.postValue("0"); // Default value
                    }
                })
                .addOnFailureListener(e -> {
                    //Log.e("Firestore", "Error fetching donor blood donation count", e);
                    donorBloodDonationCountLiveData.postValue("0");
                });

        return donorBloodDonationCountLiveData;
    }

    // Function to fetch donation history
    public LiveData<List<DonationHistory>> getDonationHistory(String userId) {
        MutableLiveData<List<DonationHistory>> donationHistoryLiveData  = new MutableLiveData<>();

        database.collection("donors").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Donor donor = documentSnapshot.toObject(Donor.class);
                        if (donor != null && donor.getDonationHistory() != null) {
                            donationHistoryLiveData.setValue(donor.getDonationHistory());
                        } else {
                            donationHistoryLiveData.setValue(new ArrayList<>()); // Empty list if no data
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    donationHistoryLiveData.setValue(new ArrayList<>()); // Empty list on failure
                });

        return donationHistoryLiveData;
    }

    /*******************************************fetch data of each input*************************/

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }


    public LiveData<List<Donor>> getAllDonors() {

        database.collection("donors").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.e("Firestore", "Error fetching donors: " + error.getMessage(), error);
                return; // Do nothing if error occurs
            }

            if (value != null && !value.isEmpty()) {
                List<Donor> donorTableArrayList = new ArrayList<>();
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    Donor donor = snapshot.toObject(Donor.class);
                    if (donor != null) {
                        donorTableArrayList.add(donor);
                    }
                }
               // Log.d("Firestore", "Total donors fetched: " + donorTableArrayList.size());
                donorsLiveData.setValue(donorTableArrayList); // Update only if data exists
            } else {
               // Log.d("Firestore", "No donors found in Firestore. Keeping LiveData unchanged.");
                donorsLiveData.setValue(new ArrayList<>()); // Ensure UI knows there are no donors
            }
        });

        return donorsLiveData;
    }

    public void signOut(OnSignOutCompleteListener listener) {
        auth.signOut(); // Sign out from Firebase
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSignOutSuccess();
            } else {
                listener.onSignOutFailure(task.getException() != null ? task.getException().getMessage() : "Sign out failed");
            }
        });
    }



    /***********************************************Settings *************************************/

    public void updateName(String id, String name,Context context) {

        if (id != null) {
            database.collection("donors")
                    .document(id)
                    .update("donor_name", name)
                    .addOnSuccessListener(aVoid -> {
                        //Log.d("AuthRepository", "Name updated: " + name);
                        Toast.makeText(context, "Name Updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating name: " + e.getMessage());
                        Toast.makeText(context, "Error Updating Name", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateGender(String id, String gender,Context context) {
        if (id != null) {
            database.collection("donors")
                    .document(id)
                    .update("gender", gender)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "Gender updated: " + gender);
                        Toast.makeText(context, "Gender Updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating gender: " + e.getMessage());
                        Toast.makeText(context, "Error Updating Gender", Toast.LENGTH_SHORT).show();
                    });
        }
    }
    public void updateCity(String id, String city,Context context) {
        if (id != null) {
            database.collection("donors")
                    .document(id)
                    .update("donor_city", city)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "City updated: " + city);
                        Toast.makeText(context, "City Updated", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                       // Log.e("AuthRepository", "Error updating city: " + e.getMessage());
                        Toast.makeText(context, "Error Updating City", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateState(String id, String state,Context context)
    {
        if (id != null)
        {
            database.collection("donors")
                    .document(id)
                    .update("donor_state", state)
                    .addOnSuccessListener(aVoid -> {
                        //Log.d("AuthRepository", "State updated: " + state);
                        Toast.makeText(context, "State Updated", Toast.LENGTH_SHORT).show();
                        })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating state: " + e.getMessage());
                        Toast.makeText(context, "Error Updating State", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateCountry(String id,String country,Context context) {
        if (id != null) {
            database.collection("donors")
                    .document(id)
                    .update("donor_country", country)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "Country Updated: " + country);
                        Toast.makeText(context,"Country Updated",Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating country: " + e.getMessage());
                        Toast.makeText(context,"Error Updating Country",Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateMobileNumber(String id,String phonenumber,Context context)
    {
        if(id!=null)
        {
            database.collection("donors")
                    .document(id)
                    .update("donor_mobile_number", phonenumber)
                    .addOnSuccessListener(aVoid -> {
                        //Log.d("AuthRepository", "Mobile Number updated: " + phonenumber);
                        Toast.makeText(context,"Mobile Number Updated",Toast.LENGTH_SHORT).show();
                        })
                    .addOnFailureListener(e -> {
                       // Log.e("AuthRepository", "Error updating mobile number: " + e.getMessage());
                        Toast.makeText(context,"Error Updating Mobile Number",Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateBloodGroup(String id,String bloodGroup,Context context)
    {

        if(id!=null)
        {
            database.collection("donors")
                    .document(id)
                    .update("donor_blood_group", bloodGroup)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "Blood Group updated: " + bloodGroup);
                        Toast.makeText(context,"Blood Group Updated",Toast.LENGTH_SHORT).show();

                    })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating blood group: " + e.getMessage());
                        Toast.makeText(context,"Error Updating Blood Group",Toast.LENGTH_SHORT).show();
                    });
        }
    }
    public void updateDOB(String id,String dob,int age,Context context) {
        if(id!=null)
        {
            database.collection("donors")
                    .document(id)
                    .update("donor_dob", dob,"age",age)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "DOB updated: " + dob);
                        Toast.makeText(context,"DOB Updated",Toast.LENGTH_SHORT).show();
                        })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error updating DOB: " + e.getMessage());
                        Toast.makeText(context,"Error Updating DOB",Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void updateLastBloodDonationDate(String id, String lastBloodDonationDate, String bloodDonationType,
                                            String recipientName, String donationQuantity, String recipientLocation,
                                            String count, Context context) {
        if (id != null) {
            DocumentReference donorRef = database.collection("donors").document(id);

            // 🔹 Step 1: Update donor's profile details
            Map<String, Object> profileUpdates = new HashMap<>();
            profileUpdates.put("donor_last_blood_donation_date", lastBloodDonationDate);
            profileUpdates.put("blood_donation_type", bloodDonationType);
            profileUpdates.put("donation_count", count);

            // 🔹 Step 2: Create a new history entry
            Map<String, Object> donationEntry = new HashMap<>();
            donationEntry.put("donationDate", lastBloodDonationDate);
            donationEntry.put("donationType", bloodDonationType);
            donationEntry.put("recipientName", recipientName);
            donationEntry.put("bloodQuantity", donationQuantity);
            donationEntry.put("location", recipientLocation);

            // 🔹 Step 3: Perform both updates (Profile & History)
            donorRef.update(profileUpdates)
                    .continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            // Append to donation history
                            return donorRef.update("donationHistory", FieldValue.arrayUnion(donationEntry));
                        } else {
                            throw task.getException();
                        }
                    })
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "Profile updated & history added");
                        Toast.makeText(context, "Last Blood Donation Updated & Saved in History", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                       // Log.e("AuthRepository", "Error updating data: " + e.getMessage());
                        Toast.makeText(context, "Error Updating Last Blood Donation Date", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    public void DeleteAccount(String id, Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (id != null && user != null) {
            reAuthenticateGoogleUser(user, context, () -> deleteUserFromFirebase(id, user, context));
        } else {
            Log.e("DeleteAccount", "User ID is null or user is not logged in, cannot delete.");
        }
    }
    private void reAuthenticateGoogleUser(FirebaseUser user, Context context, Runnable onSuccess) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        if (account != null) {
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            user.reauthenticate(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Log.d("ReAuth", "Google re-authentication successful.");
                            onSuccess.run(); // Proceed with account deletion
                        } else {
                            //Log.e("ReAuth", "Google re-authentication failed", task.getException());
                            Toast.makeText(context, "Re-authentication failed. Please sign in again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Google Sign-In required. Please sign in again.", Toast.LENGTH_SHORT).show();
            //Log.e("ReAuth", "No Google account found. User must sign in again.");
        }
    }
    private void deleteUserFromFirebase(String id, FirebaseUser user, Context context) {
        // Delete donor details from Firestore first
        FirebaseFirestore.getInstance().collection("donors")
                .document(id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    //Log.d("AuthRepository", "Account deleted from Firestore: " + id);

                    // Now delete the user from Firebase Authentication
                    user.delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    //Log.d("DeleteUser", "User deleted from Firebase Authentication.");
                                    FirebaseAuth.getInstance().signOut(); // Sign out after deletion
                                    Toast.makeText(context, "Account Deleted", Toast.LENGTH_SHORT).show();

                                    // Navigate to MainActivity
                                    Intent intent = new Intent(context, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(intent);
                                } else {
                                    //Log.e("DeleteUser", "Failed to delete user", task.getException());
                                    Toast.makeText(context, "Failed to delete account. Try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .addOnFailureListener(e -> Log.e("AuthRepository", "Error deleting Firestore account: " + e.getMessage()));
    }


    public void EnableAccount(String id,Context context)
    {
        if (id!=null) {
            database.collection("donors")
                    .document(id)
                    .update("donor_status", true)
                    .addOnSuccessListener(aVoid -> {
                       // Log.d("AuthRepository", "Account enabled: " + id);
                        Toast.makeText(context,"Account Enabled",Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                       // Log.e("AuthRepository", "Error enabling account: " + e.getMessage());
                        Toast.makeText(context,"Error Enabling Account",Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public void DisableAccount(String id,Context context)
    {
        if (id!=null) {

            database.collection("donors")
                    .document(id)
                    .update("donor_status", false)
                    .addOnSuccessListener(aVoid -> {
                        //Log.d("AuthRepository", "Account disabled: " + id);
                        Toast.makeText(context,"Account Disabled",Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        //Log.e("AuthRepository", "Error disabling account: " + e.getMessage());
                        Toast.makeText(context,"Error Disabling Account",Toast.LENGTH_SHORT).show();
                        });
        }
    }
    /***********************************************Settings *************************************/
    public void signOutAndRevokeAccess(OnSignOutCompleteListener listener) {
        auth.signOut(); // Sign out from Firebase
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listener.onSignOutSuccess();
            } else {
                listener.onSignOutFailure(task.getException() != null ? task.getException().getMessage() : "Revoke access failed");
            }
        });
    }

    public interface OnSignOutCompleteListener {
        void onSignOutSuccess();
        void onSignOutFailure(String errorMessage);
    }

}
