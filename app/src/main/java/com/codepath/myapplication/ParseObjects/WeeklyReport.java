package com.codepath.myapplication.ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;

@ParseClassName("WeeklyReport")
public class WeeklyReport extends ParseObject {
    private static final String KEY_DAYS_IN_A_ROW = "daysInARow";
    private static final String KEY_WEEKLY_CALORIES_BURNED = "weeklyCaloriesBurned";
    private static final String KEY_DURATION = "duration";
    private static final String KEY_LAST_WORKOUT_DATE = "lastWorkoutDate";

    public int getDaysInARow() {
        return getInt(KEY_DAYS_IN_A_ROW);
    }

    public void setDaysInARow(int daysInARow) {
        put(KEY_DAYS_IN_A_ROW, daysInARow);
    }

    public int getWeeklyCaloriesBurned() {
        return getInt(KEY_WEEKLY_CALORIES_BURNED);
    }

    public void setWeeklyCaloriesBurned(int weeklyCaloriesBurned) {
        put(KEY_WEEKLY_CALORIES_BURNED, weeklyCaloriesBurned);
    }

    public int getDuration() {
        return getInt(KEY_DURATION);
    }

    public void setDuration(int duration) {
        put(KEY_DURATION, duration);
    }

    public Date getLastWorkoutDate() {
        return getDate(KEY_LAST_WORKOUT_DATE);
    }

    public void updateLastWorkoutDate() {
        put(KEY_LAST_WORKOUT_DATE, new Date());
    }
}
