package com.example.trainly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<WorkoutHistoryItem> historyList;

    public WorkoutHistoryAdapter(Context context, ArrayList<WorkoutHistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        WorkoutHistoryItem item = historyList.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDate.setText(item.getDate());
        holder.tvSummary.setText(item.getSummary());

        // Icon status
        if (item.isCompleted()) {
            holder.ivStatus.setImageResource(R.drawable.ic_history_done);
        } else {
            holder.ivStatus.setImageResource(R.drawable.ic_history_pending);
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivStatus;
        TextView tvTitle, tvDate, tvSummary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivStatus = itemView.findViewById(R.id.ivStatusIcon);
            tvTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvSummary = itemView.findViewById(R.id.tvHistorySummary);
        }
    }
}
