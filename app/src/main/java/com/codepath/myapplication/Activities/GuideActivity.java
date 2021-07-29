package com.codepath.myapplication.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.myapplication.R;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class GuideActivity extends YouTubeBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        final String videoId = getIntent().getStringExtra("videoId");
        final YouTubePlayerView playerView = (YouTubePlayerView) findViewById(R.id.ytPlayer);
        playerView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo(videoId);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
                Toast.makeText(GuideActivity.this, "Error initializing YouTube player", Toast.LENGTH_SHORT).show();
            }

        });
    }
}