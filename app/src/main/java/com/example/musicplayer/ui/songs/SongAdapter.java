package com.example.musicplayer.ui.songs;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ArrayList<Song> songList;
    private Context context;
    private OnSongListener onSongListener;

    public SongAdapter(Context setContext, ArrayList<Song> setSongList, OnSongListener setOnSongListener) {
        context = setContext;
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

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, currentSong.getTitle(), Toast.LENGTH_SHORT).show();
                holder.onSongListener.onSongClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLayout;
        TextView songTitle;
        TextView songArtist;
        OnSongListener onSongListener;


        protected ViewHolder(@NonNull View itemView, OnSongListener setOnSongListener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_song);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
            onSongListener = setOnSongListener;
        }
    }

    public interface OnSongListener {
        void onSongClick(int position);
    }

}

