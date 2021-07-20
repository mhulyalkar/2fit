package com.codepath.myapplication.Models;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.myapplication.ParseObjects.Exercise;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Screen which shows all the exercises of a workout.
 */
public class WorkoutActivity extends AppCompatActivity {
    private static final int TYPE_MAIN = 1;
    private static final int TYPE_MINI = 2;
    private static final String TAG = "WorkoutActivity";

    private List<Exercise> workoutPlan;
    private Map<String, Exercise> exercisesMap = new HashMap<>();
    private Workout workout;
    private TextView tvTimer;
    private TextView tvExercise;
    private TextView tvExerciseTimer;
    private ImageButton ibPause;
    private CustomCountDownTimer mainTimer;
    private CustomCountDownTimer exerciseTimer;
    private Button btnNext;
    private boolean isPaused = false;
    private int index;

    public void timerStart(long timeLengthMilli, int type) {
        if (type == TYPE_MAIN) {
            mainTimer = new CustomCountDownTimer(timeLengthMilli, 1000, type);
            mainTimer.start();
        } else if (type == TYPE_MINI) {
            exerciseTimer = new CustomCountDownTimer(timeLengthMilli, 1000, type);
            exerciseTimer.start();
        } else {
            Log.e(TAG, "Timer type not recognized in timerStart(), type: " + type);
            Toast.makeText(WorkoutActivity.this, "Timer did not load properly", Toast.LENGTH_SHORT).show();
        }
    }

    public void timerPause() {
        mainTimer.cancel();
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
        }
    }

    public void timerResume() {
        timerStart(mainTimer.getMilliLeft(), mainTimer.getType());
        if (exerciseTimer != null) {
            timerStart(exerciseTimer.getMilliLeft(), exerciseTimer.getType());
        }
    }

    public void addTimePrompt() {
        //Popup window with buttons to reset the timer
        final View pView;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PopupWindow pop = new PopupWindow(inflater.inflate(R.layout.popup_add_time, null));
        pView = pop.getContentView();
        final int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        final int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(pView, width, height, focusable);
        popupWindow.showAtLocation(pView, Gravity.CENTER, 0, 0);
        final Button btnOneMin, btnTwoMin, btnFiveMin, btnTenMin, btnCustomMin, btnEndWorkout;
        btnOneMin = pView.findViewById(R.id.btnOneMin);
        btnOneMin.setOnClickListener(new CustomOnClickListener(1 , popupWindow));
        btnTwoMin = pView.findViewById(R.id.btnTwoMin);
        btnTwoMin.setOnClickListener(new CustomOnClickListener(2 , popupWindow));
        btnFiveMin = pView.findViewById(R.id.btnFiveMin);
        btnFiveMin.setOnClickListener(new CustomOnClickListener(5 , popupWindow));
        btnTenMin = pView.findViewById(R.id.btnTenMin);
        btnTenMin.setOnClickListener(new CustomOnClickListener(10 , popupWindow));
        btnCustomMin = pView.findViewById(R.id.btnCustomMin);
        btnCustomMin.setOnClickListener(new CustomOnClickListener(20 , popupWindow));
        btnEndWorkout = pView.findViewById(R.id.btnEndWorkout);
        btnEndWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent i = new Intent(WorkoutActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    public List<Exercise> generateWorkout() {
        //randomly generate a series of exercises based on a workout
        final List<Object> exercises = new ArrayList<>(workout.getExercises());
        final List<Exercise> workoutPlan = new ArrayList<>();
        int randomInt;
        for (int i = 0; i < workout.getExercises().size(); i++) {
            randomInt = (int) (Math.random() * exercises.size());
            final String exerciseName = (String) exercises.get(randomInt);
            final Exercise exerciseObject = exercisesMap.get(exerciseName);
            if (exerciseObject == null) {
                Log.e(TAG, "Couldn't find: " + exerciseName);
                Toast.makeText(WorkoutActivity.this, "Couldn't find: " + exerciseName, Toast.LENGTH_SHORT).show();
            }
            workoutPlan.add(exercisesMap.get(exerciseName));
            exercises.remove(randomInt);
        }
        return workoutPlan;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        workout = getIntent().getParcelableExtra("workout");
        exercisesMap = (Map<String, Exercise>) getIntent().getSerializableExtra("exercises");
        workoutPlan = generateWorkout();
        workoutPlan.add(index, exercisesMap.get("Jumping Jacks"));

        ibPause = findViewById(R.id.ibPause);
        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPaused) {
                    timerPause();
                    isPaused = true;
                    ibPause.setImageResource(R.drawable.ic_stat_pause);
                } else {
                    timerResume();
                    isPaused = false;
                    ibPause.setImageResource(R.drawable.ic_stat_name);
                }
            }
        });
        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (exerciseTimer.getMilliLeft() > 0) {
                    exerciseTimer.onFinish();
                }
                load();
            }
        });
        tvTimer = findViewById(R.id.tvTimer);
        tvExercise = findViewById(R.id.tvExercise);
        tvExerciseTimer = findViewById(R.id.tvExerciseTimer);
        tvExercise.setText("" + workoutPlan.get(0));
        load();
        timerStart(3 * 1000, TYPE_MAIN);
    }

    public void load() {
        if (index >= workoutPlan.size()) {
            generateWorkout();
            workoutPlan.remove(0);
            index = 0;
        }
        final Exercise exercise = workoutPlan.get(index);
        final int repNumber;
        //Difficulty determines the number of reps of an exercise
        if (workout.getDifficulty() == 1 || workout.getDifficulty() == 2) {
            repNumber = exercise.getBeginnerReps();
        } else if (workout.getDifficulty() == 3 || workout.getDifficulty() == 4) {
            repNumber = exercise.getIntermediateReps();
        } else if (workout.getDifficulty() == 5) {
            repNumber = exercise.getAdvancedReps();
        } else {
            Log.e(TAG, "Difficulty not recognized");
            Toast.makeText(WorkoutActivity.this, "Difficulty not found", Toast.LENGTH_SHORT).show();
            return;
        }
        String reps;
        if (exercise.getIsReps()) {
            reps = "x" + repNumber + " Repetitions";
        } else {
            timerStart(repNumber * 1000, TYPE_MINI);
            tvExerciseTimer.setVisibility(View.VISIBLE);
            reps = repNumber + " seconds";
        }
        tvExercise.setText(workoutPlan.get(index).getExerciseName() + "\n" + reps);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    public class CustomOnClickListener implements View.OnClickListener {
        private int min;
        private PopupWindow popupWindow;

        public CustomOnClickListener(int min, PopupWindow popupWindow) {
            this.min = min;
            this.popupWindow = popupWindow;
        }
        @Override
        public void onClick(View v) {
            timerStart(min * 60000, TYPE_MAIN);
            popupWindow.dismiss();
        }
    }
    /**
     * Custom timer implementation with 2 timer types: TYPE_MAIN, TYPE_MINI.
     * TYPE_MAIN is the main timer which shows how long the user has been working out.
     * TYPE_MINI is a smaller timer which shows represent the time left for a timed exercises (e.g. Plank, Wall Sit).
     */
    public class CustomCountDownTimer extends CountDownTimer {
        private static final int TYPE_MAIN = 1;
        private static final int TYPE_MINI = 2;
        private final int type;
        private long milliLeft;

        public CustomCountDownTimer(long millisInFuture, long countDownInterval, int type) {
            super(millisInFuture + 1000, countDownInterval);
            this.type = type;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            milliLeft = millisUntilFinished;
            final long min = (millisUntilFinished / (1000 * 60));
            final long sec = ((millisUntilFinished / 1000) - min * 60);
            final String secString;
            if (sec < 10) {
                secString = "0" + sec;
            } else {
                secString = sec + "";
            }
            if (type == TYPE_MAIN) {
                tvTimer.setText(min + ":" + secString);
            } else if (type == TYPE_MINI) {
                tvExerciseTimer.setText(min + ":" + secString);
            } else {
                Log.e(TAG, "Timer type not recognized by CustomCountDownTimer, type: " + type);
                Toast.makeText(WorkoutActivity.this, "Timer did not load properly", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish() {
            if (type == TYPE_MAIN) {
                addTimePrompt();
            }
            if (type == TYPE_MINI) {
                tvExerciseTimer.setVisibility(View.INVISIBLE);
            }
        }

        public long getMilliLeft() {
            return milliLeft;
        }

        public int getType() {
            return type;
        }
    }
}