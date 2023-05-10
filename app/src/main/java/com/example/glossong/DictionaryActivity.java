package com.example.glossong;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.glossong.fragment.MyAlertDialog;
import com.example.glossong.listener.ItemClickListener;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Translation;
import com.example.glossong.model.Word;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.List;

public class DictionaryActivity extends AppCompatActivity implements ItemClickListener {
    RecyclerView wordList;

    WordViewModel wordViewModel;
    ItemClickListener icl;

    String type;
    Long songId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        // TODO: сделать редактирование слов

        wordList = findViewById(R.id.wordList);
        ImageButton backButton = findViewById(R.id.backButton);
        wordList.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        type = intent.getStringExtra("words");
        icl = this;

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        if (type.equals("song")) {
            songId = intent.getLongExtra("songId", 0);
            if (songId != 0) {
                wordViewModel.getSongDictionary(songId).observe(this, new Observer<List<EngToRusWord>>() {
                    @Override
                    public void onChanged(List<EngToRusWord> words) {
                        DictionaryAdapter adapter = new DictionaryAdapter(getApplicationContext(), words, icl);
                        wordList.setAdapter(adapter);
                    }
                });
            }
        }
        else {
            wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
                @Override
                public void onChanged(List<EngToRusWord> words) {
                    DictionaryAdapter adapter = new DictionaryAdapter(getApplicationContext(), words, icl);
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
    }

    @Override
    public void onItemClick(Word item) {
        FragmentManager manager = getSupportFragmentManager();
        MyAlertDialog dialog = new MyAlertDialog();
        dialog.setItem(item);
        dialog.show(manager, "deleteWordAlert");
    }

    public void deleteWord(Word item) {
        long engWordId;
        WordTuple word = wordViewModel.getWordBySpelling(item.getSpelling()).get(0); // Получаем все слова по написанию, всегда выдаёт одно, английское слово точно есть
        engWordId = word.wordId;
        if (type.equals("song")) { // Если слово больше не содержится ни в одной из песен, то удалить слово, его перевод и связь
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            if (wordInSongs.songs.size() == 1) { // Если слово привязано только к одной песне
                deleteTranslations(item, engWordId);
            }
            deleteDictionary(songId, engWordId);
        }
        else {
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            deleteTranslations(item, engWordId);
            List<Song> songs = wordInSongs.songs;
            for (Song song: songs) {
                deleteDictionary(song.getId(), engWordId);
            }
        }
    }

    private void deleteTranslations(Word item, long engWordId) {
        EngToRusWord engWord = wordViewModel.getEngToRusWords(item.getSpelling()); // Получаем английское слово и его переводы
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