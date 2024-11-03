package com.example.petadoption;

public class AdoptionRequest {
    private String requestId;
    private String petId;
    private String Id;
    private String name;
    private String message;
    private String petPhoto; // Add this if you have a URL or resource for the pet's photo

    // Default constructor required for calls to DataSnapshot.getValue(AdoptionRequest.class)
    public AdoptionRequest() {}

    public AdoptionRequest(String petId, String fromID, String name, String message) {
        this.petId = petId;
        this.Id = fromID;
        this.name = name;
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    public String getPetId() { return petId; }
    public String getOwnerId() { return Id; }
    public String getName() { return name; }
    public String getMessage() { return message; }
    public String getPetPhoto() { return petPhoto; }
}

