package com.example.glossong.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.glossong.WordRepository;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.SongDictionary;
import com.example.glossong.model.SongWithWords;
import com.example.glossong.model.Translation;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository repository;
    private LiveData<List<EngToRusWord>> engWords;

    public WordViewModel(@NonNull Application application) {
        super(application);

        this.repository = new WordRepository(application);
        this.engWords = repository.getAllEngWords();
    }

    public long insert(EngWord word) {
        long wordId = this.repository.insert(word);
        return wordId;
    }

    public long insert(RusWord word) {
        long wordId = this.repository.insert(word);
        return wordId;
    }

    public void insert(Translation translation) {
        this.repository.insert(translation);
    }

    public void insert(Dictionary dictionary) {
        this.repository.insert(dictionary);
    }

    public long delete(EngWord word) {
        long wordId = this.repository.delete(word);
        return wordId;
    }

    public long delete(RusWord word) {
        long wordId = this.repository.delete(word);
        return wordId;
    }

    public void delete(Translation translation) {
        this.repository.delete(translation);
    }

    public void delete(Dictionary dictionary) {
        this.repository.delete(dictionary);
    }

    public LiveData<List<EngToRusWord>> getEngWords() {
        return this.engWords;
    }

    public LiveData<List<EngToRusWord>> getSongDictionary(Long songId) {
        return this.repository.getSongDictionary(songId);
    }

    public List<WordTuple> getWordBySpelling(String spelling) {
        return this.repository.getWordBySpelling(spelling);
    }

    public List<Dictionary> getSongDictionaryByWordId(Long songId, Long engWordId) {
        return this.repository.getSongDictionaryByWordId(songId, engWordId);
    }

    public EngToRusWord getEngToRusWords(String spelling) {
        return this.repository.getEngToRusWordsBySpelling(spelling);
    }

    public RusToEngWord getRusToEngWords(String spelling) {
        return this.repository.getRusToEngWordsBySpelling(spelling);
    }

    public WordInSongs getWordInSongs(Long wordId) {
        return this.repository.getWordInSongs(wordId);
    }
}
