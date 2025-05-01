package com.main.blooddonorserch.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.blooddonorserch.Repository.AuthRepository;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.main.blooddonorserch.model.DonationHistory;
import com.main.blooddonorserch.model.Donor;

import java.util.ArrayList;
import java.util.List;

import io.grpc.internal.SharedResourceHolder;

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = "AuthViewModel";

    private MutableLiveData<FirebaseUser> signInResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> userSaved = new MutableLiveData<>();

    private LiveData<List<Donor>> allDonors;
    private MutableLiveData<SharedResourceHolder.Resource<FirebaseUser>> authResult;

    private AuthRepository repository;

    private final LiveData<String> errorLiveData;

    private final MutableLiveData<Boolean> authSuccessLiveData;


    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);

        authSuccessLiveData = repository.getAuthSuccessLiveData();

        errorLiveData = repository.getErrorLiveData();



        authResult = new MutableLiveData<>();

        allDonors = repository.getAllDonors();

       // Log.d(TAG, "AuthViewModel initialized");
    }
    /************************************** Sign Up Logic **************************/

    public void authenticateWithGoogle(GoogleSignInAccount account) {
        repository.firebaseAuthWithGoogle(account);
    }
    public LiveData<Boolean> checkIfMobileExists(String mobileNumber) {
        return repository.checkMobileNumberExists(mobileNumber);
    }

    public LiveData<Boolean> getAuthSuccessLiveData() {
        return authSuccessLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> saveUserDetails(Donor donor) {
        //Log.d(TAG, "Saving user details: " + donor.getId());
        return repository.saveUserDetails(donor);
    }

    /************************************** Sign Up Logic END **************************/
    /******************************Fetch Data from Firebase ***********************/
    public LiveData<String> getDonorisBlocked()
    {
        //Log.d("AuthViewModel", "Fetching donor blood group");
        return repository.getFirebaseDonorisBlocked();
    }

    public LiveData<String> getDonorName()
    {
        //Log.d("AuthViewModel", "Fetching donor name");
        return repository.getFirebaseDonorName();
    }
    public LiveData<String> getDonorCity()
    {
        //Log.d("AuthViewModel", "Fetching donor city");
        return repository.getFirebaseDonorCity();
    }
    public LiveData<String> getDonorState()
    {
        //Log.d("AuthViewModel", "Fetching donor state");
        return repository.getFirebaseDonorState();
    }
    public LiveData<String> getDonorCountry()
    {
        //Log.d("AuthViewModel", "Fetching donor country");
        return repository.getFirebaseDonorCountry();
    }
    public LiveData<String> getDonorEmail()
    {
        //Log.d("AuthViewModel", "Fetching donor email");
        return repository.getFirebaseDonorEmail();
    }
    public LiveData<String> getDonorMobileNumber()
    {
       // Log.d("AuthViewModel", "Fetching donor mobile number");
        return repository.getFirebaseDonorMobileNumber();
    }
    public LiveData<String> getDonorBloodGroup()
    {
        //Log.d("AuthViewModel", "Fetching donor blood group");
        return repository.getFirebaseDonorBloodGroup();
    }
    public LiveData<String> getDonorDob()
    {
        //Log.d("AuthViewModel", "Fetching donor dob");
        return repository.getFirebaseDonorDob();
    }
    public LiveData<String> getDonorAge()
    {
        //Log.d("AuthViewModel", "Fetching donor age");
        return repository.getFirebaseDonorAge();
    }
    public LiveData<Boolean> getDonorStatus()
    {
       // Log.d("AuthViewModel", "Fetching donor status");
        return repository.getFirebaseDonorStatus();
    }
    public LiveData<String> getDonorBloodDonationType()
    {
        //Log.d("AuthViewModel", "Fetching donor blood donation type");
        return repository.getFirebaseDonorBloodDonationType();
    }
    public LiveData<String> getDonorLastBloodDonationDate()
    {
       // Log.d("AuthViewModel", "Fetching donor last blood donation date");
        return repository.getFirebaseDonorLastBloodDonationDate();
    }

    public LiveData<String> getDonorLastBloodDonationCount()
    {
        //Log.d("AuthViewModel", "Fetching donor last blood donation count");
        return repository.getFirebaseDonorBloodDonationCount();
    }

    public LiveData<String> getDonorGender()
    {
        //Log.d("AuthViewModel", "Fetching donor gender");
        return repository.getFirebaseGenderType();
    }
    /******************************Fetch Data from Firebase ***********************/
    public LiveData<List<Donor>> getAllDonors() {
        return allDonors;
    }

    public LiveData<List<DonationHistory>> fetchDonationHistory(String userId) {
        return repository.getDonationHistory(userId);
    }


    public FirebaseUser getCurrentUser() {
        return repository.getCurrentUser();
    }

    public void SignUptWithGoogle(GoogleSignInAccount account) {

    }

    public LiveData<FirebaseUser> getSignInResult() {
        return signInResult;
    }

    public LiveData<Boolean> isUserSaved() {
        return userSaved;
    }



    /********************************Settings Screen****************************/

    public  void updateName(String id,String name,Context context) {
        repository.updateName(id,name,context);
        //Log.d("AuthViewModel", "Name updated: " + name);
    }

    public void updateGender(String id,String gender,Context context) {
        repository.updateGender(id,gender,context);
        //Log.d("AuthViewModel", "Gender updated: " + gender);
    }

    public void updateCity(String id,String city,Context context) {
        repository.updateCity(id,city,context);
       // Log.d("AuthViewModel", "City updated: " + city);
    }
    public void updateState(String id,String state,Context context) {
        repository.updateState(id,state,context);
       // Log.d("AuthViewModel", "State updated: " + state);
    }

    public void updateCountry(String id,String country,Context context) {
        repository.updateCountry(id,country,context);
       // Log.d("AuthViewModel", "Country updated: " + country);
    }
    public void updateMobileNumber(String id,String mobileNumber,Context context) {
        repository.updateMobileNumber(id,mobileNumber,context);
        //Log.d("AuthViewModel", "Mobile Number updated: " + mobileNumber);
    }

    public void updateBloodGroup(String id,String bloodGroup,Context context) {
        repository.updateBloodGroup(id,bloodGroup,context);
       // Log.d("AuthViewModel", "Blood Group updated: " + bloodGroup);
    }
    public void updateDOB(String id,String dob,int age,Context context) {
        repository.updateDOB(id,dob,age,context);
       // Log.d("AuthViewModel", "Blood Group updated: " + dob);
    }
    public void updateLastBloodDonationDate(String id,String lastBloodDonationDate,String bloodDonationType,String recepientname,String donation_quantity,String recepientlocation,String count,Context context) {
        repository.updateLastBloodDonationDate(id,lastBloodDonationDate,bloodDonationType,recepientname,donation_quantity,recepientlocation,count,context);
       // Log.d("AuthViewModel", "Last Blood Donation Date updated: " + lastBloodDonationDate);
    }
    public void DeleteAccount(String id, Context context) {
        repository.DeleteAccount(id,context);
       // Log.d("AuthViewModel", "Account deleted: " + id);
    }
    public void EnableAccount(String id,Context context) {
        repository.EnableAccount(id,context);
        //Log.d("AuthViewModel", "Account enabled: " + id);
    }
    public void DisableAccount(String id,Context context) {
        repository.DisableAccount(id,context);
       // Log.d("AuthViewModel", "Account disabled: " + id);
    }


    public void signOutAndRevokeAccess(AuthRepository.OnSignOutCompleteListener listener) {
        repository.signOutAndRevokeAccess(listener);
    }


    /********************************Settings Screen END ****************************/

}
