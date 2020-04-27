package com.example.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.musicplayer.ui.songs.Song;

public class MediaService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final String TAG = "MediaService";

    private MediaPlayer player;
    private ArrayList<Song> songList;
    private int songPosition;
    private final IBinder mediaBinder = new MediaBinder();
    private TextViewUpdater textViewUpdater;

    public void onCreate(){
        super.onCreate();
        player = new MediaPlayer();
        initMusicPlayer();
        songPosition = 0;
    }

    public void initMusicPlayer(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setSongList(ArrayList<Song> setSongList){
        songList = setSongList;
    }

    public void setSong(int songIndex) { songPosition = songIndex; }

    public void setTextViewUpdater(TextViewUpdater setTextViewUpdater) { textViewUpdater = setTextViewUpdater; }

    public ArrayList<Song> getSongList() { return songList; }

    public class MediaBinder extends Binder {
        MediaService getService() {
            return MediaService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mediaBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void playSong(){
        //play a song
        player.reset();
        Log.v(TAG, "songPosition: " + songPosition);
        Log.v(TAG, "songList: " + songList);
        Song playSong = songList.get(songPosition);
        textViewUpdater.updateTextView(songPosition);
        long currentSong = playSong.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e) {
            Log.e(TAG, "Error setting data source", e);
        }
        player.prepareAsync();
    }

    public void pauseSong() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
        } else {
            Log.d(TAG, "Player is null :(");
        }
    }

    public void stopSong() {
        if (player != null) {
            player.stop();
        } else {
            Log.d(TAG, "Player is null :(");
        }
    }

    public void playPrev() {
        songPosition--;
        if (songPosition < 0) { songPosition = songList.size() - 1; }
        playSong();
    }

    public void playNext() {
        songPosition++;
        if (songPosition >= songList.size()) { songPosition = 0; }
        playSong();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNext();
        if (textViewUpdater != null) {
            textViewUpdater.updateTextView(songPosition);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    public interface TextViewUpdater {
        void updateTextView(int position);
    }

}
