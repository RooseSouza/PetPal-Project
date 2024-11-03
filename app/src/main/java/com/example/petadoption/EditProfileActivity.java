package com.example.petadoption;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, emailEditText, phoneEditText, addressEditText;
    private DatabaseReference userRef;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button uploadProfilePictureButton = findViewById(R.id.changeAvatarButton);

        userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Load existing user data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);

                    nameEditText.setText(name);
                    phoneEditText.setText(phone);
                    addressEditText.setText(address);
                } else {
                    Toast.makeText(EditProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this, "Error loading user data", Toast.LENGTH_SHORT).show();
            }
        });

        uploadProfilePictureButton.setOnClickListener(v -> openFileChooser());

        saveButton.setOnClickListener(v -> {
            String newName = nameEditText.getText().toString();
            String newPhone = phoneEditText.getText().toString();
            String newAddress = addressEditText.getText().toString();

            if (newName.isEmpty() || newAddress.isEmpty() || newPhone.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                convertAndUploadImageToFirebase(newName, newPhone, newAddress);
            } else {
                updateUserProfile(newName, newPhone, newAddress, null);
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
        }
    }

    private void convertAndUploadImageToFirebase(String name, String phone, String address) {
        try {
            // Open an InputStream to get the image data
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Convert bitmap to Base64
            String base64Image = encodeToBase64(bitmap);

            // Save Base64 string in Firebase Realtime Database
            userRef.child("profileImageBase64").setValue(base64Image).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateUserProfile(name, phone, address, base64Image);
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload picture. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(EditProfileActivity.this, "Failed to process image", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void updateUserProfile(String name, String phone, String address, String base64Image) {
        userRef.child("name").setValue(name);
        userRef.child("phone").setValue(phone);
        userRef.child("address").setValue(address);

        if (base64Image != null) {
            userRef.child("profileImageBase64").setValue(base64Image);
        }

        userRef.updateChildren(new HashMap<>()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditProfileActivity.this, "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
