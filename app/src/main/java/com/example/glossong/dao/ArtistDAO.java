package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.glossong.model.Artist;

import java.util.List;

@Dao
public interface ArtistDAO {
    @Query("SELECT * FROM artist")
    LiveData<List<Artist>> selectAll();

    @Query("SELECT * FROM artist WHERE name=:name")
    LiveData<Artist> findByName(String name);

    @Insert
    long insert(Artist artist);

    @Delete
    void delete(Artist... artists);

    @Update
    void update(Artist... artists);
}
