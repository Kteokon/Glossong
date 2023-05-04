package com.example.glossong.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.glossong.ArtistRepository;
import com.example.glossong.model.Artist;
import com.example.glossong.model.ArtistAndSongs;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArtistViewModel extends AndroidViewModel {
    private ArtistRepository repository;

    public ArtistViewModel(@NonNull Application application) {
        super(application);

        this.repository = new ArtistRepository(application);
    }

    public Long insert(Artist artist) {
        return this.repository.insert(artist);
    }

    public void update(Artist artist) {
        this.repository.update(artist);
    }

    public void delete(Artist artist) {
        this.repository.delete(artist);
    }

    public Long getArtistIdByName(String name) throws ExecutionException, InterruptedException {
        return this.repository.getArtistIdByName(name);
    }

    public LiveData<ArtistAndSongs> getArtistByName(String name) {
        return this.repository.getArtistByName(name);
    }
}
