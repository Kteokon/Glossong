package com.example.glossong.model;

import androidx.room.DatabaseView;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

@DatabaseView
public class DictionaryWithTranslations {
    @Embedded
    public Dictionary dictionary;
    @Relation(
            entity = Translation.class,
            parentColumn = "eng_word_id",
            entityColumn = "eng_word_id"
    )
    public List<EngToRusWord> translations;
}
