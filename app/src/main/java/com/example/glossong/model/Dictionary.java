package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(tableName = "dictionary",
        primaryKeys = {"song_id", "eng_word_id"})
public class Dictionary {
    @ColumnInfo(name = "song_id")
    @NonNull
    private Long songId;

    @ColumnInfo(name = "eng_word_id")
    @NonNull
    private Long engWordId;

    public Long getSongId() {
        return this.songId;
    }
    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public Long getEngWordId() {
        return this.engWordId;
    }
    public void setEngWordId(Long engWordId) {
        this.engWordId = engWordId;
    }
}
