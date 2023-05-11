package com.example.glossong.listener;

import com.example.glossong.model.Word;

import java.io.Serializable;

public interface ItemClickListener extends Serializable {
    void onItemClick(Word item);
}
