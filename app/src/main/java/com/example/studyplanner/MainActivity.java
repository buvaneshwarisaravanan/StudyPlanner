package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;
    private Button loginButton;
    private Button registerButton;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(v -> {

            String phone = phoneNumber.getText().toString().trim();
            String pin = password.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Enter phone number",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (pin.isEmpty()) {
                Toast.makeText(
                        MainActivity.this,
                        "Enter PIN/Password",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            if (!phone.startsWith("+")) {
                phone = "+91" + phone;
            }

            String email =
                    phone.replace("+", "") + "@studyplanner.app";

            loginButton.setEnabled(false);

            mAuth.signInWithEmailAndPassword(email, pin)
                    .addOnCompleteListener(task -> {

                        loginButton.setEnabled(true);

                        if (task.isSuccessful()) {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    MainActivity.this,
                                    DashboardActivity.class
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    MainActivity.this,
                                    "Login failed. Check phone number and PIN.",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });

        registerButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    RegisterActivity.class
            );

            startActivity(intent);
        });
    }
}

