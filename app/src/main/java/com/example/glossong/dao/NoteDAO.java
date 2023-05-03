package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.glossong.model.Note;
import com.example.glossong.model.NoteAndSong;
import com.example.glossong.model.NoteWithSongAndArtist;

import java.util.List;

@Dao
public interface NoteDAO {
    @Query("SELECT * FROM note")
    LiveData<List<Note>> selectAll();

    @Transaction
    @Query("SELECT * FROM note")
    LiveData<List<NoteWithSongAndArtist>> selectAllNotesWithSong();

    @Query("SELECT * FROM note WHERE song_id=:id LIMIT 1")
    LiveData<Note> findBySongId(Long id);

    @Insert
    void insert(Note... notes);

    @Delete
    void delete(Note... notes);

    @Update
    void update(Note... notes);
}
