package com.example.glossong.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.glossong.SongRepository;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.Song;

import java.util.List;

public class SongViewModel extends AndroidViewModel {
    private SongRepository repository;
    private LiveData<List<SongAndArtist>> songs;

    public SongViewModel(@NonNull Application application) {
        super(application);

        this.repository = new SongRepository(application);
        this.songs = repository.getSongs();
    }

    public void insert(Song song) {
        this.repository.insert(song);
    }

    public void update(Song song) {
        this.repository.update(song);
    }

    public void delete(Song song) {
        this.repository.delete(song);
    }

    public LiveData<List<SongAndArtist>> getSongs() {
        return this.songs;
    }
}
