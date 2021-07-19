package com.codepath.myapplication.Models;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.myapplication.ParseObjects.Exercise;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.HashMap;
import java.util.List;

/**
 * Shows the details of a selected workout.
 */
public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";
    private TextView tvWorkoutName;
    private RatingBar rbRatingDetails;
    private TextView tvTargetAreasDetails;
    private Button btnDetailStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        rbRatingDetails = findViewById(R.id.rbRatingDetails);
        tvTargetAreasDetails = findViewById(R.id.tvTargetAreasDetails);
        final Workout workout = getIntent().getParcelableExtra("workout");
        tvWorkoutName.setText(workout.getWorkoutName());
        rbRatingDetails.setRating((float) workout.getDifficulty());
        tvTargetAreasDetails.setText(workout.getTargetArea());
        btnDetailStart = findViewById(R.id.btnDetailStart);
        btnDetailStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseQuery<Exercise> query = ParseQuery.getQuery(Exercise.class);
                final HashMap<String, Exercise> exercisesMap = new HashMap<>();
                query.addAscendingOrder("name");
                query.findInBackground(new FindCallback<Exercise>() {
                    @Override
                    public void done(List<Exercise> exercises, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting exercises", e);
                            Toast.makeText(DetailActivity.this, "Issue with getting exercises", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (int i = 0; i < exercises.size(); i++) {
                            final Exercise currentExercise = exercises.get(i);
                            exercisesMap.put(currentExercise.getExerciseName(), currentExercise);
                        }
                        final Intent i = new Intent(DetailActivity.this, WorkoutActivity.class);
                        i.putExtra("workout", workout);
                        i.putExtra("exercises", exercisesMap);
                        startActivity(i);
                    }
                });
            }
        });
    }
}