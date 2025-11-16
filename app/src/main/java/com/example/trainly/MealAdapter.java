package com.example.trainly;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    ArrayList<MealItem> list;

    public MealAdapter(ArrayList<MealItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealItem item = list.get(position);
        holder.name.setText(item.getName());
        holder.calories.setText(item.getCalories() + " kcal");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int pos) {
        list.remove(pos);
        notifyItemRemoved(pos);
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView name, calories;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvMealName);
            calories = itemView.findViewById(R.id.tvMealCalories);
        }
    }
}
