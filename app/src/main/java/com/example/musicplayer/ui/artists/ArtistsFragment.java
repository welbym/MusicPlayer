package com.example.musicplayer.ui.artists;

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

public class ArtistsFragment extends Fragment {

    private static final String TAG = "ArtistFragment";

    private Context context;
    private ArrayList<Artist> artistList;
    private ArtistAdapter.OnArtistListener onArtistListener;

    public ArtistsFragment(Context setContext, ArrayList<Artist> setArtistList, ArtistAdapter.OnArtistListener setOnArtistListener) {
        context = setContext;
        artistList = setArtistList;
        onArtistListener = setOnArtistListener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        RecyclerView songListView = view.findViewById(R.id.recycler_view_songs);
        if (songListView != null) {
            Log.d(TAG, "songListView is not null :)");
            songListView.setAdapter(new ArtistAdapter(artistList, onArtistListener));
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            songListView.setLayoutManager(layoutManager);
            songListView.addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
        } else {
            Log.d(TAG, "songListView is null :(");
        }

        return view;
    }
}