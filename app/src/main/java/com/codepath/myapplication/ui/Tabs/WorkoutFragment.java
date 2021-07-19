package com.codepath.myapplication.ui.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.myapplication.Adapters.WorkoutAdapter;
import com.codepath.myapplication.Models.DetailActivity;
import com.codepath.myapplication.Models.WorkoutActivity;
import com.codepath.myapplication.ParseObjects.Exercise;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * RecyclerView of all Workout objects on Parse Server.
 */
public class WorkoutFragment extends Fragment {
    private static final String TAG = "WorkoutFragment";
    private WorkoutAdapter nAdapter;
    private List<Workout> allWorkouts;
    private RecyclerView rvWorkouts;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
        rvWorkouts = rootView.findViewById(R.id.rvWorkout);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getActivity()));
        allWorkouts = new ArrayList<>();
        final WorkoutAdapter.OnClickListener onClickListener = new WorkoutAdapter.OnClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //Skips the detail page
                final ParseQuery<Exercise> query = ParseQuery.getQuery(Exercise.class);
                final HashMap<String, Exercise> exercisesMap = new HashMap<>();
                query.addAscendingOrder("name");
                query.findInBackground(new FindCallback<Exercise>() {
                    @Override
                    public void done(List<Exercise> exercises, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting exercises", e);
                            Toast.makeText(getActivity(), "Issue with getting exercises", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < exercises.size(); i++) {
                            final Exercise currentExercise = exercises.get(i);
                            exercisesMap.put(currentExercise.getExerciseName(), currentExercise);
                        }
                        final Intent i = new Intent(getActivity(), WorkoutActivity.class);
                        i.putExtra("workout", allWorkouts.get(position));
                        i.putExtra("exercises", exercisesMap);
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onItemClicked(int position) {
                final Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("workout", allWorkouts.get(position));
                startActivity(i);
            }
        };
        nAdapter = new WorkoutAdapter(allWorkouts, getActivity(), onClickListener);
        rvWorkouts.setAdapter(nAdapter);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ParseQuery<Workout> query = ParseQuery.getQuery(Workout.class);
        query.addAscendingOrder("difficulty");
        query.findInBackground(new FindCallback<Workout>() {
            @Override
            public void done(List<Workout> workouts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting workouts", e);
                    Toast.makeText(getActivity(), "Issue with getting workouts", Toast.LENGTH_SHORT).show();
                    return;
                }
                allWorkouts.addAll(workouts);
                nAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }
}
