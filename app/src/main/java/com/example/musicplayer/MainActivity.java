package com.example.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.musicplayer.ui.albums.AlbumsFragment;
import com.example.musicplayer.ui.artists.ArtistsFragment;
import com.example.musicplayer.ui.home.HomeFragment;
import com.example.musicplayer.ui.playlists.PlaylistsFragment;
import com.example.musicplayer.ui.songs.Song;
import com.example.musicplayer.ui.songs.SongsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MyActivity";

    private static final int PERMISSION_REQUEST = 1;

    public MediaPlayer player;
    private ArrayList<Song> songList;

    private HomeFragment homeFragment;
    private SongsFragment songsFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;
    private PlaylistsFragment playlistsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            }

        } else {

            setSongList();
            setFragments();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    setSongList();
                }
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void setFragments() {
        homeFragment = new HomeFragment();
        songsFragment = new SongsFragment(this, songList);
        albumsFragment= new AlbumsFragment();
        artistsFragment = new ArtistsFragment();
        playlistsFragment = new PlaylistsFragment();
        // Gets the navView by ID and sets it to variable
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Creates listener to detect when nav view buttons are pressed and then changes fragment accordingly
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        selectedFragment = homeFragment;
                        break;
                    case R.id.navigation_songs:
                        selectedFragment = songsFragment;
                        break;
                    case R.id.navigation_albums:
                        selectedFragment = albumsFragment;
                        break;
                    case R.id.navigation_artists:
                        selectedFragment = artistsFragment;
                        break;
                    case R.id.navigation_playlists:
                        selectedFragment = playlistsFragment;
                        break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                } else {
                    Log.d("MainActivity", "selectedFragment is null :(");
                }

                return true;
            }
        });

        // Sets default fragment to the HomeFragment so it is displayed right when the app is launched
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new HomeFragment()).commit();
    }

    public void setSongList() {
        songList = new ArrayList<>();
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            //add songs to list
            do {
                long ID = musicCursor.getLong(idColumn);
                String title = musicCursor.getString(titleColumn);
                String artist = musicCursor.getString(artistColumn);
                String album = musicCursor.getString(albumColumn);
                songList.add(new Song(ID, title, artist, album));
            } while (musicCursor.moveToNext());
        }
        if (musicCursor != null) { musicCursor.close(); }
        sortSongList();
    }

    public void sortSongList() {
        Collections.sort(songList, new Comparator<Song>(){
            public int compare(Song a, Song b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
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
