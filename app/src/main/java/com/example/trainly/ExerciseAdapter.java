package com.example.trainly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    List<Exercise> list;

    public ExerciseAdapter(List<Exercise> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvExerciseName);
            tvDetails = v.findViewById(R.id.tvExerciseDetails);
        }
    }

    @Override
    public ExerciseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Exercise ex = list.get(position);
        h.tvName.setText(ex.name);
        h.tvDetails.setText(ex.details);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
