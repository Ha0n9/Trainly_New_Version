package com.example.trainly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder> {

    List<WorkoutHistoryItem> list;

    public WorkoutHistoryAdapter(List<WorkoutHistoryItem> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTitle, tvSummary;

        ViewHolder(View v) {
            super(v);
            tvDate = v.findViewById(R.id.tvHistoryDate);
            tvTitle = v.findViewById(R.id.tvHistoryTitle);
            tvSummary = v.findViewById(R.id.tvHistorySummary);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        WorkoutHistoryItem item = list.get(position);

        h.tvDate.setText(item.date);
        h.tvTitle.setText(item.title);
        h.tvSummary.setText(item.summary);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
