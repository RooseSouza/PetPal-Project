package com.example.petadoption;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private final MutableLiveData<List<Pet>> petList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Pet>> getPetList() {
        return petList;
    }

    public void setPetList(List<Pet> pets) {
        petList.setValue(pets);
    }
}

