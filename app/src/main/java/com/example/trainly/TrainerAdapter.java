package com.example.trainly;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {

    Context context;
    ArrayList<TrainerItem> list;
    DatabaseHelper db;
    int traineeId;

    public TrainerAdapter(Context context, ArrayList<TrainerItem> list, int traineeId, DatabaseHelper db) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.traineeId = traineeId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trainer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TrainerItem t = list.get(pos);

        h.name.setText(t.name);

        h.btnRequest.setOnClickListener(v -> {
            boolean exists = db.sendTrainerRequest(traineeId, t.id);

            if (exists) {
                Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "You already sent a request!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, btnRequest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTrainerName);
            btnRequest = itemView.findViewById(R.id.btnRequestTrainer);
        }
    }
}
