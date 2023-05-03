package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class SongWithWords {
    @Embedded
    public Song song;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = Dictionary.class,
                    parentColumn = "song_id",
                    entityColumn = "eng_word_id")
    )
    public List<EngWord> words;
}
