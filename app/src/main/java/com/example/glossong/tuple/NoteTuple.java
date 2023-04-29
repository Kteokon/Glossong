package com.example.glossong.tuple;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

public class NoteTuple {
    @NonNull
    private Integer id;
    private String text;
    @ColumnInfo(name = "song_id")
    @NonNull
    private Long songId;

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public Long getSongId() {
        return this.songId;
    }
    public void setSongId(Long songId) {
        this.songId = songId;
    }
}
