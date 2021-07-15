package com.codepath.myapplication.Models;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.myapplication.R;
import com.codepath.myapplication.Workout;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen which shows all the exercises of a workout.
 */
public class WorkoutActivity extends AppCompatActivity {
    private final Handler timerHandler = new Handler();
    private List<String> workoutPlan;
    private Workout workout;
    private TextView tvTimer;
    private TextView tvExercise;
    private long startTime = 0;
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            tvTimer.setText(String.format("%d:%02d", minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    public List<String> generateWorkout() {
        List<Object> exercises = workout.getExercises();
        List<String> workoutPlan = new ArrayList<>();

        int randomInt;
        for (int i = 0; i < workout.getExercises().size(); i++) {
            randomInt = (int) (Math.random() * exercises.size());
            workoutPlan.add((String) exercises.get(randomInt));
            exercises.remove(randomInt);
        }
        return workoutPlan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        workout = getIntent().getParcelableExtra("workout1");
        workoutPlan.add("Jumping Jacks");
        workoutPlan = generateWorkout();
        tvTimer = findViewById(R.id.tvTimer);
        tvExercise = findViewById(R.id.tvExercise);
        tvExercise.setText("" + workoutPlan.get(0));
        timerHandler.removeCallbacks(timerRunnable);
        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }
}