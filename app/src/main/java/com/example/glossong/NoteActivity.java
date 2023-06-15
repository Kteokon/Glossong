package com.example.glossong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Note;
import com.example.glossong.model.Song;
import com.example.glossong.viewmodel.NoteViewModel;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteActivity extends AppCompatActivity {
    NoteViewModel noteViewModel;

    Note note;
    Long songId;
    boolean isNew = false;

    EditText textET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    deleteNote();
                }
            }
        });
        toolbar.setNavigationIcon(R.drawable.save_changes);
        toolbar.setNavigationContentDescription("Сохранить");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.deleteButton: {
                deleteNote();
                break;
            }
        }
        return true;
    }

    public void cancelChanges(View v) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    private void deleteNote() {
        if (!isNew) {
            noteViewModel.delete(note);
        }
        finish();
    }
}