package com.example.glossong.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "song",
        foreignKeys = {@ForeignKey(entity = Artist.class,
                parentColumns = "id",
                childColumns = "artist_id")
        })
public class Song implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @ColumnInfo(name = "artist_id",
            defaultValue = "1")
    @NonNull
    private Long artistId;

    @ColumnInfo(defaultValue = "Без названия")
    @NonNull
    private String name;

    @NonNull
    private String source;

    private String lyrics;

    private String translation;

//    @Ignore
    public Song(@NonNull String source, String lyrics, String translation) {
        this.source = source;
        this.lyrics = lyrics;
        this.translation = translation;
    }

//    @Ignore
//    public Song(@NonNull Integer artistId, @NonNull String source, String lyrics, String translation) {
//        this.artistId = artistId;
//        this.source = source;
//        this.lyrics = lyrics;
//        this.translation = translation;
//    }
//
//    @Ignore
//    public Song(@NonNull String name, @NonNull String source, String lyrics, String translation) {
//        this.name = name;
//        this.source = source;
//        this.lyrics = lyrics;
//        this.translation = translation;
//    }
//
//    public Song(@NonNull Integer artistId, @NonNull String name, @NonNull String source, String lyrics, String translation) {
//        this.artistId = artistId;
//        this.name = name;
//        this.source = source;
//        this.lyrics = lyrics;
//        this.translation = translation;
//    }

    public Long getId(){
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getArtistId(){
        return this.artistId;
    }
    public void setArtistId(Long artistId) {
        this.artistId = artistId;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSource(){
        return this.source;
    }
    public void setSource(String source) {
        this.source = source;
    }

    public String getLyrics(){
        return this.lyrics;
    }
    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getTranslation(){
        return this.translation;
    }
    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
