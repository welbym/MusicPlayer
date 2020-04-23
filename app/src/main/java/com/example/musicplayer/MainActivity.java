package com.example.musicplayer;

import android.os.Bundle;

import com.example.musicplayer.ui.albums.AlbumsFragment;
import com.example.musicplayer.ui.artists.ArtistsFragment;
import com.example.musicplayer.ui.home.HomeFragment;
import com.example.musicplayer.ui.playlists.PlaylistsFragment;
import com.example.musicplayer.ui.songs.SongsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Gets the navView by ID and sets it to variable
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Creates listener to detect when nav view buttons are pressed and then changes fragment accordingly
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = new HomeFragment();
                        break;
                    case R.id.navigation_songs:
                        selectedFragment = new SongsFragment();
                        break;
                    case R.id.navigation_albums:
                        selectedFragment = new AlbumsFragment();
                        break;
                    case R.id.navigation_artists:
                        selectedFragment = new ArtistsFragment();
                        break;
                    case R.id.navigation_playlists:
                        selectedFragment = new PlaylistsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();

                return true;
            }
        });
        // Sets default fragment to the HomeFragment so it is displayed right when the app is launched
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }


    public void play(View v) {
        if (player == null) {
            player = MediaPlayer.create(this, R.raw.bigdata);
        }
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlayer();
            }
        });
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    public void stop(View v) {
        stopPlayer();
    }

    private void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            Toast.makeText(this, "Player released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

}
