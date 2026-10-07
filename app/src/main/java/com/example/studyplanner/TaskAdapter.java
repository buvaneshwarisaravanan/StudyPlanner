package com.example.studyplanner;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TaskAdapter
        extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<String> taskIdList;
    private List<String> taskTitleList;
    private List<String> taskDeadlineList;
    private List<Boolean> completedList;

    private FirebaseFirestore db;

    public TaskAdapter(
            List<String> taskIdList,
            List<String> taskTitleList,
            List<String> taskDeadlineList,
            List<Boolean> completedList) {

        this.taskIdList = taskIdList;
        this.taskTitleList = taskTitleList;
        this.taskDeadlineList = taskDeadlineList;
        this.completedList = completedList;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_task,
                        parent,
                        false
                );

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaskViewHolder holder,
            int position) {

        holder.taskTitleText.setText(
                taskTitleList.get(position)
        );

        holder.taskDeadlineText.setText(
                "Deadline: " + taskDeadlineList.get(position)
        );

        // Prevent unwanted checkbox listener calls
        holder.taskCheckBox.setOnCheckedChangeListener(null);

        boolean completed = completedList.get(position);

        holder.taskCheckBox.setChecked(completed);

        // Show strike-through for completed tasks
        if (completed) {

            holder.taskTitleText.setPaintFlags(
                    holder.taskTitleText.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG
            );

        } else {

            holder.taskTitleText.setPaintFlags(
                    holder.taskTitleText.getPaintFlags()
                            & (~Paint.STRIKE_THRU_TEXT_FLAG)
            );
        }

        // Checkbox click
        holder.taskCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {

                    String taskId =
                            taskIdList.get(position);

                    db.collection("tasks")
                            .document(taskId)
                            .update("completed", isChecked)
                            .addOnSuccessListener(aVoid -> {

                                completedList.set(
                                        position,
                                        isChecked
                                );

                                if (isChecked) {

                                    holder.taskTitleText.setPaintFlags(
                                            holder.taskTitleText.getPaintFlags()
                                                    | Paint.STRIKE_THRU_TEXT_FLAG
                                    );

                                    Toast.makeText(
                                            buttonView.getContext(),
                                            "Task Completed",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    holder.taskTitleText.setPaintFlags(
                                            holder.taskTitleText.getPaintFlags()
                                                    & (~Paint.STRIKE_THRU_TEXT_FLAG)
                                    );

                                    Toast.makeText(
                                            buttonView.getContext(),
                                            "Task Marked Incomplete",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                }
                            })
                            .addOnFailureListener(e -> {

                                holder.taskCheckBox.setChecked(
                                        !isChecked
                                );

                                Toast.makeText(
                                        buttonView.getContext(),
                                        "Update failed",
                                        Toast.LENGTH_SHORT
                                ).show();
                            });
                }
        );

        // Long press to delete task
        holder.itemView.setOnLongClickListener(v -> {

            String taskId =
                    taskIdList.get(position);

            db.collection("tasks")
                    .document(taskId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        taskIdList.remove(position);
                        taskTitleList.remove(position);
                        taskDeadlineList.remove(position);
                        completedList.remove(position);

                        notifyItemRemoved(position);

                        Toast.makeText(
                                v.getContext(),
                                "Task Deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                v.getContext(),
                                "Delete failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskTitleList.size();
    }

    public static class TaskViewHolder
            extends RecyclerView.ViewHolder {

        CheckBox taskCheckBox;
        TextView taskTitleText;
        TextView taskDeadlineText;

        public TaskViewHolder(
                @NonNull View itemView) {

            super(itemView);

            taskCheckBox =
                    itemView.findViewById(
                            R.id.taskCheckBox
                    );

            taskTitleText =
                    itemView.findViewById(
                            R.id.taskTitleText
                    );

            taskDeadlineText =
                    itemView.findViewById(
                            R.id.taskDeadlineText
                    );
        }
    }
}