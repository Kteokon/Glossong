package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.Song;

import java.util.List;

@Dao
public interface SongDAO {
    @Transaction
    @Query("SELECT * FROM song")
    public LiveData<List<SongAndArtist>> getSongsWithArtists();

    @Insert
    void insert(Song... songs);

    @Delete
    void delete(Song... songs);

    @Update
    void update(Song... songs);
}
