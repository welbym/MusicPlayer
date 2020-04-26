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

    public SongAdapter(Context setContext, ArrayList<Song> setSongList) {
        context = setContext;
        songList = setSongList;
    }

    @NonNull
    @Override
    public SongAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.song, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Song currentSong = songList.get(position);
        holder.songTitle.setText(currentSong.getTitle());
        holder.songArtist.setText(currentSong.getArtist());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, currentSong.getTitle(), Toast.LENGTH_SHORT).show();
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

        protected ViewHolder(@NonNull View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_song);
            songTitle = itemView.findViewById(R.id.song_title);
            songArtist = itemView.findViewById(R.id.song_artist);
        }
    }

}

