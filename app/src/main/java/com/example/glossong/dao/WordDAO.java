package com.example.glossong.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Translation;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;

import java.util.List;

@Dao
public interface WordDAO {
    @Insert
    long insert(EngWord engWord);
    @Insert
    long insert(RusWord rusWord);
    @Insert
    void insert(Translation... translations);
    @Insert
    void insert(Dictionary... dictionaries);

    @Update
    void update(EngWord... engWords);
    @Update
    void update(RusWord... rusWords);

    @Delete
    int delete(EngWord... engWords);
    @Delete
    int delete(RusWord... rusWords);
    @Delete
    int delete(Translation... translations);
    @Delete
    void delete(Dictionary... dictionaries);

    @Query("SELECT * FROM dictionary where song_id LIKE :songId AND eng_word_id LIKE :engWordId")
    List<Dictionary> findSongDictionaryByWordId(Long songId, Long engWordId);
    @Query("SELECT * FROM translation where eng_word_id LIKE :engWordId AND rus_word_id LIKE :rusWordId")
    Translation findTranslation(Long engWordId, Long rusWordId);
    @Query("SELECT spelling, id as wordId FROM eng_word where spelling=:spelling union SELECT spelling, id as wordId FROM rus_word where spelling=:spelling")
    List<WordTuple> findWordBySpelling(String spelling);
    @Transaction
    @Query("SELECT * FROM eng_word")
    LiveData<List<EngToRusWord>> findEngToRusWords();
    @Transaction
    @Query("SELECT eng_word.* FROM eng_word INNER JOIN dictionary ON eng_word.id=dictionary.eng_word_id WHERE dictionary.song_id=:id ")
    LiveData<List<EngToRusWord>> findSongDictionary(Long id);
    @Transaction
    @Query("SELECT eng_word.* FROM eng_word INNER JOIN dictionary ON eng_word.id=dictionary.eng_word_id WHERE dictionary.song_id=:id ")
    List<EngToRusWord> findSongWords(Long id);
    @Transaction
    @Query("SELECT * FROM eng_word where id=:id")
    LiveData<EngToRusWord> findEngToRusWordsByEngId(Long id);

    @Transaction
    @Query("SELECT * FROM eng_word where spelling=:spelling")
    EngToRusWord findEngToRusWordsBySpelling(String spelling);
    @Transaction
    @Query("SELECT * FROM rus_word where spelling=:spelling")
    RusToEngWord findRusToEngWordsBySpelling(String spelling);
    @Transaction
    @Query("SELECT * FROM eng_word where id=:id")
    WordInSongs findWordInSongs(Long id);
}
