package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "artist")
public class Artist implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @NonNull
    private String name;

    public Artist(@NonNull String name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
