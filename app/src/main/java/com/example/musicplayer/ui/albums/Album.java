package com.example.musicplayer.ui.albums;

import android.util.Log;

import com.example.musicplayer.ui.songs.Song;

import java.util.ArrayList;

public class Album {

    private static final String TAG = "Album";

    private String album;
    private String artist;
    private ArrayList<Song> songList;

    public Album(final String setTitle, final String setArtist) {
        album = setTitle;
        artist = setArtist;
        songList = new ArrayList<>();
    }

    public String getTitle() {
        return album;
    }

    public String getArtist() { return artist; }

    public ArrayList<Song> getSongList() { return songList; }

    public void addSong(Song addSong) {
        if (songList != null ) {
            songList.add(addSong);
        } else {
            Log.d(TAG, "songList is null :(");
        }
    }
}
