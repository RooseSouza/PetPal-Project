package com.example.petadoption;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private ImageView userIcon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize userIcon ImageView
        userIcon = view.findViewById(R.id.userIcon);

        // Set up click listener for userIcon
        userIcon.setOnClickListener(v -> showLogoutDialog());

        return view;
    }

    private void showLogoutDialog() {
        // Create an AlertDialog to confirm logout
        new AlertDialog.Builder(requireContext())
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> logOutUser())
                .setNegativeButton("No", null)
                .show();
    }

    private void logOutUser() {
        // Log out the current user
        FirebaseAuth.getInstance().signOut();

        // Redirect to LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish current activity to prevent back navigation
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
