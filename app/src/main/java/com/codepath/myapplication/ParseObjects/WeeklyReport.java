package com.codepath.myapplication.ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("WeeklyReport")
public class WeeklyReport extends ParseObject {
    private static final String KEY_DAYS_IN_A_ROW = "daysInARow";
    private static final String KEY_WEEKLY_CALORIES_BURNED = "weeklyCaloriesBurned";
    private static final String KEY_DURATION = "duration";

    public int getDaysInARow() {
        return getInt(KEY_DAYS_IN_A_ROW);
    }
    public int getWeeklyCaloriesBurned() {
        return getInt(KEY_WEEKLY_CALORIES_BURNED);
    }
    public int getDuration() {
        return getInt(KEY_DURATION);
    }
    public void setDaysInARow(int daysInARow) {
        put(KEY_DAYS_IN_A_ROW, daysInARow);
    }
    public void setWeeklyCaloriesBurned(int weeklyCaloriesBurned) {
        put(KEY_WEEKLY_CALORIES_BURNED, weeklyCaloriesBurned);
    }
    public void setDuration(int duration) {
        put(KEY_DURATION, duration);
    }
}
