package com.example.petadoption;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private DatabaseReference userRef;
    private ImageView userProfileImageView;

    private DatabaseReference petRef;
    private RecyclerView recyclerView;
    private HomePetAdapter HomePetAdapter;
    private List<Pet> petList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Fetch and display the profile image
        userProfileImageView = view.findViewById(R.id.userIcon);
        fetchProfileImage();

        TextView textView = view.findViewById(R.id.exploreTitle);
        SpannableString content = new SpannableString("Explore Pets");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserID = user != null ? user.getUid() : "";

        petRef = FirebaseDatabase.getInstance().getReference("pets");
        recyclerView = view.findViewById(R.id.homeRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        HomePetAdapter = new HomePetAdapter(getActivity(), petList);
        recyclerView.setAdapter(HomePetAdapter);

        fetchOtherUsersPets(currentUserID);

        return view;
    }

    private void fetchProfileImage() {
        userRef.child("profileImageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String base64Image = dataSnapshot.getValue(String.class);
                if (base64Image != null) {
                    Bitmap bitmap = decodeFromBase64(base64Image);
                    userProfileImageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(getActivity(), "Profile image not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to fetch profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap decodeFromBase64(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void fetchOtherUsersPets(String currentUserID) {
        Log.d("HomeFragment", "Fetching pets for user: " + currentUserID);
        petRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                petList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String ownerId = userSnapshot.getKey();
                    if (!ownerId.equals(currentUserID)) {
                        for (DataSnapshot petSnapshot : userSnapshot.getChildren()) {
                            Pet pet = petSnapshot.getValue(Pet.class);
                            if (pet != null) {
                                pet.setOwnerId(ownerId);
                                Log.d("HomeFragment", "Owner ID: " + ownerId);
                                pet.setId(petSnapshot.getKey());
                                // Fetch owner's profile picture
                                FirebaseDatabase.getInstance().getReference("users").child(ownerId)
                                        .child("profileImageBase64")
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot profileSnapshot) {
                                                String profileImageBase64 = profileSnapshot.getValue(String.class);
                                                pet.setOwnerProfileImageBase64(profileImageBase64); // Set owner's profile picture in Pet object
                                                petList.add(pet);
                                                Log.d("HomeFragment", "Pet added: " + pet.getPetName());
                                                HomePetAdapter.updateData(petList); // Update adapter when data is ready
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Log.e("HomeFragment", "Failed to load owner's profile picture.");
                                            }
                                        });
                            }
                        }
                    }
                }
                Log.d("HomeFragment", "Total pets loaded: " + petList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load pets.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
