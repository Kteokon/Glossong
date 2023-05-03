package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Relation;

public class NoteWithSongAndArtist {
    @Embedded
    public Note note;
    @Relation(
            entity = Song.class,
            parentColumn = "song_id",
            entityColumn = "id"
    )
    public SongAndArtist songAndArtist;
}
