package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    EditText etName, etEmail, etPassword, etConfirmPass, etAge, etHeight, etWeight;
    Button btnCreateAccount;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        db = new DatabaseHelper(this);

        etName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        etAge = findViewById(R.id.etSignupAge);
        etHeight = findViewById(R.id.etSignupHeight);
        etWeight = findViewById(R.id.etSignupWeight);
        etPassword = findViewById(R.id.etSignupPassword);
        etConfirmPass = findViewById(R.id.etSignupConfirmPass);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirm = etConfirmPass.getText().toString().trim();

            String ageStr = etAge.getText().toString().trim();
            String heightStr = etHeight.getText().toString().trim();
            String weightStr = etWeight.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() ||
                    ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {

                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirm)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            int age = Integer.parseInt(ageStr);
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);

            boolean created = db.createUser(name, email, pass, age, height, weight);

            if (!created) {
                Toast.makeText(this, "Email already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
