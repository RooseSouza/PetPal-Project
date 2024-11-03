package com.example.petadoption;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class EditPetActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText petNameEditText, ageEditText, breedEditText, sizeEditText, DescEditText, HealthEditText, LocationEditText, rescueLocEditText;
    private RadioGroup genderRadioGroup, isStreetRadioGroup;
    private Button submitButton, uploadPhotoButton;
    private String petId;
    private ImageView petImageView;
    private String petImageBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        petNameEditText = findViewById(R.id.petnameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        breedEditText = findViewById(R.id.breedEditText);
        sizeEditText = findViewById(R.id.sizeEditText);
        DescEditText = findViewById(R.id.DescEditText);
        HealthEditText = findViewById(R.id.HealthEditText);
        LocationEditText = findViewById(R.id.LocationEditText);
        rescueLocEditText = findViewById(R.id.rescueLocEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        isStreetRadioGroup = findViewById(R.id.isStreetRadioGroup);
        submitButton = findViewById(R.id.submit_button);
        uploadPhotoButton = findViewById(R.id.uploadButton);
        petImageView = findViewById(R.id.petImageView); // Add an ImageView to show the selected image

        // Retrieve data from intent
        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");
        Log.d("EditPetActivity", "Received Pet ID: " + petId);

        if (petId != null) {
            fetchPetDetailsFromFirebase(petId);
        } else {
            Toast.makeText(this, "Pet ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if petId is null
        }

        submitButton.setOnClickListener(v -> updatePetDetails());

        // Set up image upload button
        uploadPhotoButton.setOnClickListener(v -> openFileChooser());
    }

    private void fetchPetDetailsFromFirebase(String petId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("pets").child(uid).child(petId);

            petRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    Pet pet = task.getResult().getValue(Pet.class);
                    if (pet != null) {
                        // Fill in the details from Firebase
                        petNameEditText.setText(pet.getPetName());
                        ageEditText.setText(pet.getAge());
                        breedEditText.setText(pet.getBreed());
                        sizeEditText.setText(pet.getSize());
                        DescEditText.setText(pet.getDescription());
                        HealthEditText.setText(pet.getHealthStatus());
                        LocationEditText.setText(pet.getLocation());
                        rescueLocEditText.setText(pet.getRescueLocation());

                        // Load the pet image if exists
                        if (pet.getImageBase64() != null) {
                            petImageBase64 = pet.getImageBase64();
                            byte[] decodedString = Base64.decode(petImageBase64, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            petImageView.setImageBitmap(decodedByte);
                        }

                        // Set gender
                        if ("Male".equals(pet.getGender())) {
                            genderRadioGroup.check(R.id.Male);
                        } else {
                            genderRadioGroup.check(R.id.Female);
                        }

                        // Set street status as Yes or No
                        isStreetRadioGroup.check(R.id.StreetNo);
                    }
                } else {
                    Toast.makeText(this, "Failed to load pet details", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                petImageView.setImageBitmap(bitmap);
                petImageBase64 = convertBitmapToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updatePetDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && petId != null) {
            String uid = user.getUid();
            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("pets").child(uid).child(petId);

            // Retrieve updated details from the fields
            String petName = petNameEditText.getText().toString().trim();
            String age = ageEditText.getText().toString().trim();
            String breed = breedEditText.getText().toString().trim();
            String size = sizeEditText.getText().toString().trim();
            String description = DescEditText.getText().toString().trim();
            String healthStatus = HealthEditText.getText().toString().trim();
            String location = LocationEditText.getText().toString().trim();
            String rescueLocation = rescueLocEditText.getText().toString().trim();

            // Gender and street fields as String
            String gender = (genderRadioGroup.getCheckedRadioButtonId() == R.id.Male) ? "Male" : "Female";
            boolean isStreet = (isStreetRadioGroup.getCheckedRadioButtonId() == R.id.StreetYes);

            // Update the database with the new details
            petRef.child("petName").setValue(petName);
            petRef.child("age").setValue(age);
            petRef.child("breed").setValue(breed);
            petRef.child("size").setValue(size);
            petRef.child("description").setValue(description);
            petRef.child("healthStatus").setValue(healthStatus);
            petRef.child("location").setValue(location);
            petRef.child("rescueLocation").setValue(rescueLocation);
            petRef.child("gender").setValue(gender);
            petRef.child("isStreet").setValue(isStreet);
            petRef.child("imageBase64").setValue(petImageBase64) // Save the Base64 image string
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pet details updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ProfileFragment.class);
                        finish(); // Return to the previous screen
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to update pet details", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "User not authenticated or Pet ID missing", Toast.LENGTH_SHORT).show();
        }
    }
}
