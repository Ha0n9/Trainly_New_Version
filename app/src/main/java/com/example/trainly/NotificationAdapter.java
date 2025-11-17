package com.example.trainly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationItem> list;

    public NotificationAdapter(Context context, ArrayList<NotificationItem> list) {
        this.context = context;
        this.list = list;
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
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView msg, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            msg = itemView.findViewById(R.id.tvMessage);
            time = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
