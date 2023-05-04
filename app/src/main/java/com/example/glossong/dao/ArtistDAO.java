package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.glossong.model.Artist;
import com.example.glossong.model.ArtistAndSongs;

@Dao
public interface ArtistDAO {
    @Insert
    long insert(Artist artist);

    @Delete
    void delete(Artist... artists);

    @Update
    void update(Artist... artists);

    @Transaction
    @Query("SELECT * FROM artist WHERE name=:name")
    LiveData<ArtistAndSongs> findArtistByName(String name);

    @Query("SELECT id FROM artist WHERE name=:name")
    Long findArtistIdByName(String name);
}
