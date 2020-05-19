package com.example.musicplayer.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;

public class NowPlayingFragment extends Fragment {

    private ImageView nowPlayingArt;
    private TextView nowPlayingTitleText;
    private TextView nowPlayingArtistText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        nowPlayingArt = view.findViewById(R.id.now_playing_art);
        nowPlayingTitleText = view.findViewById(R.id.now_playing_title_text);
        nowPlayingTitleText.setSelected(true);
        nowPlayingArtistText = view.findViewById(R.id.now_playing_artist_text);
        nowPlayingArtistText.setSelected(true);
        return view;
    }

    public void setNowPlayingArt(Bitmap setArt) {
        nowPlayingArt.setImageBitmap(setArt);
    }


    public void setNowPlayingText(String title, String artist) {
        nowPlayingTitleText.setText(title);
        nowPlayingArtistText.setText(artist);
    }

    public boolean nowPlayingTextBlank() {
        return nowPlayingTitleText.getText().toString().equals("");
    }
}
