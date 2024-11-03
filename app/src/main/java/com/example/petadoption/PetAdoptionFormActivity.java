package com.example.petadoption;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PetAdoptionFormActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextMessage;
    private Button buttonSubmit;

    private String ownerId;
    private String petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_adoption_form); // Make sure this matches your layout name

        // Retrieve ownerId and petId from the intent
        Intent intent = getIntent();
        ownerId = intent.getStringExtra("ownerId");
        petId = intent.getStringExtra("petId");

        editTextName = findViewById(R.id.editTextName);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitAdoptionRequest();
            }
        });
    }

    private void submitAdoptionRequest() {
        // Get the input data
        String name = editTextName.getText().toString().trim();
        String message = editTextMessage.getText().toString().trim();

        // Check if inputs are valid
        if (name.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an AdoptionRequest object
        AdoptionRequest request = new AdoptionRequest(ownerId, petId, name, message);

        // Save the request to Firebase
        DatabaseReference requestsRef = FirebaseDatabase.getInstance().getReference("adoptionRequests");
        String requestId = requestsRef.push().getKey(); // Generate a unique key for the request
        requestsRef.child(requestId).setValue(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Adoption request submitted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity or navigate as needed
            } else {
                Toast.makeText(this, "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
