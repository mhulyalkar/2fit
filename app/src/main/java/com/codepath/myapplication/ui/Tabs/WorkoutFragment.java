package com.codepath.myapplication.ui.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codepath.myapplication.Activities.DetailActivity;
import com.codepath.myapplication.Activities.LoginActivity;
import com.codepath.myapplication.Activities.WorkoutActivity;
import com.codepath.myapplication.Adapters.WorkoutAdapter;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView of all Workout objects on Parse Server.
 */
public class WorkoutFragment extends Fragment {
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
                final Intent i = new Intent(getActivity(), WorkoutActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // these two lines makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("workout", allWorkouts.get(position));
                startActivity(i);
                Animatoo.animateFade(getActivity());
            }

            @Override
            public void onItemClicked(int position) {
                final Intent i = new Intent(getActivity(), DetailActivity.class);
                i.putExtra("workout", allWorkouts.get(position));
                startActivity(i);
                Animatoo.animateSlideDown(getActivity());
            }
        };
        nAdapter = new WorkoutAdapter(allWorkouts, getActivity(), onClickListener);
        rvWorkouts.setAdapter(nAdapter);
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getActivity()));
        allWorkouts.clear();
        allWorkouts.addAll(LoginActivity.getWorkoutList());
        nAdapter.notifyDataSetChanged();
        return rootView;
    }
}
