package com.main.blooddonorserch.view.home.ui.home;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.main.blooddonorserch.R;

import com.main.blooddonorserch.databinding.DonorListViewBinding;
import com.main.blooddonorserch.model.Donor;

import java.util.ArrayList;



public  class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.DonorViewHolder> {

    //Creating an ArrayList of DonorTable
    private ArrayList<Donor> donorTableArrayList;

    public MyCustomAdapter(ArrayList<Donor> donorTableArrayList) {
        //Constructor to pass the donorTableArrayList
        this.donorTableArrayList = donorTableArrayList;
    }

    @NonNull
    @Override
    public DonorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Creating an object of DonorListViewBinding
        DonorListViewBinding donorListViewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                                                            R.layout.donor_list_view ,
                                                                            parent,
                                                                            false);
        //Returning an object of DonorViewHolder
        return new DonorViewHolder(donorListViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DonorViewHolder holder, int position) {
        //Getting the current donor from donorTableArrayList
        Donor currentdonor = donorTableArrayList.get(position);
        //Binding the current donor to donorListViewBinding
        holder.donorListViewBinding.setDonor(currentdonor);
        holder.donorListViewBinding.executePendingBindings();

        // Set click listener for the Share button
        holder.donorListViewBinding.shareButton.setOnClickListener(v -> shareDonorInfo(currentdonor, v));


    }

    @Override
    public int getItemCount() {
        if (donorTableArrayList != null) {
            //Returning the size of donorTableArrayList
            return donorTableArrayList.size();
        }
        return 0;
    }

    public void setDonor(ArrayList<Donor> donorTableArrayList)
    {
        this.donorTableArrayList = donorTableArrayList;
        notifyDataSetChanged();
    }

    private void shareDonorInfo(Donor donor, View view) {
        String shareText = "Blood Donor Details:\n" +
                "Name: " + donor.getDonor_name() + "\n" +
                "Blood Group: " + donor.getDonor_blood_group() + "\n" +
                "Mobile: " + donor.getDonor_mobile_number() + "\n" +
                "Location: " + donor.getDonor_city() + ", " + donor.getDonor_state() + ", " + donor.getDonor_country();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        // Show chooser to select app for sharing
        view.getContext().startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
    class DonorViewHolder extends RecyclerView.ViewHolder {
        private DonorListViewBinding donorListViewBinding;

        public DonorViewHolder(DonorListViewBinding donorListViewBinding) {
            super(donorListViewBinding.getRoot());
            this.donorListViewBinding = donorListViewBinding;
        }
    }
}
