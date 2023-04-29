package com.example.glossong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.model.Artist;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Song;
import com.google.android.exoplayer2.ExoPlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.CustomViewHolder>{
    private List<SongAndArtist> songs = new ArrayList<>();
    Context context;
    LayoutInflater inflater;
    boolean sorted = false;
    ExoPlayer player;

    public SongListAdapter(Context context, List<SongAndArtist> songs, ExoPlayer player) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.songs = songs;
        this.player = player;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        int i = position;
        Song song = songs.get(i).song;
        Artist artist = songs.get(i).artist;

        holder.songTV.setText(song.getName());
        holder.artistTV.setText(artist.getName());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyMediaPlayer.getInstance(context).release();
                MyMediaPlayer.nowPlaying = i;

                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("songs", (Serializable) songs);
                intent.putExtra("nowPlaying", i);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterSongs(List<SongAndArtist> filteredSongs) {
        this.songs = songs;
        sorted = true;
        notifyDataSetChanged();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView artistTV,songTV;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            artistTV = view.findViewById(R.id.artist);
            songTV = view.findViewById(R.id.song);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
