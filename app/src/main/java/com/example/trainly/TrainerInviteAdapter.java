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

public class TrainerInviteAdapter extends RecyclerView.Adapter<TrainerInviteAdapter.ViewHolder> {
    Context context;
    ArrayList<TraineeItem> list;
    int trainerId;
    DatabaseHelper db;

    public TrainerInviteAdapter(Context context, ArrayList<TraineeItem> list, int trainerId, DatabaseHelper db) {
        this.context = context;
        this.list = list;
        this.trainerId = trainerId;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_invite_trainee, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TraineeItem t = list.get(pos);

        h.name.setText(t.name);
        h.info.setText("Age " + t.age + " • " + t.height + "cm • " + t.weight + "kg");
        h.email.setText(t.email);

        h.btnInvite.setOnClickListener(v -> {
            boolean success = db.trainerInviteTrainee(trainerId, t.id);

            if (success) {
                Toast.makeText(context, "Invitation sent to " + t.name, Toast.LENGTH_SHORT).show();
                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, list.size());

                // Refresh activity to show empty state if needed
                if (list.isEmpty() && context instanceof TrainerInviteTraineeActivity) {
                    ((TrainerInviteTraineeActivity) context).refreshList();
                }

            } else {
                Toast.makeText(context, "Already sent invitation to this trainee", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, info, email;
        Button btnInvite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTraineeName);
            info = itemView.findViewById(R.id.tvTraineeInfo);
            email = itemView.findViewById(R.id.tvTraineeEmail);
            btnInvite = itemView.findViewById(R.id.btnInviteTrainee);
        }
    }
}