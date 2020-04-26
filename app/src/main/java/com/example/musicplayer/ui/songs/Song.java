package com.example.musicplayer.ui.songs;

public class Song {

    private long id;
    private String title;
    private String artist;
    private String album;

    public Song(final long setID, final String setTitle,
                final String setArtist, final String setAlbum) {
        id = setID;
        title = setTitle;
        artist = setArtist;
        album = setAlbum;
    }

    public final long getID() {
        return id;
    }

    public final String getTitle() {
        return title;
    }

    public final String getArtist() {
        return artist;
    }

    public final String getAlbum() {
        return album;
    }
}
