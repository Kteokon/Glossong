package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "eng_word")
public class EngWord extends Word {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    public EngWord(@NonNull String spelling) {
        super(spelling);
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
