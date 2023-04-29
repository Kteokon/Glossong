package com.example.glossong.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.glossong.NoteRepository;
import com.example.glossong.model.Note;
import com.example.glossong.tuple.NoteTuple;

import java.util.concurrent.ExecutionException;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;

    public NoteViewModel(@NonNull Application application) {
        super(application);

        this.repository = new NoteRepository(application);
    }

    public void insert(Note note) {
        this.repository.insert(note);
    }

    public void update(Note note) {
        this.repository.update(note);
    }

    public void delete(Note note) {
        this.repository.delete(note);
    }

    public LiveData<Note> getNoteBySongId(Long id) {
        return this.repository.getNoteBySongId(id);
    }
}
