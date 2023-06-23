package com.example.glossong;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.glossong.fragment.WordDialog;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Translation;
import com.example.glossong.model.Word;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;
import com.example.glossong.viewmodel.SongViewModel;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.List;

public class Functions {
    WordViewModel wordViewModel;

    public static String getSongDuration(long dur) {
        long songMin = dur / 1000 / 60;
        long songSec = dur / 1000 % 60;
        String res = Long.toString(songMin) + ":";
        if (songMin / 10 == 0) {
            res = "0" + res;
        }
        if (songSec / 10 == 0) {
            res = res + "0" + Long.toString(songSec);
        }
        else {
            res = res + Long.toString(songSec);
        }
        return res;
    }

    public void addWord(ViewModelStoreOwner viewModelStoreOwner, String word, List<String> translations, Long songId) {
        wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);
        List<WordTuple> words = wordViewModel.getWordBySpelling(word); // Находим слово по написанию
        EngWord engWord;
        long engWordId;
        if (words.size() == 0 || words.get(0) == null) { // Если таких нет
            engWord = new EngWord(word); // То это новое слово
            engWordId = wordViewModel.insert(engWord);
        }
        else {
            engWordId = words.get(0).wordId; // Старое слово, получаем его id
        }
        for (int i = 0; i < translations.size(); i++) { // Цикл по переводам
            words = wordViewModel.getWordBySpelling(translations.get(i)); // Находим перевод по написанию
            RusWord rusWord;
            long rusWordId;
            if (words.size() == 0 || words.get(0) == null) { // Если таких нет
                rusWord = new RusWord(translations.get(i)); // То это новое слово
                rusWordId = wordViewModel.insert(rusWord);
            }
            else {
                rusWordId = words.get(0).wordId; // Старое слово, получаем его id
            }
            Translation translation = new Translation();
            translation.setEngWordId(engWordId);
            translation.setRusWordId(rusWordId);
            if (wordViewModel.getTranslation(translation) == null) { // Если связи между словами нет
                wordViewModel.insert(translation);
            }
            if (songId != -1) {
                List<Dictionary> dictionary = wordViewModel.getSongDictionaryByWordId(songId, engWordId);
                if (dictionary.size() == 0) {
                    Dictionary newDictionary = new Dictionary();
                    newDictionary.setSongId(songId);
                    newDictionary.setEngWordId(engWordId);
                    wordViewModel.insert(newDictionary);
                }
            }
        }
    }

    public boolean NetworkIsConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void deleteSong(ViewModelStoreOwner viewModelStoreOwner, Song song, boolean deleteWords) {
        SongViewModel songViewModel = new ViewModelProvider(viewModelStoreOwner).get(SongViewModel.class);
        Long songId = song.getId();
        wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);

        List<EngToRusWord> songDictionary = wordViewModel.getSongWords(song.getId());
        for (EngToRusWord engToRusWord: songDictionary) {
            EngWord engWord = engToRusWord.word;
            Long engWordId = engWord.getId();
            Dictionary dictionary = new Dictionary();
            dictionary.setSongId(songId);
            dictionary.setEngWordId(engWordId);
            wordViewModel.delete(dictionary);
            if (deleteWords) { // Если пользователь согласен удалить слова, то удаляем связь с песней
                List<Song> songs = wordViewModel.getWordInSongs(engWordId).songs;
                if (songs.size() == 0) { // Если слово больше нигде не осталось
                    deleteTranslations(viewModelStoreOwner, engToRusWord);
                }
            }
        }
        songViewModel.delete(song);
    }

    public static void deleteTranslations(ViewModelStoreOwner viewModelStoreOwner, EngToRusWord engWord) {
        WordViewModel wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);
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

    public static void deleteWord(ViewModelStoreOwner viewModelStoreOwner, EngToRusWord engToRusWord, Long songId) {
        long engWordId = engToRusWord.word.getId();
        WordViewModel wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);
        if (songId == -1L) {
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            deleteTranslations(viewModelStoreOwner, engToRusWord);
            List<Song> songs = wordInSongs.songs;
            for (Song song: songs) {
                deleteDictionary(viewModelStoreOwner, song.getId(), engWordId);
            }
        }
        else {  // Если слово больше не содержится ни в одной из песен, то удалить слово, его перевод и связь
            WordInSongs wordInSongs = wordViewModel.getWordInSongs(engWordId); // Получаем слово и песни, в которых оно находится
            if (wordInSongs.songs.size() == 1) { // Если слово привязано только к одной песне
                deleteTranslations(viewModelStoreOwner, engToRusWord);
            }
            deleteDictionary(viewModelStoreOwner, songId, engWordId);
        }
    }

    public static void deleteTranslation(ViewModelStoreOwner viewModelStoreOwner, EngToRusWord engToRusWord, int index, Long songId, WordDialog wordDialog) {
        WordViewModel wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);

        if (engToRusWord.translations.size() == 1) { // Если у слова остался один перевод, то удаляем полностью
            deleteWord(viewModelStoreOwner, engToRusWord, songId);
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

    private static void deleteDictionary(ViewModelStoreOwner viewModelStoreOwner, Long songId, Long engWordId) {
        WordViewModel wordViewModel = new ViewModelProvider(viewModelStoreOwner).get(WordViewModel.class);

        Dictionary dictionary = new Dictionary();
        dictionary.setSongId(songId);
        dictionary.setEngWordId(engWordId);
        wordViewModel.delete(dictionary);
    }
}
