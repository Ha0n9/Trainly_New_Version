package com.example.trainly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StartWorkoutAdapter extends RecyclerView.Adapter<StartWorkoutAdapter.ViewHolder> {

    List<Exercise> list;

    public StartWorkoutAdapter(List<Exercise> list) {
        this.list = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        CheckBox cbDone;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvStartExName);
            tvDetails = v.findViewById(R.id.tvStartExDetails);
            cbDone = v.findViewById(R.id.cbStartExDone);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_start_workout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        Exercise ex = list.get(position);
        h.tvName.setText(ex.name);
        h.tvDetails.setText(ex.details);
        h.cbDone.setChecked(false);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
