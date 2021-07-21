package com.codepath.myapplication.Models;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;

/**
 * Shows the details of a selected workout.
 */
public class DetailActivity extends AppCompatActivity {
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
                final Intent i = new Intent(DetailActivity.this, WorkoutActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // these two lines makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("workout", workout);
                startActivity(i);
            }
        });
    }
}