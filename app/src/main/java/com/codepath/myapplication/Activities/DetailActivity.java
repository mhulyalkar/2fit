package com.codepath.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Shows the details of a selected workout.
 */
public class DetailActivity extends AppCompatActivity {
    private TextView tvWorkoutName;
    private RatingBar rbRatingDetails;
    private TextView tvTargetAreasDetails;
    private Button btnDetailStart;
    private ImageView ivDetailPoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvWorkoutName = findViewById(R.id.tvWorkoutName);
        rbRatingDetails = findViewById(R.id.rbDifficultyRating);
        tvTargetAreasDetails = findViewById(R.id.tvTargetAreasDetails);
        final Workout workout = getIntent().getParcelableExtra("workout");
        tvWorkoutName.setText(workout.getWorkoutName());
        rbRatingDetails.setRating((float) workout.getDifficulty());
        tvTargetAreasDetails.setText(workout.getTargetArea());
        btnDetailStart = findViewById(R.id.btnDetailStart);
        ivDetailPoster = findViewById(R.id.ivDetailPoster);
        if (LoginActivity.isUserOnline()) {
            final MultiTransformation multiLeft = new MultiTransformation(
                    new CenterCrop());
                    new RoundedCornersTransformation(25, 0, RoundedCornersTransformation.CornerType.BOTTOM_LEFT);
            Glide.with(DetailActivity.this).load(workout.getImageURL()).apply(new RequestOptions().transform(new CenterCrop(), new RoundedCorners(30))).into(ivDetailPoster);
        }
        btnDetailStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(DetailActivity.this, WorkoutActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // these two lines makes sure the Back button won't work
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("workout", workout);
                startActivity(i);
                Animatoo.animateZoom(DetailActivity.this);
            }
        });
    }
}