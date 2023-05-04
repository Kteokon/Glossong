package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ArtistAndSongs {
    @Embedded
    public Artist artist;
    @Relation(
            parentColumn = "id",
            entityColumn = "artist_id"
    )
    public List<Song> songs;
}
