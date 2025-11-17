package com.example.trainly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerTraineeListAdapter extends RecyclerView.Adapter<TrainerTraineeListAdapter.ViewHolder> {

    Context context;
    ArrayList<TrainerRequestItem> list;
    int trainerId;

    public TrainerTraineeListAdapter(Context context, ArrayList<TrainerRequestItem> list, int trainerId) {
        this.context = context;
        this.list = list;
        this.trainerId = trainerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trainer_trainee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TrainerRequestItem t = list.get(pos);

        h.name.setText(t.name);
        h.email.setText(t.email);

        // Click to view trainee detail
        h.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrainerViewTraineeDetailActivity.class);
            intent.putExtra("trainerId", trainerId);
            intent.putExtra("traineeId", t.traineeId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvTraineeName);
            email = itemView.findViewById(R.id.tvTraineeEmail);
        }
    }
}