package com.main.blooddonorserch.model;

import java.util.List;

public class Donor {
    private String id;
    private String donor_name;
    private String donor_city;
    private String donor_state;
    private String donor_country;
    private String donor_email;
    private String donor_mobile_number;
    private String donor_blood_group;
    private String donor_last_blood_donation_date;

    private String donor_dob;

    private String age;

    private boolean donor_status = true;

    private String donation_count;

    private String blood_donation_type;

    private String gender;

    private String donor_weight;

    private String isBlocked;

    private String recipient_name;

    private String blood_quantity;

    private String recipient_location;

    private List<DonationHistory> donationHistory;


    public Donor(String id, String donor_name, String donor_city, String donor_state, String donor_country, String donor_email, String donor_mobile_number, String donor_blood_group, String donor_last_blood_donation_date, String blood_donation_type,String donor_dob, String age,String donation_count,String donor_weight,String gender,String recipient_name,String blood_quantity,String recipient_location) {
        this.id = id;
        this.donor_name = donor_name;
        this.donor_city = donor_city;
        this.donor_state = donor_state;
        this.donor_country = donor_country;
        this.donor_email = donor_email;
        this.donor_mobile_number = donor_mobile_number;
        this.donor_blood_group = donor_blood_group;
        this.donor_last_blood_donation_date = donor_last_blood_donation_date;
        this.blood_donation_type = blood_donation_type;
        this.donor_dob = donor_dob;
        this.age = age;
        this.donation_count = donation_count;
        this.donor_weight = donor_weight;
        this.gender = gender;
        this.isBlocked = "false";
        this.recipient_name = recipient_name;
        this.blood_quantity = blood_quantity;
        this.recipient_location = recipient_location;
    }

    // Constructor WITH donationHistory (for Fetching Data in History Fragment)
    public Donor(String id, String donor_name, String donor_city, String donor_state, String donor_country,
                 String donor_email, String donor_mobile_number, String donor_blood_group,
                 String donor_last_blood_donation_date, String donor_dob, String age,
                 String donation_count, String blood_donation_type, String gender,
                 String donor_weight, String isBlocked, List<DonationHistory> donationHistory) {
        this.id = id;
        this.donor_name = donor_name;
        this.donor_city = donor_city;
        this.donor_state = donor_state;
        this.donor_country = donor_country;
        this.donor_email = donor_email;
        this.donor_mobile_number = donor_mobile_number;
        this.donor_blood_group = donor_blood_group;
        this.donor_last_blood_donation_date = donor_last_blood_donation_date;
        this.donor_dob = donor_dob;
        this.age = age;
        this.donation_count = donation_count;
        this.blood_donation_type = blood_donation_type;
        this.gender = gender;
        this.donor_weight = donor_weight;
        this.isBlocked = isBlocked;
        this.donationHistory = donationHistory;
    }

    public Donor() {
    }

    public Donor(String id, String donor_name, String donor_city, String donor_state, String donor_country, String donor_email, String donor_mobile_number, String donor_blood_group, String donor_last_blood_donation_date) {
        this.id = id;
        this.donor_name = donor_name;
        this.donor_city = donor_city;
        this.donor_state = donor_state;
        this.donor_country = donor_country;
        this.donor_email = donor_email;
        this.donor_mobile_number = donor_mobile_number;
        this.donor_blood_group = donor_blood_group;
        this.donor_last_blood_donation_date = donor_last_blood_donation_date;
    }
    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDonor_name() {
        return donor_name;
    }

    public void setDonor_name(String donor_name) {
        this.donor_name = donor_name;
    }

    public String getDonor_city() {
        return donor_city;
    }

    public void setDonor_city(String donor_city) {
        this.donor_city = donor_city;
    }

    public String getDonor_state() {
        return donor_state;
    }

    public void setDonor_state(String donor_state) {
        this.donor_state = donor_state;
    }

    public String getDonor_country() {
        return donor_country;
    }

    public void setDonor_country(String donor_country) {
        this.donor_country = donor_country;
    }

    public String getDonor_email() {
        return donor_email;
    }

    public void setDonor_email(String donor_email) {
        this.donor_email = donor_email;
    }

    public String getDonor_mobile_number() {
        return donor_mobile_number;
    }

    public void setDonor_mobile_number(String donor_mobile_number) {
        this.donor_mobile_number = donor_mobile_number;
    }

    public String getDonor_blood_group() {
        return donor_blood_group;
    }

    public void setDonor_blood_group(String donor_blood_group) {
        this.donor_blood_group = donor_blood_group;
    }

    public String getDonor_last_blood_donation_date() {
        return donor_last_blood_donation_date;
    }

    public void setDonor_last_blood_donation_date(String donor_last_blood_donation_date) {
        this.donor_last_blood_donation_date = donor_last_blood_donation_date;
    }

    public String getDonor_dob() {
        return donor_dob;
    }

    public void setDonor_dob(String donor_dob) {
        this.donor_dob = donor_dob;
    }

    public String getDonor_Age() {
        return age;
    }

    public void setDonor_Age(String age) {
        this.age = age;
    }

    public boolean getDonor_status() {
        return donor_status;
    }

    public void setDonor_status(boolean donor_status) {
        this.donor_status = donor_status;
    }

    public String getDonation_count() {
        return donation_count;
    }

    public void setDonation_count(String donation_count) {
        this.donation_count = donation_count;
    }

    public String getBlood_donation_type() {
        return blood_donation_type;
    }

    public void setBlood_donation_type(String blood_donation_type) {
        this.blood_donation_type = blood_donation_type;
    }

    public String getDonor_weight() {
        return donor_weight;
    }

    public void setDonor_weight(String donor_weight) {
        this.donor_weight = donor_weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getBlood_quantity() {
        return blood_quantity;
    }

    public void setBlood_quantity(String blood_quantity) {
        this.blood_quantity = blood_quantity;
    }

    public String getRecipient_location() {
        return recipient_location;
    }

    public void setRecipient_location(String recipient_location) {
        this.recipient_location = recipient_location;
    }

    public List<DonationHistory> getDonationHistory() {
        return donationHistory;
    }

    public void setDonationHistory(List<DonationHistory> donationHistory) {
        this.donationHistory = donationHistory;
    }
}
