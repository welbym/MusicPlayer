package com.example.musicplayer.ui.albums;

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

public class AlbumsFragment extends Fragment {

    private AlbumsViewModel albumsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        albumsViewModel =
                ViewModelProviders.of(this).get(AlbumsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_albums, container, false);
        final TextView textView = root.findViewById(R.id.text_albums);
        albumsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}