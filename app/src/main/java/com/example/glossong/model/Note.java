package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "note",
        foreignKeys = {@ForeignKey(entity = Song.class,
                parentColumns = "id",
                childColumns = "song_id",
                onDelete = ForeignKey.CASCADE)
        })
public class Note  implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;

    @ColumnInfo(name = "note_text")
    private String noteText;

    @ColumnInfo(name = "song_id")
    @NonNull
    private Long songId;

    public Note(String noteText, @NonNull Long songId) {
        this.noteText = noteText;
        this.songId = songId;
    }

    public Integer getId() {
        return this.id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNoteText() {
        return this.noteText;
    }
    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public Long getSongId() {
        return this.songId;
    }
    public void setSongId(Long songId) {
        this.songId = songId;
    }
}
