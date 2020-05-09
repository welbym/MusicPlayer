package com.example.musicplayer.ui.songPlaying;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;

public class SongPlayingFragment extends Fragment {

    private final String TAG = "SongPlayingFragment";

    private OnBackListener backListener;
    private OnBackPressedCallback callback;
    private ImageView songPlayingArt;
    private TextView songPlayingTitleText;
    private TextView songPlayingArtistText;

    public SongPlayingFragment(OnBackListener setBackListener) {
        backListener = setBackListener;
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backListener.OnBackClick();
                Log.v(TAG,"Passed if statement");
            }
        };
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);
        songPlayingArt = view.findViewById(R.id.song_playing_art);
        songPlayingTitleText = view.findViewById(R.id.song_playing_title_text);
        songPlayingArtistText = view.findViewById(R.id.song_playing_artist_text);
        return view;
    }

    public void setSongPlayingArt(Bitmap setArt) {
        songPlayingArt.setImageBitmap(setArt);
    }

    public void setSongPlayingText(String title, String artist) {
        songPlayingTitleText.setText(title);
        songPlayingArtistText.setText(artist);
    }

    public void setBackListener(boolean songPlayingOpen) {
        if (songPlayingOpen) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        }
        callback.setEnabled(songPlayingOpen);
    }

    public interface OnBackListener {
        void OnBackClick();
    }

}
