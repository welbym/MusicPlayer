package com.example.musicplayer.ui.artists;

import android.util.Log;

import com.example.musicplayer.ui.songs.Song;

import java.util.ArrayList;

public class Artist {

    private static final String TAG = "Artist";

    private String artist;
    private ArrayList<Song> songList;

    public Artist(final String setArtist) {
        artist = setArtist;
        songList = new ArrayList<>();
    }

    public String getName() { return artist; }

    public ArrayList<Song> getSongList() { return songList; }

    public void addSong(Song addSong) {
        if (songList != null ) {
            songList.add(addSong);
        } else {
            Log.d(TAG, "songList is null :(");
        }
    }
}
