package com.codepath.myapplication.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codepath.myapplication.R;
import com.codepath.myapplication.databinding.ActivityMainBinding;
import com.codepath.myapplication.ui.Tabs.SectionsPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;


/**
 * Main activity with two tabbed fragments: Weekly Report and Workouts.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String CLIENT_ID = "444623aa7a4a4026ae43611406f1056e";
    private static final String REDIRECT_URI = "http://com.codepath.twofitapp.2fitApp/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

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
        final FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Working on implementing custom workout", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
            mSpotifyAppRemote.getPlayerApi().pause();
            final Intent i = new Intent(MainActivity.this, LoginActivity.class);
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
        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.i(TAG, "Connected to Spotify");
                        connected();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                    }
                });
    }

    private void connected() {
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        Toast.makeText(MainActivity.this, track.name + " by " + track.artist.name, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Couldn't find track");
                        Toast.makeText(MainActivity.this, "Couldn't find track", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSpotifyAppRemote.getPlayerApi().pause();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    public SpotifyAppRemote getMSpotifyAppRemote() {
        return mSpotifyAppRemote;
    }
}