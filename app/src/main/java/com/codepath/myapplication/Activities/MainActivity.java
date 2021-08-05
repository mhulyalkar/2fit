package com.codepath.myapplication.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codepath.myapplication.R;
import com.codepath.myapplication.databinding.ActivityMainBinding;
import com.codepath.myapplication.ui.Tabs.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;


/**
 * Main activity with two tabbed fragments: Weekly Report and Workouts.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "444623aa7a4a4026ae43611406f1056e";
    private static final String REDIRECT_URI = "http://com.codepath.twofitapp.2fitApp/callback";
    private static SpotifyAppRemote mSpotifyAppRemote;
    private static Track currentTrack;
    private static boolean isPaused = false;

    public static SpotifyAppRemote getMSpotifyAppRemote() {
        return mSpotifyAppRemote;
    }

    public static void setCurrentTrack(Track currentTrack) {
        MainActivity.currentTrack = currentTrack;
    }

    public static Track getCurrentTrack() {
        return currentTrack;
    }

    public static void spotifyPopUp(Activity activity) {
        final View promptView;
        final LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final PopupWindow popupWindowAddTime = new PopupWindow(inflater.inflate(R.layout.popup_spotify, null));
        promptView = popupWindowAddTime.getContentView();
        final int width = LinearLayout.LayoutParams.MATCH_PARENT;
        final int height = 500;
        final boolean focusable = true;
        final PopupWindow popupWindow = new PopupWindow(promptView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.popup_window_animation);
        popupWindow.showAtLocation(promptView, Gravity.CENTER, 0, 900);
        final ImageButton ibSpotifySkipNext, ibSpotifySkipPrev;
        final ImageView ivSpotifyLogo, ivSongImage;
        final SparkButton btnSpotifyPause;
        final TextView tvSpotifySongTitle, tvSpotifySinger;
        tvSpotifySinger = promptView.findViewById(R.id.tvSpotifySinger);
        tvSpotifySongTitle = promptView.findViewById(R.id.tvSpotifySongTitle);
        ibSpotifySkipNext = promptView.findViewById(R.id.ibSpotifySkipNext);
        ibSpotifySkipNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
                getSpotifyTrack(activity, tvSpotifySinger, tvSpotifySongTitle);
            }
        });
        ibSpotifySkipPrev = promptView.findViewById(R.id.ibSpotifySkipPrev);
        ibSpotifySkipPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpotifyAppRemote.getPlayerApi().skipPrevious();
                getSpotifyTrack(activity, tvSpotifySinger, tvSpotifySongTitle);
            }
        });
        ivSongImage = promptView.findViewById(R.id.ivSongImage);
        ivSpotifyLogo = promptView.findViewById(R.id.ivSpotifyLogo);
        btnSpotifyPause = promptView.findViewById(R.id.btnSpotifyPause);
        if (isPaused) {
            btnSpotifyPause.setActiveImage(R.mipmap.ic_spotify_play);
            btnSpotifyPause.setInactiveImage(R.mipmap.ic_spotify_pause);
        }
        btnSpotifyPause.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (isPaused) {
                    mSpotifyAppRemote.getPlayerApi().resume();
                } else {
                    mSpotifyAppRemote.getPlayerApi().pause();
                }
                isPaused = !isPaused;
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
            }
        });
        tvSpotifySinger.setText(currentTrack.artist.name);
        tvSpotifySongTitle.setText(currentTrack.name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        final TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        final FloatingActionButton fab = binding.btnSpotify;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spotifyPopUp(MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.btnLogout) {
            if (LoginActivity.isUserOnline()) {
                mSpotifyAppRemote.getPlayerApi().pause();
            }
            final Intent i = new Intent(MainActivity.this, LoginActivity.class);
            LoginActivity.removeCurrentWeeklyReport();
            LoginActivity.removeCurrentUser();
            ParseUser.logOut();
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        final ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();
        if (LoginActivity.isUserOnline() & currentTrack == null) {
            SpotifyAppRemote.connect(this, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.i(TAG, "Connected to Spotify");
                            Toast.makeText(MainActivity.this, "Connected to Spotify", Toast.LENGTH_SHORT).show();
                            connected();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("MainActivity", throwable.getMessage(), throwable);
                            Toast.makeText(MainActivity.this, "Could not connect to Spotify", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    MainActivity.setCurrentTrack(playerState.track);
                    if (currentTrack != null) {
                        Toast.makeText(MainActivity.this, currentTrack.name + " by " + currentTrack.artist.name, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Couldn't find track");
                        Toast.makeText(MainActivity.this, "Couldn't find track", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    public static void getSpotifyTrack(Activity activity,TextView artist, TextView songTitle) {
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    MainActivity.setCurrentTrack(playerState.track);
                    if (currentTrack != null) {
                        artist.setText(getCurrentTrack().artist.name);
                        songTitle.setText(getCurrentTrack().name);
                    } else {
                        Log.e(TAG, "Couldn't find track");
                        Toast.makeText(activity, "Couldn't find track", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}