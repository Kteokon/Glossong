package com.example.glossong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.glossong.model.Note;
import com.example.glossong.viewmodel.NoteViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_TEXT = "com.example.glossong.NOTE_TEXT";

    Note note;
    Long songId;
    boolean isNew = false;

    EditText textET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        textET = findViewById(R.id.text);

        Intent intent = getIntent();

        note = (Note) intent.getSerializableExtra("note");
        songId = intent.getLongExtra("songId", 0);

        if (note == null) {
            Log.d("mytag", "new note");
            isNew = true;
            textET.setText("");
        }
        else{
            Log.d("mytag", "existed note of song " + note.getSongId());
            textET.setText(note.getNoteText());
        }
    }

    public void saveChanges(View v) {
        Intent intent = new Intent();

        String text = textET.getText().toString();
        text = text.replaceAll(" ", "");

        NoteViewModel noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        if (!text.equals("")) {
            if (isNew) {
                intent.putExtra(NoteActivity.NOTE_TEXT, text);
            }
            else {
                note.setNoteText(text);
                Log.d("mytag", "" + note.getId() + " " + note.getNoteText() + " " + note.getSongId());
                noteViewModel.update(note);
            }
        }
        else {
            if (!isNew) {
                noteViewModel.delete(note);
            }
        }

        setResult(RESULT_OK, intent);
        finish();
    }

    public void cancelChanges(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}