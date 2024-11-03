package com.example.petadoption;

public class Pet {
    private String petName;
    private String location;
    private String id;
    private String age;
    private String breed;
    private String size;
    private String gender;
    private String description;
    private String healthStatus;
    private boolean isStreet;
    private String rescueLocation;
    private String imageBase64; // New field for storing the image in Base64 format
    private String ownerProfileImageBase64;
    private String ownerId; // New field for storing the owner's ID

    // Empty constructor required for Firebase
    public Pet() {}

    public Pet(String petName, String location, String id, String age, String breed, String size,
               String gender, String description, String healthStatus, boolean isStreet, String rescueLocation,
               String imageBase64, String ownerId) { // Add ownerId parameter
        this.petName = petName;
        this.location = location;
        this.id = id;
        this.age = age;
        this.breed = breed;
        this.size = size;
        this.gender = gender;
        this.description = description;
        this.healthStatus = healthStatus;
        this.isStreet = isStreet;
        this.rescueLocation = rescueLocation;
        this.imageBase64 = imageBase64; // Initialize imageBase64
        this.ownerId = ownerId; // Initialize ownerId
    }

    // Getter and setter for imageBase64
    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    // Getter and setter for ownerProfileImageBase64
    public String getOwnerProfileImageBase64() { return ownerProfileImageBase64; }
    public void setOwnerProfileImageBase64(String ownerProfileImageBase64) {
        this.ownerProfileImageBase64 = ownerProfileImageBase64;
    }

    // Getter and setter for ownerId
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    // Getters and setters for other fields
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAge() { return age; }
    public void setAge(String age) { this.age = age; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }

    public boolean isStreet() { return isStreet; }
    public void setStreet(boolean street) { isStreet = street; }

    public String getRescueLocation() { return rescueLocation; }
    public void setRescueLocation(String rescueLocation) { this.rescueLocation = rescueLocation; }
}
