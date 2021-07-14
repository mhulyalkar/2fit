package com.codepath.myapplication;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Workout")
public class Workout extends ParseObject {
    private static final String KEY_NAME = "Name";
    private static final String KEY_TARGET_AREAS = "targetAreas";
    private static final String KEY_EXERCISES = "exersizes";
    private static final String KEY_DIFFICULTY = "difficulty";

    public Workout() {
        super();
    }

    public String getWorkoutName() {
        return getString(KEY_NAME);
    }
    public String getTargetArea() {
        return getString(KEY_TARGET_AREAS);
    }
    public List<Object> getExercises() {
        return getList(KEY_EXERCISES);
    }
    public int getDifficulty() {
        return getInt(KEY_DIFFICULTY);
    }
}
