package com.example.trainly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmailFP;
    Button btnRecover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        DatabaseHelper db = new DatabaseHelper(this);

        etEmailFP = findViewById(R.id.etForgotEmail);
        btnRecover = findViewById(R.id.btnForgotReset);

        btnRecover.setOnClickListener(v -> {

            String email = etEmailFP.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
                return;
            }

            String pass = db.getPasswordByEmail(email);

            if (pass == null) {
                Toast.makeText(this, "Email not found", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Your password: " + pass, Toast.LENGTH_LONG).show();
            }
        });
    }
}
