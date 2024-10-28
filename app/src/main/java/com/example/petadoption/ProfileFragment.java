package com.example.petadoption;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
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

    private ImageView userIcon;
    private TextView nameEditText;
    private DatabaseReference userRef;
    private Button addPetButton;

    private RecyclerView recyclerViewPets;
    private DatabaseReference petsRef;
    private List<Pet> petList;
    private PetAdapter petAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        userIcon = view.findViewById(R.id.userIcon);
        nameEditText = view.findViewById(R.id.nameEditText);
        addPetButton = view.findViewById(R.id.btnAddPet);

        // Set up click listener for userIcon
        userIcon.setOnClickListener(v -> showLogoutDialog());

        recyclerViewPets = view.findViewById(R.id.recyclerViewPets);
        recyclerViewPets.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get Firebase Authentication instance and fetch user data from database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
            fetchUserName();
            petsRef = FirebaseDatabase.getInstance().getReference("pets").child(uid);
            fetchPets();
        }

        addPetButton.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), AddPetActivity.class)));
        return view;
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

    private void fetchPets() {
        petList = new ArrayList<>();
        petsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot petSnapshot : snapshot.getChildren()) {
                    Pet pet = petSnapshot.getValue(Pet.class);
                    if (pet != null) {
                        petList.add(pet);
                    }
                }
                petAdapter = new PetAdapter(petList);
                recyclerViewPets.setAdapter(petAdapter);
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
