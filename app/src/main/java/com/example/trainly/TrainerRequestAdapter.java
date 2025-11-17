package com.example.trainly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TrainerRequestAdapter extends RecyclerView.Adapter<TrainerRequestAdapter.ViewHolder> {

    Context context;
    ArrayList<TrainerRequestItem> list;
    DatabaseHelper db;
    int trainerId;

    public TrainerRequestAdapter(Context context, ArrayList<TrainerRequestItem> list,
                                 DatabaseHelper db, int trainerId) {
        this.context = context;
        this.list = list;
        this.db = db;
        this.trainerId = trainerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trainer_request, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {

        TrainerRequestItem t = list.get(pos);

        h.name.setText(t.name);
        h.info.setText("Age " + t.age + " • " + t.height + "cm • " + t.weight + "kg");

        h.btnAccept.setOnClickListener(v -> {
            boolean ok = db.acceptTrainerRequest(t.requestId, trainerId, t.traineeId);
            if (ok) {
                Toast.makeText(context, "Accepted!", Toast.LENGTH_SHORT).show();
                list.remove(pos);
                notifyItemRemoved(pos);
            }
        });

        h.btnReject.setOnClickListener(v -> {
            db.rejectTrainerRequest(t.requestId, "Not suitable");
            Toast.makeText(context, "Rejected!", Toast.LENGTH_SHORT).show();
            list.remove(pos);
            notifyItemRemoved(pos);
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, info;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvTraineeName);
            info = itemView.findViewById(R.id.tvTraineeInfo);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
