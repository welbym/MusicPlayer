package com.example.musicplayer.ui.songs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.musicplayer.R;

public class SongsFragment extends Fragment {

    private SongsViewModel songsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        songsViewModel =
                ViewModelProviders.of(this).get(SongsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_songs, container, false);
        final TextView textView = root.findViewById(R.id.text_songs);
        songsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}