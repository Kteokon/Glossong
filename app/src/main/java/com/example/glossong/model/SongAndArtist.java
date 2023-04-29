package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.io.Serializable;

public class SongAndArtist implements Serializable {
    @Embedded
    public Song song;
    @Relation(
            parentColumn = "artist_id",
            entityColumn = "id"
    )
    public Artist artist;
}
