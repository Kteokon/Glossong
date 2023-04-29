package com.example.glossong.model;

import androidx.annotation.NonNull;

public class Word {
    @NonNull
    private String spelling;

    public Word(String spelling) {
        this.spelling = spelling;
    }

    public String getSpelling() {
        return this.spelling;
    }
    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }
}
