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

public class TraineeInvitationAdapter extends RecyclerView.Adapter<TraineeInvitationAdapter.ViewHolder> {

    Context context;
    ArrayList<TrainerInvitationItem> list;
    int traineeId;
    DatabaseHelper db;

    public TraineeInvitationAdapter(Context context, ArrayList<TrainerInvitationItem> list,
                                    int traineeId, DatabaseHelper db) {
        this.context = context;
        this.list = list;
        this.traineeId = traineeId;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_trainer_invitation, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        TrainerInvitationItem item = list.get(pos);

        h.trainerName.setText(item.trainerName);
        h.trainerEmail.setText(item.trainerEmail);

        h.btnAccept.setOnClickListener(v -> {
            boolean success = db.acceptTrainerInvitation(
                    item.invitationId, item.trainerId, traineeId
            );

            if (success) {
                Toast.makeText(context, "Accepted invitation from " + item.trainerName,
                        Toast.LENGTH_SHORT).show();
                list.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, list.size());

                if (list.isEmpty() && context instanceof TraineeInvitationsActivity) {
                    ((TraineeInvitationsActivity) context).refreshInvitations();
                }
            } else {
                Toast.makeText(context, "Failed to accept invitation", Toast.LENGTH_SHORT).show();
            }
        });

        h.btnReject.setOnClickListener(v -> {
            db.rejectTrainerInvitation(item.invitationId, item.trainerId, "Not interested");
            Toast.makeText(context, "Rejected invitation from " + item.trainerName,
                    Toast.LENGTH_SHORT).show();
            list.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, list.size());

            if (list.isEmpty() && context instanceof TraineeInvitationsActivity) {
                ((TraineeInvitationsActivity) context).refreshInvitations();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView trainerName, trainerEmail;
        Button btnAccept, btnReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trainerName = itemView.findViewById(R.id.tvTrainerName);
            trainerEmail = itemView.findViewById(R.id.tvTrainerEmail);
            btnAccept = itemView.findViewById(R.id.btnAcceptInvitation);
            btnReject = itemView.findViewById(R.id.btnRejectInvitation);
        }
    }
}