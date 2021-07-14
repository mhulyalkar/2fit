package com.codepath.myapplication.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.myapplication.R;
import com.codepath.myapplication.Workout;

/**
 * Shows the details of a selected workout.
 */
public class DetailActivity extends AppCompatActivity {
    private TextView tvWorkoutName;
    private RatingBar rbRatingDetails;
    private TextView tvTargetAreasDetails;
    //TODO: Finish Adding imageView and Button functionality
    private ImageView ivDetailPoster;
    private Button btnDetailSubmit;

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
    }
}