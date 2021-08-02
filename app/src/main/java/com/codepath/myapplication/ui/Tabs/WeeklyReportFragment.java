package com.codepath.myapplication.ui.Tabs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.codepath.myapplication.Activities.LoginActivity;
import com.codepath.myapplication.ParseObjects.WeeklyReport;
import com.codepath.myapplication.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Date;

/**
 * Shows data on user's activity for the past week.
 */
public class WeeklyReportFragment extends Fragment {
    private static final String TAG = "WeeklyReportFragment";
    private TextView tvCalories;
    private TextView tvDaysInARow;
    private TextView tvTimeSpent;
    private TextView tvWeeklyReportOffline;
    private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_weekly_report, container, false);
        tvCalories = rootView.findViewById(R.id.tvCalories);
        tvDaysInARow = rootView.findViewById(R.id.tvDaysInARow);
        tvTimeSpent = rootView.findViewById(R.id.tvTimeSpent);
        tvWeeklyReportOffline = rootView.findViewById(R.id.tvWeeklyReportOffline);
        if (LoginActivity.getCurrentWeeklyReport() != null) {
            final WeeklyReport weeklyReport = (WeeklyReport) LoginActivity.getCurrentWeeklyReport();
            tvCalories.setText("Calories Burned: " + weeklyReport.getWeeklyCaloriesBurned());
            tvDaysInARow.setText("Days in a Row: " + weeklyReport.getDaysInARow());
            tvTimeSpent.setText("Time spent: " + weeklyReport.getDuration());
        } else {
            tvWeeklyReportOffline.setVisibility(View.VISIBLE);
        }
        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (LoginActivity.getCurrentWeeklyReport() != null) {
                    refreshWeeklyReport();
                } else {
                    Log.e(TAG, "Cannot refresh because user is not logged in");
                    Toast.makeText(getActivity(), "Not logged in", Toast.LENGTH_SHORT).show();
                    swipeContainer.setRefreshing(false);
                }
            }
        });
        return rootView;
    }

    private void refreshWeeklyReport() {
        final ParseQuery<WeeklyReport> query = ParseQuery.getQuery(WeeklyReport.class);
        query.getInBackground(LoginActivity.getCurrentUser().getParseObject("weeklyReport").getObjectId(), new GetCallback<WeeklyReport>() {
            public void done(WeeklyReport weeklyReport, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting weekly report", e);
                    Toast.makeText(getActivity(), "Issue with getting weekly report", Toast.LENGTH_SHORT).show();
                    return;
                }
                weeklyReport.pinInBackground("weeklyReport");
                LoginActivity.setCurrentWeeklyReport(weeklyReport);
                if (weeklyReport.getLastWorkoutDate() != null) {
                    //reset WeeklyReport if its a new week
                    final Date lastWorkoutDate = weeklyReport.getLastWorkoutDate();
                    final Date nextMondayDate = new Date(lastWorkoutDate.getYear(),
                            lastWorkoutDate.getMonth(),
                            weeklyReport.getLastWorkoutDate().getDate()
                                    + (7 - weeklyReport.getLastWorkoutDate().getDay()));
                    if (nextMondayDate.compareTo(new Date()) <= 0) {
                        weeklyReport.setWeeklyCaloriesBurned(0);
                        weeklyReport.setDuration(0);
                        weeklyReport.setDaysInARow(0);
                        weeklyReport.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Issue with updating Weekly Report", e);
                                    Toast.makeText(getActivity(), "Issue with updating Weekly Report", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                tvCalories.setText("Calories Burned: " + weeklyReport.getWeeklyCaloriesBurned());
                                tvDaysInARow.setText("Days in a Row: " + weeklyReport.getDaysInARow());
                                tvTimeSpent.setText("Time spent: " + weeklyReport.getDuration());
                                swipeContainer.setRefreshing(false);
                                Toast.makeText(getActivity(), "Successfully updated Weekly Report", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                Toast.makeText(getActivity(), "Successfully logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

