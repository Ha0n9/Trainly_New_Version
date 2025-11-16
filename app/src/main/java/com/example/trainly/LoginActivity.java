package com.example.trainly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvGoSignup, tvForgotPassword;

    DatabaseHelper db;  // <-- only DatabaseHelper now

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoSignup = findViewById(R.id.tvGoSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (!db.loginUser(email, pass)) {
                Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                return;
            }

            String name = db.getUserNameByEmail(email);
            String role = db.getUserRole(email);

            if ("trainer".equals(role)) {
                startActivity(
                        new Intent(this, TrainerDashboardActivity.class)
                                .putExtra("email", email)
                );
            } else {
                Intent i = new Intent(this, ClientProfileActivity.class);
                i.putExtra("email", email);
                i.putExtra("name", name);
                i.putExtra("role", role);

                startActivity(i);
            }
        });

        tvGoSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );

        tvForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );
    }
}
