package com.example.musicplayer.ui.albums;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<Album> albumList;
    private HashMap<String, Bitmap> albumArtMap;
    private OnAlbumListener onAlbumListener;

    AlbumAdapter(ArrayList<Album> setAlbumList, AlbumArtGetter albumArtGetter, AlbumAdapter.OnAlbumListener setOnAlbumListener) {
        albumList = setAlbumList;
        albumArtMap = albumArtGetter.getAlbumArtMap();
        onAlbumListener = setOnAlbumListener;
    }

    @NonNull
    @Override
    public AlbumAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.album, parent, false), onAlbumListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Album currentAlbum = albumList.get(position);
        holder.albumTitle.setText(currentAlbum.getTitle());
        holder.albumArtist.setText(currentAlbum.getArtist());
        holder.albumArt.setImageBitmap(albumArtMap.get(currentAlbum.getTitle()));

        holder.parentLayout.setOnClickListener(view -> holder.onAlbumListener.onAlbumClick(position, holder.parentLayout));
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        TextView albumTitle;
        TextView albumArtist;
        ImageView albumArt;
        OnAlbumListener onAlbumListener;


        ViewHolder(@NonNull View itemView, OnAlbumListener setOnAlbumListener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_album);
            albumTitle = itemView.findViewById(R.id.album_title);
            albumArtist = itemView.findViewById(R.id.album_artist);
            albumArt = itemView.findViewById(R.id.album_art);
            onAlbumListener = setOnAlbumListener;
        }
    }

    public interface AlbumArtGetter {
        HashMap<String, Bitmap> getAlbumArtMap();
    }
    public interface OnAlbumListener {
        void onAlbumClick(int position, RelativeLayout relativeLayout);
    }

}

