package com.example.studyplanner;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTitle;
    private EditText taskDeadline;
    private Button saveTaskButton;

    private FirebaseFirestore db;

    private String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        taskTitle = findViewById(R.id.taskTitle);
        taskDeadline = findViewById(R.id.taskDeadline);
        saveTaskButton = findViewById(R.id.saveTaskButton);

        db = FirebaseFirestore.getInstance();

        subjectId = getIntent().getStringExtra("subjectId");

        // Open calendar when deadline is clicked
        taskDeadline.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog =
                    new DatePickerDialog(
                            AddTaskActivity.this,
                            (DatePicker view, int selectedYear,
                             int selectedMonth, int selectedDay) -> {

                                String date =
                                        String.format(
                                                "%02d-%02d-%04d",
                                                selectedDay,
                                                selectedMonth + 1,
                                                selectedYear
                                        );

                                taskDeadline.setText(date);
                            },
                            year,
                            month,
                            day
                    );

            datePickerDialog.show();
        });

        saveTaskButton.setOnClickListener(v -> {

            String title =
                    taskTitle.getText().toString().trim();

            String deadline =
                    taskDeadline.getText().toString().trim();

            if (title.isEmpty()) {

                Toast.makeText(
                        AddTaskActivity.this,
                        "Enter task",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (deadline.isEmpty()) {

                Toast.makeText(
                        AddTaskActivity.this,
                        "Select deadline",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (subjectId == null) {

                Toast.makeText(
                        AddTaskActivity.this,
                        "Subject not found",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Map<String, Object> task =
                    new HashMap<>();

            task.put("subjectID", subjectId);
            task.put("title", title);
            task.put("deadline", deadline);
            task.put("completed", false);

            saveTaskButton.setEnabled(false);

            db.collection("tasks")
                    .add(task)
                    .addOnSuccessListener(
                            documentReference -> {

                                Toast.makeText(
                                        AddTaskActivity.this,
                                        "Task Added",
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            }
                    )
                    .addOnFailureListener(e -> {

                        saveTaskButton.setEnabled(true);

                        Toast.makeText(
                                AddTaskActivity.this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }
}