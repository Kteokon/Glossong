package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class SongDictionary {
    @Embedded
    public Song song;
    @Relation(
            entity = com.example.glossong.model.Dictionary.class,
            parentColumn = "id",
            entityColumn = "eng_word_id"
    )
    public List<EngToRusWord> translations;
}
