package com.example.glossong.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.util.List;

public class EngToRusWord implements Serializable {
    @Embedded
    public EngWord word;
    @Relation(
            parentColumn = "id",
            entityColumn = "id",
            associateBy = @Junction(value = Translation.class,
                    parentColumn = "eng_word_id",
                    entityColumn = "rus_word_id")
    )
    public List<RusWord> translations;
}
