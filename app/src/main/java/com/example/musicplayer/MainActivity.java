package com.example.musicplayer;

import android.Manifest;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import com.example.musicplayer.ui.NowPlayingFragment;
import com.example.musicplayer.ui.SongPlayingFragment;
import com.example.musicplayer.ui.albums.Album;
import com.example.musicplayer.ui.albums.AlbumAdapter;
import com.example.musicplayer.ui.albums.AlbumsFragment;
import com.example.musicplayer.ui.artists.Artist;
import com.example.musicplayer.ui.artists.ArtistAdapter;
import com.example.musicplayer.ui.artists.ArtistsFragment;
import com.example.musicplayer.ui.songs.Song;
import com.example.musicplayer.ui.songs.SongAdapter;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

import com.example.musicplayer.MediaService.MediaBinder;

public class MainActivity extends AppCompatActivity implements SongAdapter.AlbumArtGetter,
        AlbumAdapter.AlbumArtGetter, SongAdapter.OnSongListener, AlbumAdapter.OnAlbumListener,
        ArtistAdapter.OnArtistListener, MediaService.BottomWidgetUpdater {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST = 1;

    // List of songs from phone
    private ArrayList<Song> songList;
    // List of albums from those songs
    private ArrayList<Album> albumList;
    // List of artists from those songs
    private ArrayList<Artist> artistList;
    // Map of art from those songs
    private HashMap<String, Bitmap> albumArtMap;

    // Holds icons for 'Songs' 'Albums' 'Artists' on bottom of screen
    private BottomNavigationView bottomNavView;

    private FrameLayout containerFrame;
    private FrameLayout nowPlayingFrame;
    private ProgressBar songPositionBar;
    private Button playPauseButton;

    private MediaService mediaService;
    private Intent playIntent;

    private SongsFragment songsFragment;
    private AlbumsFragment albumsFragment;
    private ArtistsFragment artistsFragment;
    private Fragment selectedFragment;

    private NowPlayingFragment nowPlayingFragment;
    private SongPlayingFragment songPlayingFragment;
    private FrameLayout songPlayingFrame;

    private boolean extraFragmentOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
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

            // Sets arrays to empty temporarily
            songList = new ArrayList<>();
            albumArtMap = new HashMap<>();
            albumList = new ArrayList<>();
            artistList = new ArrayList<>();

            setFragments();
            setSongList();
            setAlbumList();
            setArtistList();
            sortAlbumList();

            // holds recycler views
            containerFrame = findViewById(R.id.fragment_container);
            // holds text and small album art on current song playing at bottom of screen
            nowPlayingFrame = findViewById(R.id.now_playing_container);
            nowPlayingFrame.setVisibility(View.GONE);
            songPositionBar = findViewById(R.id.song_position_bar);
            songPositionBar.setVisibility(View.GONE);
            playPauseButton = findViewById(R.id.play_pause_button);
            playPauseButton.setVisibility(View.GONE);
            bottomNavView = findViewById(R.id.nav_view);
            // holds full screen view of current song playing
            songPlayingFrame = findViewById(R.id.song_playing_container);
            songPlayingFrame.setVisibility(View.GONE);

            extraFragmentOpen = false;

            playPauseButton.setOnClickListener(v -> {
                if (mediaService != null && nowPlayingFragment.nowPlayingText()) {
                    mediaService.pauseOrPlaySong();
                }
            });
            nowPlayingFrame.setOnClickListener(v -> {
                Log.v(TAG, "Clicked now playing");
                openSongPlaying();
            });
            nowPlayingFrame.setOnTouchListener(new OnSwipeTouchListener(this) {
                public void onSwipeRight() { mediaService.playPrev(); }
                public void onSwipeLeft() {
                    mediaService.playNext();
                }
                public void onSwipeTop() {
                    openSongPlaying();
                }
            });
            songPlayingFrame.setOnTouchListener(new OnSwipeTouchListener(this) {
                public void onSwipeRight() {
                    mediaService.playPrev();
                }
                public void onSwipeLeft() {
                    mediaService.playNext();
                }
                public void onSwipeBottom() {
                    songPlayingFragmentOnBackClick();
                }
                public void onSwipeTop() {
                    mediaService.pauseOrPlaySong();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void setSongList() {
        //retrieve song info
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,
                null, null, null, null);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int trackColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);

            // sets basic default Bitmap
            Bitmap songImage = Bitmap.createBitmap(36, 36, Bitmap.Config.RGB_565);

            Log.v(TAG, "About to enter loop, hang tight");
            //add songs to list
            do {
                long ID = musicCursor.getLong(idColumn);
                String album = musicCursor.getString(albumColumn);

                if (!albumArtMap.containsKey(album)) {
                    // get metadata for album art
                    try {
                        mmr.setDataSource(this, ContentUris.withAppendedId(
                                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ID));
                        byte[] byteArray = mmr.getEmbeddedPicture();
                        if (byteArray != null) {
                            songImage = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "Didn't properly get album art", e);
                        songImage = Bitmap.createBitmap(36, 36, Bitmap.Config.RGB_565);
                    }
                    albumArtMap.put(album, songImage);
                }

                songList.add(new Song(ID, musicCursor.getString(titleColumn),
                        musicCursor.getString(artistColumn), album, musicCursor.getInt(trackColumn)));
            } while (musicCursor.moveToNext());
        }
        Log.v(TAG, "Out of the cursed loop");
        if (musicCursor != null) { musicCursor.close(); }
        if (songList != null) {
            Collections.sort(songList, (a, b) -> formatWord(a.getTitle()).compareTo(formatWord(b.getTitle())));
        }
    }

    public void setAlbumList() {
        for (Song loopSong : songList) {
            // Loop through albums to see if the List contains the Album that pertains to the Song
            boolean containsAlbum = false;
            int indexAlbum = 0;
            if (albumList.size() > 0) {
                for (int i = 0; i < albumList.size(); i++) {
                    if (loopSong.getAlbum().equals(albumList.get(i).getTitle())) {
                        containsAlbum = true;
                        indexAlbum = i;
                    }
                }
            }
            if (containsAlbum) {
                albumList.get(indexAlbum).addSong(loopSong);
            } else {
                Album addAlbum = new Album(loopSong.getAlbum(), loopSong.getArtist());
                addAlbum.addSong(loopSong);
                albumList.add(addAlbum);
            }
        }
        for (Album loopAlbum : albumList) {
            sortSongListTrack(loopAlbum.getSongList());
        }
    }

    public void sortSongListTrack(ArrayList<Song> albumSongList) {
        if (songList != null) {
            Collections.sort(albumSongList, (a, b) -> a.getTrack() - b.getTrack());
        }
    }

    public void setArtistList() {
        for (Album loopAlbum : albumList) {
            // Loop through albums to see if the List contains the Album that pertains to the Artist
            boolean containsArtist = false;
            int indexArtist = 0;
            if (artistList.size() > 0) {
                for (int i = 0; i < artistList.size(); i++) {
                    if (loopAlbum.getArtist().equals(artistList.get(i).getName())) {
                        containsArtist = true;
                        indexArtist = i;
                    }
                }
            }
            if (containsArtist) {
                artistList.get(indexArtist).addAlbum(loopAlbum);
            } else {
                Artist addArtist = new Artist(loopAlbum.getArtist());
                addArtist.addAlbum(loopAlbum);
                artistList.add(addArtist);
            }
        }
        Collections.sort(artistList, (a, b) -> formatWord(a.getName()).compareTo(formatWord(b.getName())));
        for (Artist loopArtist : artistList) {
            sortArtistListAlbum(loopArtist.getAlbumList());
        }
    }

    public void sortAlbumList() {
        albumList.clear();
        for (Artist loopArtist : artistList) {
            albumList.addAll(loopArtist.getAlbumList());
        }
    }

    public void sortArtistListAlbum(ArrayList<Album> artistAlbumList) {
        if (artistList != null) {
            Collections.sort(artistAlbumList, (a, b) -> formatWord(a.getTitle()).compareTo(formatWord(b.getTitle())));
        }
    }

    public String formatWord(String a) {
        String b = a.toLowerCase();
        if (b.startsWith("the ")) {
            b = b.replaceFirst("the ", "");
        }
        if (b.startsWith("a ")) {
            b = b.replaceFirst("a ", "");
        }
        if (b.startsWith("\"")) {
            b = b.replaceFirst("\"", "");
        }
        return b;
    }

    public void setFragments() {
        songsFragment = new SongsFragment(this, songList, this, this);
        albumsFragment= new AlbumsFragment(this, albumList, this, this);
        artistsFragment = new ArtistsFragment(this, artistList, this);
        nowPlayingFragment = new NowPlayingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.now_playing_container, nowPlayingFragment).commit();
        songPlayingFragment = new SongPlayingFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.song_playing_container, songPlayingFragment).commit();
        // Gets the navView by ID and sets it to variable
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Creates listener to detect when nav view buttons are pressed and then changes fragment accordingly
        navView.setOnNavigationItemSelectedListener(menuItem -> {
            selectedFragment = null;

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
            }
            return true;
        });

        // Sets default fragment to the SongsFragment so it is displayed right when the app is launched
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, songsFragment).commit();
    }

    public void showNowPlayingFrame() {
        nowPlayingFrame.setVisibility(View.VISIBLE);
        songPositionBar.setVisibility(View.VISIBLE);
        playPauseButton.setVisibility(View.VISIBLE);
        containerFrame.setPadding(0, 0, 0, 143);
    }

    public void openSongPlaying() {
        containerFrame.setVisibility(View.GONE);
        nowPlayingFrame.setVisibility(View.GONE);
        songPositionBar.setVisibility(View.GONE);
        playPauseButton.setVisibility(View.GONE);
        bottomNavView.setVisibility(View.GONE);
        songPlayingFrame.setVisibility(View.VISIBLE);
    }

    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MediaBinder binder = (MediaBinder) service;
            mediaService = binder.getService();
            //pass list
            mediaService.setSongList(songList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            unbindService(musicConnection);
        }
    };

    @Override
    public HashMap<String, Bitmap> getAlbumArtMap() { return albumArtMap; }

    @Override
    public void onSongClick(int position, ArrayList<Song> songArrayList, RelativeLayout relativeLayout) {
        mediaService.setSongList(songArrayList);
        mediaService.setSong(position);
        mediaService.setTextViewUpdater(this);
        mediaService.playSong();
        if (nowPlayingFrame.getVisibility() == View.GONE && nowPlayingFragment.nowPlayingText()) {
            showNowPlayingFrame();
        }
    }

    @Override
    public void onAlbumClick(int position, RelativeLayout relativeLayout) {
        ArrayList<Song> albumSongList = albumList.get(position).getSongList();
        SongsFragment albumSongsFragment = new SongsFragment(this, albumSongList, this, this);
        getSupportFragmentManager().beginTransaction().attach(albumSongsFragment).add(albumSongsFragment, "").commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, albumSongsFragment).commit();
        extraFragmentOpen = true;
        if (nowPlayingFrame.getVisibility() == View.GONE && nowPlayingFragment.nowPlayingText()) {
            showNowPlayingFrame();
        }
    }

    @Override
    public void onArtistClick(int position, LinearLayout linearLayout) {
        ArrayList<Song> artistSongList = new ArrayList<>();
        for (Album loopAlbum : artistList.get(position).getAlbumList()) {
            artistSongList.addAll(loopAlbum.getSongList());
        }
        SongsFragment artistSongsFragment = new SongsFragment(this, artistSongList, this, this);
        getSupportFragmentManager().beginTransaction().attach(artistSongsFragment).add(artistSongsFragment, "").commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, artistSongsFragment).commit();
        extraFragmentOpen = true;
        if (nowPlayingFrame.getVisibility() == View.GONE && nowPlayingFragment.nowPlayingText()) {
            showNowPlayingFrame();
        }
    }

    @Override
    public void updateTextView(int position) {
        Bitmap art = albumArtMap.get(mediaService.getSongList().get(position).getAlbum());
        String title = mediaService.getSongList().get(position).getTitle();
        String artist = mediaService.getSongList().get(position).getArtist();

        nowPlayingFragment.setNowPlayingArt(art);
        nowPlayingFragment.setNowPlayingText(title, artist);

        songPlayingFragment.setSongPlayingArt(art);
        songPlayingFragment.setSongPlayingText(title, artist);
    }

    @Override
    public Song getSong(int position) {
        return mediaService.getSongList().get(position);
    }

    @Override
    public void updatePositionBar(final Uri trackUri, final MediaPlayer player) {
        Log.v(TAG, "made it to updatePositionBar");
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, trackUri);
            int duration = Integer.parseInt(
                    mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            songPositionBar.setMax(duration);
        } catch (Exception e) {
            Log.d(TAG, "Couldn't set position bar duration", e);
        }
        updateProgress(player);
    }

    public void updateProgress(final MediaPlayer player) {
        Log.v(TAG, "made it to updateProgress");
        // Thread updates songPositionBar
        new Thread(() -> {
            boolean first = true;
            Log.v(TAG, "inside run function in thread");
            while (player.isPlaying() || first) {
                try {
                   songPositionBar.setProgress(player.getCurrentPosition());
                   first = false;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d(TAG, "Failure in thread", e);
                }
            }
        }).start();
    }

    @Override
    public void updatePlayPauseButton(boolean isPlaying) {
        if (isPlaying) {
            playPauseButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.drawable.ic_pause_black_24dp),
                    null, null, null);
        } else {
            playPauseButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getDrawable(R.drawable.ic_play_black_24dp),
                    null, null, null);
        }
    }

    // Called once right below and once up in a gesture listener
    public void songPlayingFragmentOnBackClick() {
            songPlayingFrame.setVisibility(View.GONE);
            containerFrame.setVisibility(View.VISIBLE);
            nowPlayingFrame.setVisibility(View.VISIBLE);
            songPositionBar.setVisibility(View.VISIBLE);
            playPauseButton.setVisibility(View.VISIBLE);
            bottomNavView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (songPlayingFrame.getVisibility() == View.VISIBLE) {
            songPlayingFragmentOnBackClick();
        } else if (extraFragmentOpen) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            extraFragmentOpen = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MediaService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mediaService = null;
        if (musicConnection != null) {
            unbindService(musicConnection);
        }
        super.onDestroy();
    }

}
