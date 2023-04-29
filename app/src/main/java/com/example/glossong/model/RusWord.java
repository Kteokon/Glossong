package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rus_word")
public class RusWord extends Word {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    public RusWord(@NonNull String spelling) {
        super(spelling);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
