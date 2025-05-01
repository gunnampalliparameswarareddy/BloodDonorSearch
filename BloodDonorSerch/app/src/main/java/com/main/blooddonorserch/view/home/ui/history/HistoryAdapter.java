package com.main.blooddonorserch.view.home.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.blooddonorserch.R;
import com.main.blooddonorserch.model.DonationHistory;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<DonationHistory> donationHistoryList;

    public HistoryAdapter(List<DonationHistory> donationHistoryList) {
        this.donationHistoryList = donationHistoryList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        DonationHistory donationHistory = donationHistoryList.get(position);

        holder.recipientName.setText(donationHistory.getRecipientName());
        holder.bloodQuantity.setText(donationHistory.getBloodQuantity() + " ml");
        holder.recipientLocation.setText(donationHistory.getLocation());
        holder.donationDate.setText(donationHistory.getDonationDate());
        holder.donationType.setText(donationHistory.getDonationType());
    }

    @Override
    public int getItemCount() {
        return donationHistoryList.size();
    }

    public void updateData(List<DonationHistory> newHistoryList) {
        this.donationHistoryList = newHistoryList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView recipientName, bloodQuantity, recipientLocation, donationDate, donationType;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            recipientName = itemView.findViewById(R.id.recipient_name);
            bloodQuantity = itemView.findViewById(R.id.recepient_donataion_quantity);
            recipientLocation = itemView.findViewById(R.id.recipient_location);
            donationDate = itemView.findViewById(R.id.donated_date);
            donationType = itemView.findViewById(R.id.donated_type);
        }
    }
}
