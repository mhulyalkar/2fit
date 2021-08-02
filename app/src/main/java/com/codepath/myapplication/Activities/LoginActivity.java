package com.codepath.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.codepath.myapplication.ParseObjects.Exercise;
import com.codepath.myapplication.ParseObjects.WeeklyReport;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Where user can log in or sign up.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private final static HashMap<String, Exercise> exercisesMap = new HashMap<>();
    private final static List<Workout> workoutList = new ArrayList<>();
    private static ParseUser currentUser;
    private static WeeklyReport currentWeeklyReport;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignUp;
    private Button btnLogin;
    private Spinner spGender;
    private Button btnSkipLogIn;

    public static ParseUser getCurrentUser() {
        return currentUser;
    }

    public static HashMap<String, Exercise> getExercisesMap() {
        return exercisesMap;
    }

    public static WeeklyReport getCurrentWeeklyReport() {
        return currentWeeklyReport;
    }

    public static void setCurrentWeeklyReport(WeeklyReport weeklyReport) {
        currentWeeklyReport = weeklyReport;
    }

    public static List<Workout> getWorkoutList() {
        return workoutList;
    }

    public static void removeCurrentWeeklyReport() {
        LoginActivity.currentWeeklyReport = null;
    }

    public static void removeCurrentUser() {
        currentUser = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        spGender = findViewById(R.id.spGender);

        final ArrayAdapter<String> myAdapter = new ArrayAdapter<>(LoginActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.spgender_content));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGender.setAdapter(myAdapter);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                signUp(username, password);
            }
        });
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
        btnSkipLogIn = findViewById(R.id.btnSkipLogIn);
        btnSkipLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercisesMap.isEmpty()) {
                    final ParseQuery<Exercise> queryExercises = ParseQuery.getQuery(Exercise.class);
                    queryExercises.fromLocalDatastore();
                    queryExercises.findInBackground(new FindCallback<Exercise>() {
                        public void done(List<Exercise> exercises,
                                         ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issue with getting exercises offline", e);
                                Toast.makeText(LoginActivity.this, "Issue with getting exercises offline", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (int i = 0; i < exercises.size(); i++) {
                                final Exercise currentExercise = exercises.get(i);
                                exercisesMap.put(currentExercise.getExerciseName(), currentExercise);
                            }
                            if (workoutList.size() == 0) {
                                final ParseQuery<Workout> queryWorkouts = ParseQuery.getQuery(Workout.class);
                                queryWorkouts.fromLocalDatastore();
                                queryWorkouts.addAscendingOrder("difficulty");
                                queryWorkouts.findInBackground(new FindCallback<Workout>() {
                                    @Override
                                    public void done(List<Workout> workouts, ParseException e) {
                                        if (e != null) {
                                            Log.e(TAG, "Issue with getting workouts offline", e);
                                            Toast.makeText(LoginActivity.this, "Issue with getting workouts offline", Toast.LENGTH_SHORT).show();
                                            return;
                                        }
                                        workoutList.addAll(workouts);
                                        final Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(i);
                                        Animatoo.animateCard(LoginActivity.this);
                                    }
                                });
                            } else {
                                final Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                Animatoo.animateCard(LoginActivity.this);
                            }
                        }
                    });
                } else {
                    final Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i);
                    Animatoo.animateCard(LoginActivity.this);
                }
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with log in", e);
                    Toast.makeText(LoginActivity.this, "Issue with logging in", Toast.LENGTH_SHORT).show();
                    btnSkipLogIn.setVisibility(View.VISIBLE);
                    return;
                }
                currentUser = user;
                final ParseQuery<Workout> query = ParseQuery.getQuery(Workout.class);
                query.addAscendingOrder("difficulty");
                query.findInBackground(new FindCallback<Workout>() {
                    @Override
                    public void done(List<Workout> workouts, ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Issue with getting workouts", e);
                            Toast.makeText(LoginActivity.this, "Issue with getting workouts", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        workoutList.addAll(workouts);
                        queryWeeklyReport();
                    }
                });
            }
        });
    }

    private void signUp(String username, String password) {
        final ParseUser user = new ParseUser();
        final WeeklyReport weeklyReport = new WeeklyReport();
        weeklyReport.setDuration(0);
        weeklyReport.setDaysInARow(0);
        weeklyReport.setWeeklyCaloriesBurned(0);
        user.setUsername(username);
        user.setPassword(password);
        user.put("weeklyReport", weeklyReport);
        weeklyReport.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving Weekly Report" + e);
                    Toast.makeText(LoginActivity.this, "Error While saving Weekly Report", Toast.LENGTH_SHORT).show();
                    return;
                }
                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            currentUser = user;
                            Toast.makeText(LoginActivity.this, "Sign up was successful", Toast.LENGTH_SHORT).show();
                            loginUser(username, password);
                        } else {
                            Log.e(TAG, "Sign up was not successful", e);
                            Toast.makeText(LoginActivity.this, "Sign up was not successful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void goMainActivity() {
        final ParseQuery<Exercise> query = ParseQuery.getQuery(Exercise.class);
        query.addAscendingOrder("name");
        query.findInBackground(new FindCallback<Exercise>() {
            @Override
            public void done(List<Exercise> exercises, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting exercises", e);
                    Toast.makeText(LoginActivity.this, "Issue with getting exercises", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseObject.pinAllInBackground(exercises);
                for (int i = 0; i < exercises.size(); i++) {
                    final Exercise currentExercise = exercises.get(i);
                    exercisesMap.put(currentExercise.getExerciseName(), currentExercise);
                }
                final Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                Animatoo.animateCard(LoginActivity.this);
            }
        });
    }

    private void queryWeeklyReport() {
        final ParseQuery<WeeklyReport> query = ParseQuery.getQuery(WeeklyReport.class);
        query.getInBackground(currentUser.getParseObject("weeklyReport").getObjectId(), new GetCallback<WeeklyReport>() {
            public void done(WeeklyReport weeklyReport, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting weeklyReport", e);
                    Toast.makeText(LoginActivity.this, "Issue with getting weekly report", Toast.LENGTH_SHORT).show();
                    return;
                }
                weeklyReport.pinInBackground("weeklyReport");
                currentWeeklyReport = weeklyReport;
                if (currentWeeklyReport.getLastWorkoutDate() != null) {
                    //reset WeeklyReport if its a new week
                    final Date lastWorkoutDate = currentWeeklyReport.getLastWorkoutDate();
                    final Date nextMondayDate = new Date(lastWorkoutDate.getYear(),
                            lastWorkoutDate.getMonth(),
                            currentWeeklyReport.getLastWorkoutDate().getDate()
                                    + (7 - currentWeeklyReport.getLastWorkoutDate().getDay()));
                    if (nextMondayDate.compareTo(new Date()) <= 0) {
                        currentWeeklyReport.setWeeklyCaloriesBurned(0);
                        currentWeeklyReport.setDuration(0);
                        currentWeeklyReport.setDaysInARow(0);
                        currentWeeklyReport.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Issue with resetting WeeklyReport", e);
                                    Toast.makeText(LoginActivity.this, "Issue with resetting WeeklyReport", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                goMainActivity();
                                Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}