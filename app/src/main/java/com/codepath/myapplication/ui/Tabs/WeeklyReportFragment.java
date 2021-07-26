package com.codepath.myapplication.ui.Tabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.codepath.myapplication.Activities.LoginActivity;
import com.codepath.myapplication.ParseObjects.WeeklyReport;
import com.codepath.myapplication.R;

/**
 * Shows data on user's activity for the past week.
 */
public class WeeklyReportFragment extends Fragment {
    private TextView tvCalories;
    private TextView tvDaysInARow;
    private TextView tvTimeSpent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_weekly_report, container, false);
        tvCalories = rootView.findViewById(R.id.tvCalories);
        tvDaysInARow = rootView.findViewById(R.id.tvDaysInARow);
        tvTimeSpent = rootView.findViewById(R.id.tvTimeSpent);

        final WeeklyReport weeklyReport = (WeeklyReport) LoginActivity.getCurrentWeeklyReport();
        tvCalories.setText("Calories Burned: " + weeklyReport.getWeeklyCaloriesBurned());
        tvDaysInARow.setText("Days in a Row: " + weeklyReport.getDaysInARow());
        tvTimeSpent.setText("Time spent: " + weeklyReport.getDuration());
        return rootView;
    }
}

