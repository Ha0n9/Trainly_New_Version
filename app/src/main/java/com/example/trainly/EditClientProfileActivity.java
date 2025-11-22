package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditClientProfileActivity extends AppCompatActivity {

    EditText etName, etAge, etHeight, etWeight;
    Button btnSaveChanges, btnChangePassword;

    DatabaseHelper db;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client_profile);

        TextView toolbarTitle = findViewById(R.id.tvToolbarTitle);
        if (toolbarTitle != null) {
            toolbarTitle.setText(getString(R.string.profile_edit_title));
        }
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        btnSaveChanges = findViewById(R.id.btnSaveProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        db = new DatabaseHelper(this);

        email = getIntent().getStringExtra("email");
        if (email == null) {
            Toast.makeText(this, "Error: Missing email", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProfile();

        btnSaveChanges.setOnClickListener(v -> saveProfile());

        btnChangePassword.setOnClickListener(v -> {
            Intent i = new Intent(this, ChangePasswordActivity.class);
            i.putExtra("email", email);
            startActivity(i);
        });
    }

    private void loadProfile() {
        var c = db.getReadableDatabase().rawQuery(
                "SELECT name, age, height, weight FROM users WHERE email=?",
                new String[]{email}
        );

        if (c.moveToFirst()) {
            etName.setText(c.getString(0));
            etAge.setText(String.valueOf(c.getInt(1)));
            etHeight.setText(String.valueOf(c.getDouble(2)));
            etWeight.setText(String.valueOf(c.getDouble(3)));
        }
        c.close();
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String ageStr = etAge.getText().toString().trim();
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int age = Integer.parseInt(ageStr);
        double height = Double.parseDouble(heightStr);
        double weight = Double.parseDouble(weightStr);

        var cv = new android.content.ContentValues();
        cv.put("name", name);
        cv.put("age", age);
        cv.put("height", height);
        cv.put("weight", weight);

        db.getWritableDatabase().update("users", cv, "email=?", new String[]{email});

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
        finish();
    }
}
