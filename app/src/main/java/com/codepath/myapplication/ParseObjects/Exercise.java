package com.codepath.myapplication.ParseObjects;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Exersize")
public class Exercise extends ParseObject {
    public static final String KEY_IS_REPS = "isReps";
    private static final String KEY_NAME = "name";
    private static final String KEY_BEGINNER = "beginner";
    private static final String KEY_INTERMEDIATE = "intermediate";
    private static final String KEY_ADVANCED = "advanced";

    public Exercise() {
        super();
    }

    public String getExerciseName() {
        return getString(KEY_NAME);
    }

    public int getBeginnerReps() {
        return getInt(KEY_BEGINNER);
    }

    public int getIntermediateReps() {
        return getInt(KEY_INTERMEDIATE);
    }

    public int getAdvancedReps() {
        return getInt(KEY_ADVANCED);
    }

    public boolean getIsReps() {
        return getBoolean(KEY_IS_REPS);
    }
}
