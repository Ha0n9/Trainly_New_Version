package com.example.trainly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {

    List<Workout> list;
    OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onWorkoutClick(Workout workout);
    }

    public WorkoutAdapter(List<Workout> list, OnItemClickListener listener) {
        this.list = list;
        this.clickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        CheckBox cbDone;

        public ViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvWorkoutTitle);
            tvSubtitle = v.findViewById(R.id.tvWorkoutSubtitle);
            cbDone = v.findViewById(R.id.cbWorkoutDone);
        }
    }

    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Workout w = list.get(position);
        h.tvTitle.setText(w.title);
        h.tvSubtitle.setText(w.subtitle);
        h.cbDone.setChecked(w.completed);
        h.cbDone.setEnabled(false); // Read-only

        // Click entire card to start workout
        h.itemView.setOnClickListener(v -> {
            if (clickListener != null && !w.completed) {
                clickListener.onWorkoutClick(w);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}