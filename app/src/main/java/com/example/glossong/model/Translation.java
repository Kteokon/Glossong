package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "translation",
        primaryKeys = {"eng_word_id", "rus_word_id"})
public class Translation {
    @ColumnInfo(name = "eng_word_id")
    @NonNull
    private Long engWordId;

    @ColumnInfo(name = "rus_word_id")
    @NonNull
    private Long rusWordId;

    public Long getEngWordId() {
        return this.engWordId;
    }
    public void setEngWordId(Long engWordId) {
        this.engWordId = engWordId;
    }

    public Long getRusWordId() {
        return this.rusWordId;
    }
    public void setRusWordId(Long rusWordId) {
        this.rusWordId = rusWordId;
    }
}
