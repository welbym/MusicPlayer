package com.example.musicplayer.ui.songs;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.musicplayer.R;

import java.util.ArrayList;

public class SongsFragment extends Fragment {

    private static final String TAG = "SongFragment";

    private Context context;
    private ArrayList<Song> songList;
    private SongAdapter.AlbumArtGetter getter;
    private SongAdapter.OnSongListener onSongListener;

    public SongsFragment(Context setContext, ArrayList<Song> setSongList,
                         SongAdapter.AlbumArtGetter setGetter, SongAdapter.OnSongListener setListener) {
        context = setContext;
        songList = setSongList;
        getter = setGetter;
        onSongListener = setListener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        RecyclerView songListView = view.findViewById(R.id.recycler_view_songs);
        if (songListView != null) {
            Log.d(TAG, "songListView is not null :)");
            songListView.setAdapter(new SongAdapter(songList, getter, onSongListener));
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            songListView.setLayoutManager(layoutManager);
            songListView.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
        } else {
            Log.d(TAG, "songListView is null :(");
        }

        return view;
    }

}