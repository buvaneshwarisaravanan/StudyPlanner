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

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    private TextView subjectTitle;
    private TextView noTasksText;

    private Button addTaskButton;
    private RecyclerView taskRecyclerView;

    private FirebaseFirestore db;

    private ArrayList<String> taskIdList;
    private ArrayList<String> taskTitleList;
    private ArrayList<String> taskDeadlineList;
    private ArrayList<Boolean> completedList;

    private TaskAdapter taskAdapter;

    private String subjectId;
    private String subjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tasks);

        subjectTitle = findViewById(R.id.subjectTitle);
        noTasksText = findViewById(R.id.noTasksText);
        addTaskButton = findViewById(R.id.addTaskButton);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);

        db = FirebaseFirestore.getInstance();

        subjectId = getIntent().getStringExtra("subjectId");
        subjectName = getIntent().getStringExtra("subjectName");

        if (subjectName != null) {
            subjectTitle.setText(subjectName + " Tasks");
        }

        taskIdList = new ArrayList<>();
        taskTitleList = new ArrayList<>();
        taskDeadlineList = new ArrayList<>();
        completedList = new ArrayList<>();

        taskAdapter = new TaskAdapter(
                taskIdList,
                taskTitleList,
                taskDeadlineList,
                completedList
        );

        taskRecyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        taskRecyclerView.setAdapter(taskAdapter);

        addTaskButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TasksActivity.this,
                    AddTaskActivity.class
            );

            intent.putExtra("subjectId", subjectId);

            startActivity(intent);
        });

        loadTasks();
    }

    private void loadTasks() {

        if (subjectId == null) {
            return;
        }

        db.collection("tasks")
                .whereEqualTo("subjectID", subjectId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        taskIdList.clear();
                        taskTitleList.clear();
                        taskDeadlineList.clear();
                        completedList.clear();

                        for (QueryDocumentSnapshot document :
                                task.getResult()) {

                            String title =
                                    document.getString("title");

                            String deadline =
                                    document.getString("deadline");

                            Boolean completed =
                                    document.getBoolean("completed");

                            if (title != null) {

                                taskIdList.add(
                                        document.getId()
                                );

                                taskTitleList.add(title);

                                if (deadline != null) {
                                    taskDeadlineList.add(deadline);
                                } else {
                                    taskDeadlineList.add("");
                                }

                                if (completed != null) {
                                    completedList.add(completed);
                                } else {
                                    completedList.add(false);
                                }
                            }
                        }

                        taskAdapter.notifyDataSetChanged();

                        if (taskTitleList.isEmpty()) {

                            noTasksText.setVisibility(
                                    View.VISIBLE
                            );

                            taskRecyclerView.setVisibility(
                                    View.GONE
                            );

                        } else {

                            noTasksText.setVisibility(
                                    View.GONE
                            );

                            taskRecyclerView.setVisibility(
                                    View.VISIBLE
                            );
                        }

                    } else {

                        Toast.makeText(
                                TasksActivity.this,
                                "Failed to load tasks",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (taskTitleList != null) {
            loadTasks();
        }
    }
}