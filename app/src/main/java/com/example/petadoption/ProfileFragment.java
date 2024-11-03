package com.example.petadoption;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ImageView userIcon, userPfp;
    private TextView nameEditText, addressEditText, petCounter;
    private DatabaseReference userRef;
    private Button addPetButton, editProfileButton, requestsButton;

    private RecyclerView recyclerViewPets;
    private DatabaseReference petsRef;
    private List<Pet> petList;
    private PetAdapter petAdapter;
    private ProfileViewModel profileViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userIcon = view.findViewById(R.id.userIcon);
        userPfp = view.findViewById(R.id.userpfp);
        nameEditText = view.findViewById(R.id.nameEditText);
        addPetButton = view.findViewById(R.id.btnAddPet);
        editProfileButton = view.findViewById(R.id.btnEditProfile);
        addressEditText = view.findViewById(R.id.addressEditText);
        petCounter = view.findViewById(R.id.petCounter);
        requestsButton = view.findViewById(R.id.btnViewRequests);

        // Initialize pet list and set up RecyclerView with adapter
        petList = new ArrayList<>();
        recyclerViewPets = view.findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(getContext()));
        petAdapter = new PetAdapter(getContext(), petList); // Initialize adapter with an empty list
        recyclerViewPets.setAdapter(petAdapter);

        // Set up click listener for userIcon
        userIcon.setOnClickListener(v -> showLogoutDialog());

        editProfileButton.setOnClickListener(v -> editProfile());

        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestsActivity.class);
                startActivity(intent);

            }
        });

                // Get Firebase Authentication instance and fetch user data from database
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            fetchUserName();
            fetchUserAddress();
            fetchUserProfileImage(); // Fetch profile image URL
            petsRef = FirebaseDatabase.getInstance().getReference("pets").child(uid);
            fetchPets(); // Fetch pets and update the RecyclerView
        }

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.getPetList().observe(getViewLifecycleOwner(), pets -> {
            if (petAdapter != null) {
                petAdapter.updateData(pets);
            }
            updatePetCounter(pets.size());
        });

        // Load data only if necessary
        if (profileViewModel.getPetList().getValue().isEmpty()) {
            fetchPets(); // Only fetch from Firebase if list is empty
        }

        // Navigate to AddPetActivity on addPetButton click
        addPetButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddPetActivity.class)));

        return view;
    }

    private void fetchUserProfileImage() {
        userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String base64Image = snapshot.getValue(String.class);
                    Bitmap bitmap = decodeFromBase64(base64Image);
                    if (bitmap != null) {
                        userIcon.setImageBitmap(bitmap);
                        userPfp.setImageBitmap(bitmap);
                    } else {
                        // Set a default image if decoding fails
                        userIcon.setImageResource(R.drawable.user_icon);
                        userPfp.setImageResource(R.drawable.user_icon);
                    }
                } else {
                    // Set a default image if no profile picture is found
                    userIcon.setImageResource(R.drawable.user_icon);
                    userPfp.setImageResource(R.drawable.user_icon);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
                userIcon.setImageResource(R.drawable.user_icon);// Set default image on error
                userPfp.setImageResource(R.drawable.user_icon);
            }
        });
    }

    private Bitmap decodeFromBase64(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void fetchUserName() {
        userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.getValue(String.class);
                    nameEditText.setText(name);
                } else {
                    nameEditText.setText("User"); // Default text if no name is found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nameEditText.setText("User"); // Error handling default text
            }
        });
    }

    private void fetchUserAddress() {
        userRef.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String address = snapshot.getValue(String.class);
                    addressEditText.setText(address);
                } else {
                    addressEditText.setText("No Address"); // Default text if no name is found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                nameEditText.setText("User"); // Error handling default text
            }
        });
    }

    private void updatePetCounter(int count) {
        petCounter.setText(String.valueOf(count));
    }

    private void editProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void fetchPets() {
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Pet> fetchedPets = new ArrayList<>();
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        fetchedPets.add(pet);
                    }
                }
                profileViewModel.setPetList(fetchedPets); // Update ViewModel data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logOutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logOutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
