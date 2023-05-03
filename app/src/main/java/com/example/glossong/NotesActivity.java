package com.example.glossong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.glossong.model.NoteAndSong;
import com.example.glossong.model.NoteWithSongAndArtist;
import com.example.glossong.viewmodel.NoteViewModel;

import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    private RecyclerView noteList;
    Button songsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        noteList = findViewById(R.id.notesList);
        noteList.setLayoutManager(new LinearLayoutManager(this));
        songsButton = findViewById(R.id.songsButton);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNotes().observe(this, new Observer<List<NoteWithSongAndArtist>>() {
            @Override
            public void onChanged(List<NoteWithSongAndArtist> notes) {
                NoteListAdapter adapter = new NoteListAdapter(getApplicationContext(), notes);
                noteList.setAdapter(adapter);
            }
        });

        songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}