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

    NoteViewModel noteViewModel;

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

        songId = intent.getLongExtra("songId", 0);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNoteBySongId(songId).observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note _note) {
                note = _note;
                if (_note == null) {
                    isNew = true;
                    textET.setText("");
                }
                else {
                    textET.setText(_note.getNoteText());
                }
            }
        });
    }

    public void saveChanges(View v) {
        Intent intent = new Intent();

        String text = textET.getText().toString();
        String checkText = text.replaceAll(" ", "");

        if (!checkText.equals("")) {
            if (isNew) {
                Note _note = new Note(text, songId);
                noteViewModel.insert(_note);
            }
            else {
                note.setNoteText(text);
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