package com.example.petadoption;

public class Pet {
    private String petName; // Change this to petName
    private String location;

    // Empty constructor required for Firebase
    public Pet() {}

    public Pet(String petName, String location) { // Update constructor parameter
        this.petName = petName; // Update assignment
        this.location = location;
    }

    // Getters and Setters
    public String getPetName() { return petName; } // Update getter method
    public void setPetName(String petName) { this.petName = petName; } // Update setter method

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}


