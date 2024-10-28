package com.example.petadoption;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddPetActivity extends AppCompatActivity {

    private EditText petNameEditText, ageEditText, breedEditText, sizeEditText, descEditText, healthEditText, locationEditText, rescueLocEditText;
    private RadioButton maleRadioButton, femaleRadioButton, streetYesRadioButton, streetNoRadioButton;
    private Button uploadButton, submitButton;
    private FirebaseAuth mAuth;
    private DatabaseReference userPetsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pet);

        // Initialize Firebase Auth and Database reference
        mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        userPetsRef = FirebaseDatabase.getInstance().getReference("pets").child(userId);

        // Initialize input fields
        petNameEditText = findViewById(R.id.petnameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        breedEditText = findViewById(R.id.breedEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        descEditText = findViewById(R.id.DescEditText);
        healthEditText = findViewById(R.id.HealthEditText);
        locationEditText = findViewById(R.id.LocationEditText);
        rescueLocEditText = findViewById(R.id.rescueLocEditText);

        maleRadioButton = findViewById(R.id.Male);
        femaleRadioButton = findViewById(R.id.Female);
        streetYesRadioButton = findViewById(R.id.StreetYes);
        streetNoRadioButton = findViewById(R.id.StreetNo);

        uploadButton = findViewById(R.id.upload_button);
        submitButton = findViewById(R.id.submit_button);

        // Upload photos (implement functionality as needed)
        uploadButton.setOnClickListener(v -> {
            // Code for selecting and uploading photos
        });

        // Submit pet data to Firebase
        submitButton.setOnClickListener(v -> savePetData());
    }

    private void savePetData() {
        String petName = petNameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String breed = breedEditText.getText().toString().trim();
        String size = sizeEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();
        String healthStatus = healthEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String rescueLocation = rescueLocEditText.getText().toString().trim();

        String gender = maleRadioButton.isChecked() ? "Male" : femaleRadioButton.isChecked() ? "Female" : "";
        String isStreetPet = streetYesRadioButton.isChecked() ? "Yes" : streetNoRadioButton.isChecked() ? "No" : "";

        if (TextUtils.isEmpty(petName) || TextUtils.isEmpty(age) || TextUtils.isEmpty(breed) || TextUtils.isEmpty(size) || TextUtils.isEmpty(gender) || TextUtils.isEmpty(isStreetPet)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> petData = new HashMap<>();
        petData.put("petName", petName);
        petData.put("age", age);
        petData.put("breed", breed);
        petData.put("size", size);
        petData.put("gender", gender);
        petData.put("description", description);
        petData.put("healthStatus", healthStatus);
        petData.put("location", location);
        petData.put("isStreetPet", isStreetPet);
        petData.put("rescueLocation", rescueLocation);
        // Add photo URLs after implementing photo upload

        userPetsRef.push().setValue(petData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddPetActivity.this, "Pet data saved successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddPetActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(AddPetActivity.this, "Failed to save pet data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
