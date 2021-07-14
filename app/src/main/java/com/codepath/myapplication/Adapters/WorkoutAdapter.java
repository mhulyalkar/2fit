package com.codepath.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.myapplication.R;
import com.codepath.myapplication.Workout;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder>{
    private final List<Workout> workouts;
    private final Context context;
    private final OnClickListener clickListener;
    public WorkoutAdapter(List<Workout> workouts, Context context, OnClickListener clickListener) {
        this.workouts = workouts;
        this.context = context;
        this.clickListener = clickListener;
    }
    @NotNull
    @Override
    public WorkoutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull WorkoutAdapter.ViewHolder holder, int position) {
        final Workout workout = workouts.get(position);
        holder.bind(workout);
    }
    public interface OnClickListener {
        void onItemClicked(int position);
    }
    @Override
    public int getItemCount() {
        return workouts.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView etWorkoutItem;
        public ViewHolder(View itemView) {
            super(itemView);
            etWorkoutItem = itemView.findViewById(R.id.etWorkoutItem);
        }
        public void bind(Workout workout) {
            final String name = workout.getWorkoutName();
            etWorkoutItem.setText(name);
            etWorkoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClicked(getAdapterPosition());
                }
            });
        }
    }
}
