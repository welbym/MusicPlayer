package com.example.musicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.musicplayer.ui.albums.AlbumsFragment;
import com.example.musicplayer.ui.artists.ArtistsFragment;
import com.example.musicplayer.ui.songs.Song;
import com.example.musicplayer.ui.songs.SongAdapter;
import com.example.musicplayer.ui.songs.SongsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.example.musicplayer.MediaService.MediaBinder;

public class MainActivity extends AppCompatActivity implements SongAdapter.OnSongListener {

    private static final String TAG = "MyActivity";

    private static final int PERMISSION_REQUEST = 1;

    public MediaPlayer player;
    private ArrayList<Song> songList;

    private TextView nowPlayingTitleText;
    private TextView nowPlayingArtistText;

    private MediaService mediaService;
    private Intent playIntent;
    private boolean mediaBound = false;

    private SongsFragment songsFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;

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

            player = new MediaPlayer();

            setSongList();
            setFragments();
            nowPlayingTitleText = findViewById(R.id.now_playing_title_text);
            nowPlayingArtistText = findViewById(R.id.now_playing_artist_text);
            findViewById(R.id.play_pause_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaService.pauseSong();
                }
            });
            findViewById(R.id.stop_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mediaService.stopSong();
                    nowPlayingTitleText.setText("");
                    nowPlayingArtistText.setText("");
                }
            });
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
        songsFragment = new SongsFragment(this, songList, this);
        albumsFragment= new AlbumsFragment();
        artistsFragment = new ArtistsFragment();
        // Gets the navView by ID and sets it to variable
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Creates listener to detect when nav view buttons are pressed and then changes fragment accordingly
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_songs:
                        selectedFragment = songsFragment;
                        break;
                    case R.id.navigation_albums:
                        selectedFragment = albumsFragment;
                        break;
                    case R.id.navigation_artists:
                        selectedFragment = artistsFragment;
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

        // Sets default fragment to the SongsFragment so it is displayed right when the app is launched
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, songsFragment).commit();
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

    @Override
    public void onSongClick(int position, LinearLayout linearLayout) {
        mediaService.setSong(position);
        mediaService.playSong();
        nowPlayingTitleText.setText(songList.get(position).getTitle());
        nowPlayingArtistText.setText(songList.get(position).getArtist());
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaBinder binder = (MediaBinder) service;
            mediaService = binder.getService();
            //pass list
            mediaService.setSongList(songList);
            mediaBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mediaBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mediaService = null;
        super.onDestroy();
    }

}
