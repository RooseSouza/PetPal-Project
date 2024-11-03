package com.example.petadoption;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdoptionRequestAdapter extends RecyclerView.Adapter<AdoptionRequestAdapter.ViewHolder> {
    private List<AdoptionRequest> requests;
    private Context context;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(AdoptionRequest request);
        void onReject(String requestId);
    }

    public AdoptionRequestAdapter(List<AdoptionRequest> requests, Context context, OnRequestActionListener listener) {
        this.requests = requests;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_adoption_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdoptionRequest request = requests.get(position);

        // Set requester details
        holder.reqName.setText("Request from: " + request.getName());
        holder.reqMessage.setText("Message: " + request.getMessage());

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Fetch and display pet name and photo based on petId from Firebase
        DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("pets")
                .child(currentUserId) // Owner ID for the pet's owner
                .child(request.getPetId());  // Pet ID

        // Fetch the pet name
        petRef.child("petName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String petName = snapshot.getValue(String.class);
                    holder.petName.setText(petName);
                } else {
                    holder.petName.setText("Unknown Pet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.petName.setText("Error loading pet name");
            }
        });

        // Fetch the pet photo (stored as Base64) and decode it
        petRef.child("imageBase64").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String base64Image = snapshot.getValue(String.class);
                    if (base64Image != null && !base64Image.isEmpty()) {
                        // Decode Base64 to Bitmap
                        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                        // Load the decoded Bitmap into the ImageView using Glide
                        Glide.with(context).load(decodedBitmap).into(holder.petPhoto);
                    } else {
                        holder.petPhoto.setImageResource(R.drawable.pet1); // Default image if Base64 is empty
                    }
                } else {
                    holder.petPhoto.setImageResource(R.drawable.pet1); // Default image if no photo found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.petPhoto.setImageResource(R.drawable.pet1); // Set default on error
            }
        });

        // Set click listeners for Approve and Reject buttons
        holder.buttonApprove.setOnClickListener(v -> listener.onApprove(request));
        holder.buttonReject.setOnClickListener(v -> listener.onReject(request.getPetId())); // Assuming petId is the requestId
    }


    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView petPhoto;
        TextView petName;
        TextView reqName;
        TextView reqMessage;
        Button buttonApprove;
        Button buttonReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petPhoto = itemView.findViewById(R.id.petPhoto);
            petName = itemView.findViewById(R.id.petName);
            reqName = itemView.findViewById(R.id.reqName);
            reqMessage = itemView.findViewById(R.id.reqMessage);
            buttonApprove = itemView.findViewById(R.id.buttonApprove);
            buttonReject = itemView.findViewById(R.id.buttonReject);
        }
    }
}
