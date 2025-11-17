package com.example.trainly;

import android.content.Context;
import android.database.Cursor;
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

        // Reset UI tránh bị reuse trạng thái cũ
        h.btnRequest.setVisibility(View.VISIBLE);
        h.btnRequest.setEnabled(true);
        h.status.setVisibility(View.GONE);

        int trainerId = t.id;

        // 1. Kiểm tra trainee đã có trainer chưa
        boolean hasTrainer = db.hasTrainer(traineeId);

        // 2. Lấy danh sách request để tìm trạng thái với trainer này
        String requestStatus = null;

        Cursor c = db.getTraineeRequests(traineeId);
        if (c != null) {
            while (c.moveToNext()) {
                // Fix: Lấy trainer_id từ JOIN query
                // Query trả về: r.id, u.name AS trainer_name, r.status, r.reason
                // Cần thêm trainer_id vào query
                int ridCol = c.getColumnIndex("id");

                // Tạm thời check bằng cách lấy request details
                Cursor detailCursor = db.getReadableDatabase().rawQuery(
                        "SELECT trainer_id, status FROM trainer_requests WHERE id=?",
                        new String[]{String.valueOf(c.getInt(0))}
                );

                if (detailCursor.moveToFirst()) {
                    int trainerIdFromRequest = detailCursor.getInt(0);
                    if (trainerIdFromRequest == trainerId) {
                        requestStatus = detailCursor.getString(1);
                        detailCursor.close();
                        break;
                    }
                }
                detailCursor.close();
            }
            c.close();
        }

        // ===== STATE HANDLING =====

        // ---- ACCEPTED ----
        if (hasTrainer && "accepted".equalsIgnoreCase(requestStatus)) {
            h.btnRequest.setVisibility(View.GONE);
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Accepted");
            h.status.setTextColor(context.getResources().getColor(android.R.color.holo_green_light));
            return;
        }

        // ---- PENDING ----
        if ("pending".equalsIgnoreCase(requestStatus)) {
            h.btnRequest.setEnabled(false);
            h.btnRequest.setText("Pending");
            h.status.setVisibility(View.VISIBLE);
            h.status.setText("Waiting for response");
            h.status.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
            return;
        }

        // ---- REJECTED ----
        if ("rejected".equalsIgnoreCase(requestStatus)) {
            h.btnRequest.setText("Request Again");
        }

        // ---- DEFAULT (NO REQUEST YET OR CAN REQUEST AGAIN) ----
        h.btnRequest.setOnClickListener(v -> {
            boolean success = db.sendTrainerRequest(traineeId, trainerId);

            if (success) {
                Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show();
                notifyItemChanged(pos);
            } else {
                Toast.makeText(context, "You already sent a request!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, btnRequest, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvTrainerName);
            btnRequest = itemView.findViewById(R.id.btnRequestTrainer);
            status = itemView.findViewById(R.id.tvRequestStatus);
        }
    }
}