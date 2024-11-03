package com.example.petadoption;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {
    private Context context;
    private List<Pet> petList;

    public PetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_card_pet_lister, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.petName.setText(pet.getPetName());
        holder.petLocation.setText(pet.getLocation());

        // Decode and set the image
        if (pet.getImageBase64() != null) {
            byte[] decodedString = Base64.decode(pet.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.petImage.setImageBitmap(decodedByte); // Set the image in the ImageView
        } else {
            holder.petImage.setImageResource(R.drawable.pet1); // Set a default image if no image is available
        }

        holder.petDeleteIcon.setOnClickListener(v -> {
            DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("pets");
            petsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Iterate through each user ID
                        for (DataSnapshot petSnapshot : userSnapshot.getChildren()) {
                            // Retrieve each pet under the user ID
                            Pet petToDelete = petSnapshot.getValue(Pet.class);

                            // Set the pet's ID to the Firebase key (petId)
                            if (petToDelete != null && petToDelete.getPetName().equals(pet.getPetName())) {
                                petToDelete.setId(petSnapshot.getKey()); // Set petId
                                Log.d("PetAdapter", "Pet ID: " + petToDelete.getId());

                                new AlertDialog.Builder(context)
                                        .setTitle("Delete Pet")
                                        .setMessage("Are you sure you want to delete this pet?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            // Deleting the pet after confirmation
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            if (user != null) {
                                                String uid = user.getUid();
                                                DatabaseReference petRef = petsRef.child(uid).child(petToDelete.getId());
                                                petRef.removeValue()
                                                        .addOnSuccessListener(aVoid -> {
                                                            Toast.makeText(context, "Pet deleted successfully.", Toast.LENGTH_SHORT).show();
                                                            petList.remove(position); // Remove the pet from the list
                                                            notifyItemRemoved(position); // Notify the adapter about the removed item
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(context, "Failed to delete pet.", Toast.LENGTH_SHORT).show();
                                                        });
                                            }
                                        })
                                        .setNegativeButton("No", null)
                                        .show();
                                return; // Exit after showing the dialog
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        });

        holder.petEditIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPetActivity.class);
            DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("pets");
            petsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        // Iterate through each user ID
                        for (DataSnapshot petSnapshot : userSnapshot.getChildren()) {
                            // Retrieve each pet under the user ID
                            Pet petToEdit = petSnapshot.getValue(Pet.class);

                            // Set the pet's ID to the Firebase key (petId)
                            if (petToEdit != null && petToEdit.getPetName().equals(pet.getPetName())) {
                                petToEdit.setId(petSnapshot.getKey()); // Set petId for editing
                                Log.d("PetAdapter", "Pet ID: " + petToEdit.getId());

                                intent.putExtra("petId", petToEdit.getId()); // Pass petId for database updates
                                intent.putExtra("petName", petToEdit.getPetName());
                                intent.putExtra("age", petToEdit.getAge());
                                intent.putExtra("breed", petToEdit.getBreed());
                                intent.putExtra("size", petToEdit.getSize());
                                intent.putExtra("gender", petToEdit.getGender());
                                intent.putExtra("description", petToEdit.getDescription());
                                intent.putExtra("healthStatus", petToEdit.getHealthStatus());
                                intent.putExtra("location", petToEdit.getLocation());
                                intent.putExtra("isStreet", petToEdit.isStreet());
                                intent.putExtra("rescueLocation", petToEdit.getRescueLocation());
                            }
                        }
                    }
                    context.startActivity(intent);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        TextView petName, petLocation;
        ImageView petDeleteIcon, petEditIcon, petImage; // Add petImage here

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name);
            petLocation = itemView.findViewById(R.id.pet_location);
            petDeleteIcon = itemView.findViewById(R.id.petdelete_icon);
            petEditIcon = itemView.findViewById(R.id.petedit_icon);
            petImage = itemView.findViewById(R.id.pet_image); // Initialize the petImage ImageView
        }
    }

    // Method to update the pet list and notify changes
    public void updateData(List<Pet> newPetList) {
        this.petList.clear(); // Clear the existing list
        this.petList.addAll(newPetList); // Add all new items
        notifyDataSetChanged(); // Notify the adapter to refresh the UI
    }
}
