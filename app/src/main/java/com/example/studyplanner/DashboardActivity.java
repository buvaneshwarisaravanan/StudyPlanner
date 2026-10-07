package com.example.studyplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private Button addSubjectButton;
    private Button logoutButton;
    private TextView noSubjectsText;
    private RecyclerView subjectRecyclerView;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private ArrayList<String> subjectList;
    private ArrayList<String> subjectIdList;

    private SubjectAdapter subjectAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        addSubjectButton = findViewById(R.id.addSubjectButton);
        logoutButton = findViewById(R.id.logoutButton);
        noSubjectsText = findViewById(R.id.noSubjectsText);
        subjectRecyclerView = findViewById(R.id.subjectRecyclerView);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        subjectList = new ArrayList<>();
        subjectIdList = new ArrayList<>();

        subjectAdapter = new SubjectAdapter(
                this,
                subjectList,
                subjectIdList
        );

        subjectRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        subjectRecyclerView.setAdapter(subjectAdapter);

        // Add Subject button
        addSubjectButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    AddSubjectActivity.class
            );

            startActivity(intent);
        });

        // Logout button
        logoutButton.setOnClickListener(v -> {

            mAuth.signOut();

            Toast.makeText(
                    DashboardActivity.this,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
            ).show();

            Intent intent = new Intent(
                    DashboardActivity.this,
                    MainActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });

        loadSubjects();
    }

    private void loadSubjects() {

        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("subjects")
                .whereEqualTo("userID", userId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        subjectList.clear();
                        subjectIdList.clear();

                        for (QueryDocumentSnapshot document :
                                task.getResult()) {

                            String name =
                                    document.getString("subjectName");

                            if (name != null) {

                                subjectList.add(name);

                                subjectIdList.add(
                                        document.getId()
                                );
                            }
                        }

                        subjectAdapter.notifyDataSetChanged();

                        if (subjectList.isEmpty()) {

                            noSubjectsText.setVisibility(
                                    View.VISIBLE
                            );

                            subjectRecyclerView.setVisibility(
                                    View.GONE
                            );

                        } else {

                            noSubjectsText.setVisibility(
                                    View.GONE
                            );

                            subjectRecyclerView.setVisibility(
                                    View.VISIBLE
                            );
                        }

                    } else {

                        Toast.makeText(
                                DashboardActivity.this,
                                "Failed to load subjects",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subjectList != null) {
            loadSubjects();
        }
    }
}