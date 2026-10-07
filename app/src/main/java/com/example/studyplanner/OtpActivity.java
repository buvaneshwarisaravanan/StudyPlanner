package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class OtpActivity extends AppCompatActivity {

    private EditText otp;
    private Button verifyOtpButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        otp = findViewById(R.id.otp);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        verifyOtpButton.setOnClickListener(v -> {

            String code = otp.getText().toString().trim();

            if (code.isEmpty()) {
                Toast.makeText(
                        OtpActivity.this,
                        "Enter OTP",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String verificationId =
                    getIntent().getStringExtra("verificationId");

            if (verificationId == null) {
                Toast.makeText(
                        OtpActivity.this,
                        "Verification ID missing",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            String userId =
                                    mAuth.getCurrentUser().getUid();

                            String phone =
                                    mAuth.getCurrentUser().getPhoneNumber();

                            Map<String, Object> user =
                                    new HashMap<>();

                            user.put("phone", phone);
                            user.put(
                                    "createdAt",
                                    System.currentTimeMillis()
                            );

                            db.collection("users")
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {

                                        Toast.makeText(
                                                OtpActivity.this,
                                                "Login Successful",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent =
                                                new Intent(
                                                        OtpActivity.this,
                                                        DashboardActivity.class
                                                );

                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(
                                                OtpActivity.this,
                                                "User save failed: "
                                                        + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    });

                        } else {

                            Toast.makeText(
                                    OtpActivity.this,
                                    "Invalid OTP",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
        });
    }
}