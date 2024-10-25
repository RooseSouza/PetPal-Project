package com.example.petadoption;

public class Pet {
    private String name;
    private int pfp;
    private int petImage;

    public Pet(String name, int pfp, int petImage) {
        this.name = name;
        this.pfp = pfp;
        this.petImage = petImage;
    }

    public String getName() {
        return name;
    }

    public int getPfp() {
        return pfp;
    }

    public int getPetImage() {
        return petImage;
    }
}
