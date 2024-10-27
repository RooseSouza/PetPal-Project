package com.example.petadoption;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Display the splash screen for 1 second
        new Handler().postDelayed(() -> {
            // Check if the user is logged in
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // User is logged in, go to homepage
                startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
            } else {
                // User is not logged in, go to login page
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 1000); // 1 second delay
    }
}
