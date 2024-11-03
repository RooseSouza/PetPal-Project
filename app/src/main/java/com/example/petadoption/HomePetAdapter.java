package com.example.petadoption;

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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HomePetAdapter extends RecyclerView.Adapter<HomePetAdapter.PetViewHolder> {

    private final Context context;
    private final List<Pet> petList;

    public HomePetAdapter(Context context, List<Pet> petList) {
        this.context = context;
        this.petList = petList;
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.homepage_pet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.petNameTextView.setText(pet.getPetName());
        holder.petLocationTextView.setText(pet.getLocation());

        // Decode and set images
        Bitmap decodedImage = decodeBase64ToBitmap(pet.getImageBase64());
        if (decodedImage != null) {
            holder.petImageView.setImageBitmap(decodedImage);
        } else {
            holder.petImageView.setImageResource(R.drawable.pet1);
        }

        Bitmap decodedOwnerImage = decodeBase64ToBitmap(pet.getOwnerProfileImageBase64());
        if (decodedOwnerImage != null) {
            holder.ownerProfileImageView.setImageBitmap(decodedOwnerImage);
        } else {
            holder.ownerProfileImageView.setImageResource(R.drawable.user_icon);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Log.d("HomePetAdapter", "Clicked on pet: " + pet.getPetName());
            Log.d("HomePetAdapter", "Owner ID: " + pet.getOwnerId());


            // Pass pet data to the intent
            if (pet.getOwnerId() != null) {
                Intent intent = new Intent(context, HomePetinfoActivity.class);
                intent.putExtra("petId", pet.getId()); // Assuming pet.getId() returns the pet ID
                intent.putExtra("ownerID", pet.getOwnerId());
                context.startActivity(intent);
            }else{
                Log.d("HomePetAdapter", "Owner ID is null for pet: " + pet.getPetName());
            }




        });
    }


    @Override
    public int getItemCount() {
        return petList.size();
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        try {
            byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView petImageView;
        TextView petNameTextView;
        ImageView ownerProfileImageView;
        TextView petLocationTextView;

        PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImageView = itemView.findViewById(R.id.PetImage);
            petNameTextView = itemView.findViewById(R.id.Name);
            ownerProfileImageView = itemView.findViewById(R.id.ProfilePicture);
            petLocationTextView = itemView.findViewById(R.id.Location);
        }
    }

    public void updateData(List<Pet> newPetList) {
        //petList.clear();
        Log.d("HomePetAdapter", "Updated data: " + newPetList.size());
        this.petList.addAll(newPetList);

        notifyDataSetChanged();
    }
}

