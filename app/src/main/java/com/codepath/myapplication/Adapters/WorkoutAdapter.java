package com.codepath.myapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.codepath.myapplication.Activities.LoginActivity;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ViewHolder> {
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
        final View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull WorkoutAdapter.ViewHolder holder, int position) {
        final Workout workout = workouts.get(position);
        holder.bind(workout);
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public interface OnClickListener {
        void onItemClicked(int position);

        void onItemLongClicked(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView etWorkoutItem;
        private final ImageView ivExerciseImage;

        public ViewHolder(View itemView) {
            super(itemView);
            etWorkoutItem = itemView.findViewById(R.id.etWorkoutItem);
            ivExerciseImage = itemView.findViewById(R.id.ivExerciseImage);
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
            etWorkoutItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
            if (LoginActivity.isUserOnline()) {
                final MultiTransformation multiLeft = new MultiTransformation(
                        new CenterCrop(),
                        new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT));
                Glide.with(context).load(workout.getImageURL()).apply(bitmapTransform(multiLeft)).into(ivExerciseImage);
            } else {
                etWorkoutItem.setBackgroundResource(R.drawable.custom_border);
            }
        }
    }
}
