package com.example.musicplayer.ui.albums;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

import java.util.ArrayList;

public class AlbumsFragment extends Fragment {

    private static final String TAG = "AlbumFragment";

    private Context context;
    private ArrayList<Album> albumList;
    private AlbumAdapter.AlbumArtGetter getter;
    private AlbumAdapter.OnAlbumListener onAlbumListener;

    public AlbumsFragment(Context setContext, ArrayList<Album> setAlbumList,
                          AlbumAdapter.AlbumArtGetter setGetter, AlbumAdapter.OnAlbumListener setListener) {
        context = setContext;
        albumList = setAlbumList;
        getter = setGetter;
        onAlbumListener = setListener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);

        RecyclerView songListView = view.findViewById(R.id.recycler_view_songs);
        if (songListView != null) {
            Log.d(TAG, "songListView is not null :)");
            songListView.setAdapter(new AlbumAdapter(albumList, getter, onAlbumListener));
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            songListView.setLayoutManager(layoutManager);
            songListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            songListView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        } else {
            Log.d(TAG, "songListView is null :(");
        }

        return view;
    }
}