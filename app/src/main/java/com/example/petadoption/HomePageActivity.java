package com.example.petadoption;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomePageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Pet> petList;
    private PetAdapter petAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a list of pets
        petList = new ArrayList<>();
        petList.add(new Pet("Buddy", R.drawable.pfp1, R.drawable.pet1));
        petList.add(new Pet("Max", R.drawable.pfp2, R.drawable.pet2));
        // Add more pets as needed

        // Set up the adapter with the pet list
        petAdapter = new PetAdapter(petList);
        recyclerView.setAdapter(petAdapter);
    }
}
