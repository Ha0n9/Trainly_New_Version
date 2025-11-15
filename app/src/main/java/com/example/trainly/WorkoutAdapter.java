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

    public WorkoutAdapter(List<Workout> list) {
        this.list = list;
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
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
