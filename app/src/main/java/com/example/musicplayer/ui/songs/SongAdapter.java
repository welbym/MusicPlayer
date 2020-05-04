package com.example.musicplayer.ui.songs;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songList;
    private OnSongListener onSongListener;

    SongAdapter(ArrayList<Song> setSongList, OnSongListener setOnSongListener) {
        songList = setSongList;
        onSongListener = setOnSongListener;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false), onSongListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Song currentSong = songList.get(position);
        holder.songTitle.setText(currentSong.getTitle());
        holder.songArtist.setText(currentSong.getArtist());
        holder.songAlbumArt.setImageBitmap(currentSong.getAlbumArt());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onSongListener.onSongClick(position, holder.parentLayout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        TextView songTitle;
        TextView songArtist;
        ImageView songAlbumArt;
        OnSongListener onSongListener;


        ViewHolder(@NonNull View itemView, OnSongListener setOnSongListener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_song);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            songAlbumArt = itemView.findViewById(R.id.album_art);
            onSongListener = setOnSongListener;
        }
    }

    public interface OnSongListener {
        void onSongClick(int position, RelativeLayout relativeLayout);
    }

}

