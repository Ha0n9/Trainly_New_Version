package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Toast;

    public class ChangePasswordActivity extends AppCompatActivity {
        EditText etOldPass, etNewPass, etConfirmPass;
        Button btnChangePassword;
        ImageButton btnBack;
        DatabaseHelper db;
        String email;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_change_password);

            // Initialize database helper
            db = new DatabaseHelper(this);

            // Get user email from Intent
            email = getIntent().getStringExtra("email");

            if (email == null) {
                Toast.makeText(this, "Error: Missing user email", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Bind views
            etOldPass = findViewById(R.id.etOldPass);
            etNewPass = findViewById(R.id.etNewPass);
            etConfirmPass = findViewById(R.id.etConfirmPass);
            btnChangePassword = findViewById(R.id.btnConfirmChangePass);
            btnBack = findViewById(R.id.btnBack);

            // Set button click listeners
            btnChangePassword.setOnClickListener(v -> handleChangePassword());
            btnBack.setOnClickListener(v -> finish());
        }

        private void handleChangePassword() {
            String oldPass = etOldPass.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();
            String confirmPass = etConfirmPass.getText().toString().trim();

            // Validation
            if (oldPass.isEmpty()) {
                Toast.makeText(this, "Please enter your old password", Toast.LENGTH_SHORT).show();
                etOldPass.requestFocus();
                return;
            }

            if (newPass.isEmpty()) {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show();
                etNewPass.requestFocus();
                return;
            }

            if (newPass.length() < 6) {
                Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                etNewPass.requestFocus();
                return;
            }

            if (confirmPass.isEmpty()) {
                Toast.makeText(this, "Please confirm your new password", Toast.LENGTH_SHORT).show();
                etConfirmPass.requestFocus();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                etConfirmPass.requestFocus();
                return;
            }

            if (oldPass.equals(newPass)) {
                Toast.makeText(this, "New password must be different from old password", Toast.LENGTH_SHORT).show();
                etNewPass.requestFocus();
                return;
            }

            // Update password in database
            boolean success = db.updatePassword(email, oldPass, newPass);

            if (success) {
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity and return to previous screen
            } else {
                Toast.makeText(this, "Incorrect old password. Please try again.", Toast.LENGTH_SHORT).show();
                etOldPass.requestFocus();
            }
        }
    }