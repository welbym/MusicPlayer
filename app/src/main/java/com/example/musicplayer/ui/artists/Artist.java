package com.example.musicplayer.ui.artists;

import android.util.Log;

import com.example.musicplayer.ui.albums.Album;

import java.util.ArrayList;

public class Artist {

    private static final String TAG = "Artist";

    private String artist;
    private ArrayList<Album> albumList;

    public Artist(final String setArtist) {
        artist = setArtist;
        albumList = new ArrayList<>();
    }

    public String getName() { return artist; }

    public ArrayList<Album> getAlbumList() { return albumList; }

    public void addAlbum(Album addAlbum) {
        if (albumList != null ) {
            albumList.add(addAlbum);
        } else {
            Log.d(TAG, "songList is null :(");
        }
    }
}
