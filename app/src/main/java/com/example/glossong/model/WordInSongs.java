package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class WordInSongs {
    @Embedded
    public EngWord word;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = Dictionary.class,
                    parentColumn = "eng_word_id",
                    entityColumn = "song_id")
    )
    public List<Song> songs;
}
