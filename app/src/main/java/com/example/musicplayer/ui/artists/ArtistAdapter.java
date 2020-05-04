package com.example.musicplayer.ui.artists;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private ArrayList<Artist> artistList;
    private ArtistAdapter.OnArtistListener onArtistListener;

    ArtistAdapter(ArrayList<Artist> setArtistList, ArtistAdapter.OnArtistListener setOnArtistListener) {
        artistList = setArtistList;
        onArtistListener = setOnArtistListener;
    }

    @NonNull
    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.artist, parent, false), onArtistListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Artist currentArtist = artistList.get(position);
        holder.artistName.setText(currentArtist.getName());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.onArtistListener.onArtistClick(position, holder.parentLayout);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout parentLayout;
        TextView artistName;
        OnArtistListener onArtistListener;


        ViewHolder(@NonNull View itemView, OnArtistListener setOnArtistListener) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parent_layout_artist);
            artistName = itemView.findViewById(R.id.artist_name);
            onArtistListener = setOnArtistListener;
        }
    }

    public interface OnArtistListener {
        void onArtistClick(int position, LinearLayout linearLayout);
    }

}

