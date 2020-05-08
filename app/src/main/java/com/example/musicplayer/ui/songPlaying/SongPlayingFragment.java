package com.example.musicplayer.ui.songPlaying;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;

public class SongPlayingFragment extends Fragment {

    private OnBackListener backListener;
    private ImageView songPlayingArt;

    public SongPlayingFragment(OnBackListener setBackListener) {
        backListener = setBackListener;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song_playing, container, false);
        Button backButton = view.findViewById(R.id.back_button);
        songPlayingArt = view.findViewById(R.id.song_playing_art);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backListener.OnBackClick();
            }
        });
        return view;
    }

    public void setSongPlayingArt(Bitmap setArt) {
        songPlayingArt.setImageBitmap(setArt);
    }

    public interface OnBackListener {
        void OnBackClick();
    }

}
