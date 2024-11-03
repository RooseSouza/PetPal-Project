package com.example.petadoption;

public class AdoptionRequest {
    private String ownerId;
    private String petId;
    private String name;
    private String message;

    public AdoptionRequest() {
        // Default constructor required for calls to DataSnapshot.getValue(AdoptionRequest.class)
    }

    public AdoptionRequest(String ownerId, String petId, String name, String message) {
        this.ownerId = ownerId;
        this.petId = petId;
        this.name = name;
        this.message = message;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getPetId() {
        return petId;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
