package com.example.glossong;

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
import com.example.glossong.model.Note;
import com.example.glossong.model.NoteAndSong;
import com.example.glossong.model.NoteWithSongAndArtist;
import com.example.glossong.model.Song;

import java.util.ArrayList;
import java.util.List;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.CustomViewHolder> {
    private List<NoteWithSongAndArtist> notes = new ArrayList<>();
    Context context;
    LayoutInflater inflater;

    public NoteListAdapter(Context context, List<NoteWithSongAndArtist> notes) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteListAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.note_item, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteListAdapter.CustomViewHolder holder, int position) {
        Note note = notes.get(position).note;
        Song song = notes.get(position).songAndArtist.song;
        Artist artist = notes.get(position).songAndArtist.artist;

        holder.songNameTV.setText(artist.getName() + " - " + song.getName());
        holder.noteTextTV.setText(note.getNoteText());
        holder.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra("songId", song.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        private TextView songNameTV, noteTextTV;
        ConstraintLayout cl;

        public CustomViewHolder(View view) {
            super(view);

            songNameTV = view.findViewById(R.id.songName);
            noteTextTV = view.findViewById(R.id.noteText);
            cl = view.findViewById(R.id.recycle_view_layout);
        }
    }
}
