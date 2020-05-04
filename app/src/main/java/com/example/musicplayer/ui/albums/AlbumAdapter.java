package com.example.musicplayer.ui.albums;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private ArrayList<Album> albumList;
    private OnAlbumListener onAlbumListener;

    AlbumAdapter(ArrayList<Album> setAlbumList, AlbumAdapter.OnAlbumListener setOnAlbumListener) {
        albumList = setAlbumList;
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

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onAlbumListener.onAlbumClick(position, holder.parentLayout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLayout;
        TextView albumTitle;
        TextView albumArtist;
        OnAlbumListener onAlbumListener;


        ViewHolder(@NonNull View itemView, OnAlbumListener setOnAlbumListener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_album);
            albumTitle = itemView.findViewById(R.id.album_title);
            albumArtist = itemView.findViewById(R.id.album_artist);
            onAlbumListener = setOnAlbumListener;
        }
    }

    public interface OnAlbumListener {
        void onAlbumClick(int position, LinearLayout linearLayout);
    }

}

