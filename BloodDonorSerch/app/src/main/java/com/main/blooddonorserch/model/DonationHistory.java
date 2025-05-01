package com.main.blooddonorserch.model;

import java.io.Serializable;

public class DonationHistory implements Serializable {
    private String recipientName;
    private String bloodQuantity;
    private String donationDate;
    private String donationType;
    private String location;

    public DonationHistory() {
        // Default constructor required for Firestore
    }

    public DonationHistory(String recipientName, String bloodQuantity, String donationDate, String donationType, String location) {
        this.recipientName = recipientName;
        this.bloodQuantity = bloodQuantity;
        this.donationDate = donationDate;
        this.donationType = donationType;
        this.location = location;
    }

    // Getters and Setters
    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getBloodQuantity() {
        return bloodQuantity;
    }

    public void setBloodQuantity(String bloodQuantity) {
        this.bloodQuantity = bloodQuantity;
    }

    public String getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(String donationDate) {
        this.donationDate = donationDate;
    }

    public String getDonationType() {
        return donationType;
    }

    public void setDonationType(String donationType) {
        this.donationType = donationType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
