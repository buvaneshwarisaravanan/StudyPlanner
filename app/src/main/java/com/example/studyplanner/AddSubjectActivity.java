package com.example.studyplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText subjectName;
    private Button addSubjectButton;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        subjectName = findViewById(R.id.subjectName);
        addSubjectButton = findViewById(R.id.addSubjectButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        addSubjectButton.setOnClickListener(v -> {

            String name = subjectName.getText().toString().trim();

            if (name.isEmpty()) {

                Toast.makeText(
                        AddSubjectActivity.this,
                        "Enter subject name",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (mAuth.getCurrentUser() == null) {

                Toast.makeText(
                        AddSubjectActivity.this,
                        "User not logged in",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            String userId = mAuth.getCurrentUser().getUid();

            Map<String, Object> subject = new HashMap<>();

            subject.put("userID", userId);
            subject.put("subjectName", name);

            addSubjectButton.setEnabled(false);

            db.collection("subjects")
                    .add(subject)
                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(
                                AddSubjectActivity.this,
                                "Subject Added",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    })
                    .addOnFailureListener(e -> {

                        addSubjectButton.setEnabled(true);

                        Toast.makeText(
                                AddSubjectActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}