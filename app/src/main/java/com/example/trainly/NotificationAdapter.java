package com.example.trainly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationItem> list;
    DatabaseHelper db;

    public NotificationAdapter(Context context, ArrayList<NotificationItem> list, DatabaseHelper db) {
        this.context = context;
        this.list = list;
        this.db = db;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        NotificationItem n = list.get(pos);

        h.msg.setText(n.message);

        String time = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                .format(new Date(n.timestamp));

        h.time.setText(time);

        // Show unread indicator
        if (n.isRead == 0) {
            h.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.card_dark));
            h.msg.setTextColor(context.getResources().getColor(R.color.text_primary));
        } else {
            h.cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            h.msg.setTextColor(context.getResources().getColor(R.color.text_secondary));
        }

        // Mark as read on click
        h.itemView.setOnClickListener(v -> {
            if (n.isRead == 0) {
                db.markNotificationAsRead(n.id);
                n.isRead = 1;
                notifyItemChanged(pos);
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView msg, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView;
            msg = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}