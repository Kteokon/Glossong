package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class NoteAndSong {
    @Embedded
    public Note note;
    @Relation(
            parentColumn = "song_id",
            entityColumn = "id"
    )
    public Song song;
}
