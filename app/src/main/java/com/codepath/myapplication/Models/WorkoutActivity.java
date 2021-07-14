package com.codepath.myapplication.Models;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.codepath.myapplication.R;

/**
 * Screen which shows all the exercises of a workout.
 */
public class WorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
    }
}