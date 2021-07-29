package com.codepath.myapplication.Activities;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codepath.myapplication.ParseObjects.Exercise;
import com.codepath.myapplication.ParseObjects.WeeklyReport;
import com.codepath.myapplication.ParseObjects.Workout;
import com.codepath.myapplication.R;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Screen which shows all the exercises of a workout.
 */
public class WorkoutActivity extends AppCompatActivity {
    private static final String TAG = "WorkoutActivity";
    private List<Exercise> workoutPlan;
    private Map<String, Exercise> exercisesMap;
    private Workout workout;
    private TextView tvTimer;
    private TextView tvExercise;
    private TextView tvExerciseTimer;
    private SparkButton btnPause;
    private CustomCountDownTimer mainTimer;
    private CustomCountDownTimer exerciseTimer;
    private Button btnNext;
    private ImageButton btnExerciseVideo;
    private boolean isPaused = false;
    private int index;
    private int totalExerciseTimeInMinutes = 0;

    private void timerStart(long timeLengthMilli, TimerType type) {
        if (type == TimerType.MAIN) {
            mainTimer = new CustomCountDownTimer(timeLengthMilli, 1000, type);
            mainTimer.start();
        } else if (type == TimerType.MINI) {
            exerciseTimer = new CustomCountDownTimer(timeLengthMilli, 1000, type);
            exerciseTimer.start();
        } else {
            Log.e(TAG, "Timer type not recognized in timerStart(), type: " + type);
            Toast.makeText(WorkoutActivity.this, "Timer did not load properly", Toast.LENGTH_SHORT).show();
        }
    }

    private void timerPause() {
        mainTimer.cancel();
        if (exerciseTimer != null) {
            exerciseTimer.cancel();
        }
    }

    private void timerResume() {
        timerStart(mainTimer.getMilliLeft(), mainTimer.getType());
        if (exerciseTimer != null) {
            timerStart(exerciseTimer.getMilliLeft(), exerciseTimer.getType());
        }
    }

    private void addTimePrompt() {
        //Popup window with buttons to reset the timer
        final View promptView;
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PopupWindow pop = new PopupWindow(inflater.inflate(R.layout.popup_add_time, null));
        promptView = pop.getContentView();
        final int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        final int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final boolean focusable = false;
        final PopupWindow popupWindow = new PopupWindow(promptView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(promptView, Gravity.CENTER, 0, 0);
        final Button btnOneMin, btnTwoMin, btnFiveMin, btnTenMin, btnCustomMin, btnEndWorkout;
        btnOneMin = promptView.findViewById(R.id.btnOneMin);
        btnOneMin.setOnClickListener(new CustomOnClickListener(1, popupWindow));
        btnTwoMin = promptView.findViewById(R.id.btnTwoMin);
        btnTwoMin.setOnClickListener(new CustomOnClickListener(2, popupWindow));
        btnFiveMin = promptView.findViewById(R.id.btnFiveMin);
        btnFiveMin.setOnClickListener(new CustomOnClickListener(5, popupWindow));
        btnTenMin = promptView.findViewById(R.id.btnTenMin);
        btnTenMin.setOnClickListener(new CustomOnClickListener(10, popupWindow));
        btnCustomMin = promptView.findViewById(R.id.btnCustomMin);
        btnCustomMin.setOnClickListener(new CustomOnClickListener(20, popupWindow));
        btnEndWorkout = promptView.findViewById(R.id.btnEndWorkout);
        btnEndWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.getCurrentWeeklyReport() != null) {
                    final WeeklyReport currentWeeklyReport = LoginActivity.getCurrentWeeklyReport();
                    final Date currentDate = new Date();
                    final Date lastDate = currentWeeklyReport.getLastWorkoutDate();
                    if (lastDate != null) {
                        final Date nextDate = new Date(lastDate.getYear(), lastDate.getMonth(), lastDate.getDate() + 1);
                        if (nextDate.getYear() == currentDate.getYear()
                                && nextDate.getMonth() == currentDate.getMonth()
                                && nextDate.getDate() == currentDate.getDate()) {
                            currentWeeklyReport.setDaysInARow(currentWeeklyReport.getDaysInARow() + 1);
                        } else if (currentDate.compareTo(nextDate) > 0) {
                            currentWeeklyReport.setDaysInARow(1);
                        }
                    } else {
                        currentWeeklyReport.setDaysInARow(1);
                    }
                    currentWeeklyReport.updateLastWorkoutDate();
                    currentWeeklyReport.setDuration(currentWeeklyReport.getDuration() + totalExerciseTimeInMinutes);
                    final long calsPerMin;
                    if (workout.getDifficulty() == 1 || workout.getDifficulty() == 2) {
                        calsPerMin = 162 / 60;
                    } else if (workout.getDifficulty() == 3 || workout.getDifficulty() == 4) {
                        calsPerMin = ((216 + 162) / 2) / 60;
                    } else {
                        calsPerMin = 216 / 60;
                    }
                    currentWeeklyReport.setWeeklyCaloriesBurned(currentWeeklyReport.getWeeklyCaloriesBurned() + ((int) (calsPerMin * totalExerciseTimeInMinutes)));
                    currentWeeklyReport.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Issue with updating WeeklyReport", e);
                                Toast.makeText(WorkoutActivity.this, "Issue with updating WeeklyReport", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            final Intent i = new Intent(WorkoutActivity.this, MainActivity.class);
                            popupWindow.dismiss();
                            startActivity(i);
                        }
                    });
                } else {
                    final Intent i = new Intent(WorkoutActivity.this, MainActivity.class);
                    popupWindow.dismiss();
                    startActivity(i);
                }
            }
        });
    }

    private List<Exercise> generateWorkout() {
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
        exercisesMap = LoginActivity.getExercisesMap();
        workoutPlan = generateWorkout();
        workoutPlan.add(index, exercisesMap.get("Jumping Jacks"));
        btnExerciseVideo = findViewById(R.id.btnExerciseVideo);
        btnExerciseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginActivity.getCurrentWeeklyReport() != null) {
                    final Intent i = new Intent(WorkoutActivity.this, GuideActivity.class);
                    i.putExtra("videoId", workoutPlan.get(index).getVideoId());
                    startActivity(i);
                } else {
                    Toast.makeText(WorkoutActivity.this, "Not connected/logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnPause = findViewById(R.id.btnPause);
        btnPause.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                Log.i(TAG, "onEvent");
                if (!isPaused) {
                    timerPause();
                    isPaused = true;
                } else {
                    timerResume();
                    isPaused = false;
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
            }
        });

        btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                if (exerciseTimer != null && exerciseTimer.getMilliLeft() > 0) {
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
        timerStart(3 * 1000, TimerType.MAIN);
        totalExerciseTimeInMinutes += 2;
    }

    private void load() {
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
            timerStart(repNumber * 1000, TimerType.MINI);
            tvExerciseTimer.setVisibility(View.VISIBLE);
            reps = repNumber + " seconds";
        }
        tvExercise.setText(workoutPlan.get(index).getExerciseName() + "\n" + reps);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private enum TimerType {
        MAIN,
        MINI
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
            timerStart(min * 60000, TimerType.MAIN);
            totalExerciseTimeInMinutes += min;
            popupWindow.dismiss();
        }
    }

    /**
     * Custom timer implementation with 2 timer types: TYPE_MAIN, TYPE_MINI.
     * TYPE_MAIN is the main timer which shows how long the user has been working out.
     * TYPE_MINI is a smaller timer which shows represent the time left for a timed exercises (e.g. Plank, Wall Sit).
     */
    private class CustomCountDownTimer extends CountDownTimer {
        private static final int TYPE_MAIN = 1;
        private static final int TYPE_MINI = 2;
        private final TimerType type;
        private long milliLeft;

        public CustomCountDownTimer(long millisInFuture, long countDownInterval, TimerType type) {
            super(millisInFuture, countDownInterval);
            this.type = type;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            milliLeft = millisUntilFinished;
            millisUntilFinished += 1000;
            final long min = (millisUntilFinished / (1000 * 60));
            final long sec = ((millisUntilFinished / 1000) - min * 60);
            final String secString;
            if (sec < 10) {
                secString = "0" + (sec);
            } else {
                secString = (sec) + "";
            }
            if (type == TimerType.MAIN) {
                tvTimer.setText(min + ":" + secString);
            } else if (type == TimerType.MINI) {
                tvExerciseTimer.setText(min + ":" + secString);
            } else {
                Log.e(TAG, "Timer type not recognized by CustomCountDownTimer, type: " + type);
                Toast.makeText(WorkoutActivity.this, "Timer did not load properly", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFinish() {
            //Finishes exercise timer if main timer runs out
            if (type == TimerType.MAIN) {
                tvTimer.setText("0:00");
                if (exerciseTimer != null && exerciseTimer.getMilliLeft() > 0) {
                    exerciseTimer.onFinish();
                }
                addTimePrompt();
            }
            if (type == TimerType.MINI) {
                tvExerciseTimer.setVisibility(View.INVISIBLE);
                tvExerciseTimer.setText("0:00");
            }
        }

        public long getMilliLeft() {
            return milliLeft;
        }

        public TimerType getType() {
            return type;
        }
    }
}