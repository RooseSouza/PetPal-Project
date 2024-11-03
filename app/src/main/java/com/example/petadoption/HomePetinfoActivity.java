package com.example.petadoption;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomePetinfoActivity extends AppCompatActivity {
    private Button requestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_petinfo);
        requestButton = findViewById(R.id.adoptButton);

        // Retrieve pet ID from the intent
        String petId = getIntent().getStringExtra("petId");
        String ownerId = getIntent().getStringExtra("ownerID");

        getIntent().getExtras().getString("KEY");

        if (petId != null) {
            Log.d("HomePetinfoActivity", "Pet ID: " + petId);
            fetchPetDetails(petId, ownerId); // Fetch pet details using the pet ID
        } else {
            Log.e("HomePetinfoActivity", "No Pet ID received");
        }

        if (ownerId != null) {
            Log.d("HomePetinfoActivity", "Pet ID: " + petId + ", Owner ID: " + ownerId);
            fetchPetDetails(petId, ownerId); // Fetch pet details using the pet ID
        } else {
            Log.e("HomePetinfoActivity", "No Owner ID received" + ownerId);
        }
    }

    private void fetchPetDetails(String petId, String ownerId) {
        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("pets").child(ownerId).child(petId);
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Pet pet = dataSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        setPetData(pet); // Set the retrieved pet data to the UI
                    }
                } else {
                    Log.e("HomePetinfoActivity", "No pet found for the given ID");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HomePetinfoActivity", "Database error: " + databaseError.getMessage());
            }
        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent intent = new Intent(HomePetinfoActivity.this, PetAdoptionFormActivity.class);
            intent.putExtra("petId", petId);
            intent.putExtra("ownerId", ownerId);
            startActivity(intent);

            }
        });

    }

    private void setPetData(Pet pet) {
        // Find TextViews and ImageView
        TextView petNameTextView = findViewById(R.id.petNameTextView);
        TextView ageTextView = findViewById(R.id.ageTextView);
        TextView breedTextView = findViewById(R.id.breedTextView);
        TextView sizeTextView = findViewById(R.id.sizeTextView);
        TextView genderTextView = findViewById(R.id.genderTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView healthStatusTextView = findViewById(R.id.healthStatusTextView);
        TextView locationTextView = findViewById(R.id.locationTextView);
        TextView isStreetTextView = findViewById(R.id.isStreetTextView);
        TextView rescueLocationTextView = findViewById(R.id.rescueLocationTextView);
        ImageView petImageView = findViewById(R.id.petImageView);

        // Set the data
        petNameTextView.setText(pet.getPetName());
        ageTextView.setText(String.valueOf(pet.getAge()));
        breedTextView.setText(pet.getBreed());
        sizeTextView.setText(pet.getSize());
        genderTextView.setText(pet.getGender());
        descriptionTextView.setText(pet.getDescription());
        healthStatusTextView.setText(pet.getHealthStatus());
        locationTextView.setText(pet.getLocation());
        isStreetTextView.setText(pet.isStreet() ? "Yes" : "No");
        rescueLocationTextView.setText(pet.getRescueLocation());

        // Load the image from Base64
        String base64Image = pet.getImageBase64(); // Assuming this is how you retrieve the Base64 string
        Bitmap petBitmap = decodeBase64(base64Image);
        petImageView.setImageBitmap(petBitmap);
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
