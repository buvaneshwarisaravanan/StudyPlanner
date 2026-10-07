package com.example.studyplanner;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private Context context;
    private List<String> subjectList;
    private List<String> subjectIdList;

    private FirebaseFirestore db;

    public SubjectAdapter(
            Context context,
            List<String> subjectList,
            List<String> subjectIdList) {

        this.context = context;
        this.subjectList = subjectList;
        this.subjectIdList = subjectIdList;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_subject,
                        parent,
                        false
                );

        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull SubjectViewHolder holder,
            int position) {

        holder.subjectName.setText(
                subjectList.get(position)
        );

        // Tap subject to open tasks
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    TasksActivity.class
            );

            intent.putExtra(
                    "subjectId",
                    subjectIdList.get(position)
            );

            intent.putExtra(
                    "subjectName",
                    subjectList.get(position)
            );

            context.startActivity(intent);
        });

        // Long press to delete subject
        holder.itemView.setOnLongClickListener(v -> {

            String subjectId = subjectIdList.get(position);

            db.collection("subjects")
                    .document(subjectId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        subjectList.remove(position);
                        subjectIdList.remove(position);

                        notifyItemRemoved(position);

                        Toast.makeText(
                                context,
                                "Subject Deleted",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                context,
                                "Delete failed",
                                Toast.LENGTH_SHORT
                        ).show();
                    });

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder
            extends RecyclerView.ViewHolder {

        TextView subjectName;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);

            subjectName = itemView.findViewById(
                    R.id.subjectName
            );
        }
    }
}