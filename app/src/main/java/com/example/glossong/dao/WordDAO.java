package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Translation;
import com.example.glossong.tuple.WordTuple;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    long insert(EngWord engWord);
    @Insert
    long insert(RusWord rusWord);
    @Insert
    void insert(Translation... Translations);

    @Update
    void update(EngWord... engWords);
    @Update
    void update(RusWord... rusWords);

    @Delete
    int delete(EngWord... engWords);
    @Delete
    int delete(RusWord... rusWords);
    @Delete
    int delete(Translation... Translations);

    @Query("SELECT * FROM eng_word")
    LiveData<List<EngWord>> findAllEngWords();
    @Query("SELECT * FROM rus_word")
    LiveData<List<RusWord>> findAllRusWords();
    @Query("SELECT spelling, id as wordId FROM eng_word where spelling=:spelling union SELECT spelling, id as wordId FROM rus_word where spelling=:spelling")
    List<WordTuple> findWordBySpelling(String spelling);
    @Transaction
    @Query("SELECT * FROM eng_word")
    LiveData<List<EngToRusWord>> findEngWordWithTranslations();

    @Transaction
    @Query("SELECT * FROM eng_word where spelling=:spelling")
    EngToRusWord findEngWordWithTranslationsBySpelling(String spelling);
    @Query("SELECT spelling, id as wordId FROM eng_word where spelling=:spelling union SELECT spelling, id as wordId FROM rus_word where spelling=:spelling")
    List<WordTuple> findWordWithTranslationsBySpelling(String spelling);
}
