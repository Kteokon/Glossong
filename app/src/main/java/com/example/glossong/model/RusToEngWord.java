package com.example.glossong.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class RusToEngWord {
    @Embedded
    public RusWord word;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = Translation.class,
                    parentColumn = "rus_word_id",
                    entityColumn = "eng_word_id")
    )
    public List<EngWord> translations;
}
