package com.example.reviewapp.reviewapp.models;

import java.util.ArrayList;

public class UserModel {
    public String phoneNumber;
    public String gender;
    public String birthDate;
    public double ratings;
    public ArrayList<LocationModel> locations;

    public ArrayList<LocationModel> getLocations() {
        if(locations == null){
            locations = new ArrayList<>();
        }
        return locations;
    }

    public void setLocations(ArrayList<LocationModel> locations) {
        this.locations = locations;
    }

    public UserModel() {

        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public double getRatings() {
        return ratings;
    }

    public void setRatings(double ratings) {
        this.ratings = ratings;
    }


    public UserModel(String phoneNumber, String gender, String birthDate, double ratings,ArrayList<LocationModel> locations) {
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.locations = locations;
        this.ratings = ratings;
    }
}
