package com.example.trainly;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerProgressAdapter extends RecyclerView.Adapter<TrainerProgressAdapter.ViewHolder> {

    Context context;
    ArrayList<TraineeProgressItem> list;
    int trainerId;

    public TrainerProgressAdapter(Context context, ArrayList<TraineeProgressItem> list, int trainerId) {
        this.context = context;
        this.list = list;
        this.trainerId = trainerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trainee_progress, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TraineeProgressItem item = list.get(pos);

        h.name.setText(item.name);
        h.progressText.setText(item.getProgressText() + " workouts");
        h.percentage.setText(item.getCompletionPercentage() + "%");
        h.progressBar.setProgress(item.getCompletionPercentage());

        // Set progress bar color based on completion (FIXED: Compatible với API 21+)
        int percent = item.getCompletionPercentage();
        int colorResId;

        if (percent >= 80) {
            colorResId = android.R.color.holo_green_light;
        } else if (percent >= 50) {
            colorResId = R.color.primary_orange;
        } else {
            colorResId = android.R.color.holo_red_light;
        }

        // Use ContextCompat for backward compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            h.progressBar.setProgressTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(context, colorResId))
            );
        } else {
            // Fallback for older versions
            h.progressBar.getProgressDrawable().setColorFilter(
                    ContextCompat.getColor(context, colorResId),
                    android.graphics.PorterDuff.Mode.SRC_IN
            );
        }

        // Click to view detail
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrainerViewTraineeDetailActivity.class);
            intent.putExtra("trainerId", trainerId);
            intent.putExtra("traineeId", item.traineeId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, progressText, percentage;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTraineeName);
            progressText = itemView.findViewById(R.id.tvProgressText);
            percentage = itemView.findViewById(R.id.tvPercentage);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}