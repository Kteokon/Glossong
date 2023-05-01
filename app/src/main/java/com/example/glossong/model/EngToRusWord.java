package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class EngToRusWord {
    @Embedded
    public EngWord word;
    @Relation(
            parentColumn = "eng_word_id",
            entityColumn = "rus_word_id",
            associateBy = @Junction(Translation.class)
    )
    public List<RusWord> translations;
}
