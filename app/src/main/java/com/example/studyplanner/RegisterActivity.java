package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phoneNumber = findViewById(R.id.registerPhone);
        password = findViewById(R.id.registerPassword);
        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(v -> {

            String phone = phoneNumber.getText().toString().trim();
            String pin = password.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Enter phone number",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (pin.isEmpty()) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Enter PIN/Password",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (pin.length() < 6) {
                Toast.makeText(
                        RegisterActivity.this,
                        "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!phone.startsWith("+")) {
                phone = "+91" + phone;
            }

            String email =
                    phone.replace("+", "") + "@studyplanner.app";

            registerButton.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(email, pin)
                    .addOnCompleteListener(task -> {

                        registerButton.setEnabled(true);

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Account Created Successfully",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    RegisterActivity.this,
                                    DashboardActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    RegisterActivity.this,
                                    "Registration failed: "
                                            + task.getException().getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}

