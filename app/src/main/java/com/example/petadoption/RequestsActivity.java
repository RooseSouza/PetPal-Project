package com.example.petadoption;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements AdoptionRequestAdapter.OnRequestActionListener {
    private RecyclerView recyclerView;
    private AdoptionRequestAdapter adapter;
    private List<AdoptionRequest> adoptionRequests;
    private DatabaseReference requestsRef;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adoptionRequests = new ArrayList<>();
        adapter = new AdoptionRequestAdapter(adoptionRequests, this, this);
        recyclerView.setAdapter(adapter);

        // Get the current user's ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsRef = FirebaseDatabase.getInstance().getReference("adoptionRequests").child(currentUserId);

        // Fetch adoption requests only for the current user
        fetchAdoptionRequests();
    }

    private void fetchAdoptionRequests() {
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adoptionRequests.clear();

                if (!dataSnapshot.exists()) {
                    // No requests for the current user, adapter will display nothing
                    adapter.notifyDataSetChanged();
                    return;
                }

                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                    AdoptionRequest request = requestSnapshot.getValue(AdoptionRequest.class);
                    if (request != null) {
                        request.setRequestId(requestSnapshot.getKey()); // Set the request ID from Firebase
                        adoptionRequests.add(request);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RequestsActivity.this, "Failed to load requests.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onApprove(AdoptionRequest request) {
        // Approve logic: remove the specific request
        requestsRef.child(request.getRequestId()).removeValue(); // Remove the specific request under adoptionRequests > currentUserId > requestId

        // Implement pet deletion logic from the owner's pets node
        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("pets")
                .child(currentUserId)
                .child(request.getPetId());
        petsRef.removeValue();
    }

    @Override
    public void onReject(String requestId) {
        // Reject logic: remove request from database
        requestsRef.child(requestId).removeValue();
    }
}
