package com.example.glossong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.glossong.fragment.AddWordDialog;
import com.example.glossong.fragment.WordDialog;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Translation;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.List;

public class DictionaryActivity extends AppCompatActivity {
    RecyclerView wordList;

    WordViewModel wordViewModel;

    Long songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        wordList = findViewById(R.id.wordList);
        ImageButton backButton = findViewById(R.id.back);
        ImageButton addWordButton = findViewById(R.id.addWord);
        wordList.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        songId = intent.getLongExtra("songId", -1);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        if (songId == -1) {
            wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
                @Override
                public void onChanged(List<EngToRusWord> words) {
                    DictionaryAdapter adapter = new DictionaryAdapter(getApplicationContext(), words, getSupportFragmentManager());
                    wordList.setAdapter(adapter);
                }
            });
        }
        else {
            wordViewModel.getSongDictionary(songId).observe(this, new Observer<List<EngToRusWord>>() {
                @Override
                public void onChanged(List<EngToRusWord> words) {
                    DictionaryAdapter adapter = new DictionaryAdapter(getApplicationContext(), words, getSupportFragmentManager());
                    wordList.setAdapter(adapter);
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = AddWordDialog.newInstance(songId);
                dialog.show(getSupportFragmentManager(), "tag");
            }
        });
    }

    public void deleteWord(EngToRusWord engToRusWord) {
        long engWordId = engToRusWord.word.getId();
        Log.d("mytag", "deleting");
        if (songId == -1) {
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            deleteTranslations(engToRusWord);
            List<Song> songs = wordInSongs.songs;
            for (Song song: songs) {
                deleteDictionary(song.getId(), engWordId);
            }
        }
        else {  // Если слово больше не содержится ни в одной из песен, то удалить слово, его перевод и связь
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            if (wordInSongs.songs.size() == 1) { // Если слово привязано только к одной песне
                deleteTranslations(engToRusWord);
            }
            deleteDictionary(songId, engWordId);
        }
    }

    public void deleteTranslation(EngToRusWord engToRusWord, int index, WordDialog wordDialog) {
        if (engToRusWord.translations.size() == 1) { // Если у слова остался один перевод, то удаляем полностью
            deleteWord(engToRusWord);
            wordDialog.dismiss();
        }
        else { // Иначе удаляем перевод и связь с ним
            Long engWordId = engToRusWord.word.getId();
            Long rusWordId = engToRusWord.translations.get(index).getId();

            RusToEngWord rusToEngWord = wordViewModel.getRusToEngWords(engToRusWord.translations.get(index).getSpelling());
            if (rusToEngWord.translations.size() == 1) { // Если этот перевод есть только в одном слове, то удаляем перевод
                wordViewModel.delete(engToRusWord.translations.get(index));
            } // Иначе оставить для других слов

            Translation translation = new Translation();
            translation.setEngWordId(engWordId);
            translation.setRusWordId(rusWordId);
            wordViewModel.delete(translation);
        }
    }

    private void deleteTranslations(EngToRusWord engWord) {
        long engWordId = engWord.word.getId();
        List<RusWord> translations = engWord.translations; // Переводы английского слова
        wordViewModel.delete(engWord.word); // Удаляем английское слово и получаем id
        for (RusWord rusWord: translations) {
            RusToEngWord rusToEngWords = wordViewModel.getRusToEngWords(rusWord.getSpelling()); // Получаем русское слово и его переводы
            int amountOfWords = rusToEngWords.translations.size(); // Получаем количество переводов русского слова
            long rusWordId = rusToEngWords.word.getId();
            if (amountOfWords == 0) { // Если у него не осталось слов на английском
                wordViewModel.delete(rusToEngWords.word);
            }
            Translation translation = new Translation();
            translation.setEngWordId(engWordId);
            translation.setRusWordId(rusWordId);
            wordViewModel.delete(translation);
        }
    }

    private void deleteDictionary(Long songId, Long engWordId) {
        Dictionary dictionary = new Dictionary();
        dictionary.setSongId(songId);
        dictionary.setEngWordId(engWordId);
        wordViewModel.delete(dictionary);
    }
}