package com.example.glossong.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.R;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Translation;
import com.example.glossong.model.YandexDictionary;
import com.example.glossong.tuple.WordTuple;
import com.example.glossong.viewmodel.WordViewModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FullscreenDialog extends DialogFragment implements View.OnClickListener {
    ScrollView lyricsSV, translationSV;
    TextView lyricsTV, translationTV, wordsTV;
    RelativeLayout bottomMenu;

    Long songId;
    String clickedWord = "";
    List<String> translations;

    public static FullscreenDialog newInstance(Long songId, String lyrics, String translation) {
        FullscreenDialog myFullscreenDialog = new FullscreenDialog();
        Bundle args = new Bundle();
        args.putLong("songId", songId);
        args.putString("lyrics", lyrics);
        args.putString("translation", translation);
        myFullscreenDialog.setArguments(args);
        return myFullscreenDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fullscreen_dialog, container, false);

        lyricsSV = view.findViewById(R.id.svlyrics);
        translationSV = view.findViewById(R.id.svtranslation);
        lyricsTV = view.findViewById(R.id.lyrics);
        translationTV = view.findViewById(R.id.translation);
        wordsTV = view.findViewById(R.id.words);
        Button closeButton = view.findViewById(R.id.closeButton);
        Button lyricsButton = view.findViewById(R.id.lyricsButton);
        Button translationButton = view.findViewById(R.id.translationButton);
        bottomMenu = view.findViewById(R.id.bottomMenu);
        Button addButton = view.findViewById(R.id.addToDictionaryButton);

        this.songId = getArguments().getLong("songId");
        String lyrics = getArguments().getString("lyrics");
        String translation = getArguments().getString("translation");

        List<Character> symbols = Arrays.asList(new Character[]{' ', '\n', '.', '?', '!', ',', ';', ':'});

        SpannableString ss = new SpannableString(lyrics);

        for (int i = 0; i < lyrics.length(); i++) {
            if (!symbols.contains(lyrics.charAt(i))) {
                int endWord = 0;
                for (int j = i + 1; j < lyrics.length(); j++) {
                    if (symbols.contains(lyrics.charAt(j))) {
                        endWord = j;
                    }
                    else {
                        if (j == lyrics.length() - 1) {
                            endWord = lyrics.length();
                        }
                    }
                    if (endWord != 0) {
                        String word;
                        word = lyrics.substring(i, endWord);
                        ClickableSpan clickableSpan = new ClickableSpan() {
                            @Override
                            public void onClick(@NonNull View widget) {
                                bottomMenu.setVisibility(View.VISIBLE);
                                Log.d("mytag", "Click");
                                DictionaryTask task = new DictionaryTask();
                                clickedWord = word;
                                String what = "https://dictionary.yandex.net/api/v1/dicservice.json/lookup?key=dict.1.1.20230215T074722Z.df778d51e0ebdb09.260d7a33d7d4fa79d4df13fc548ce0817721cc4b&lang=en-ru&text=" + word;
                                try {
                                    translations = task.execute(what).get();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                ds.setColor(Color.YELLOW);
                                ds.setUnderlineText(false);
                            }
                        };
                        ss.setSpan(clickableSpan, i, endWord, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        i = j - 1;
                        break;
                    }
                }
            }
        }

        lyricsTV.setText(ss);
        lyricsTV.setMovementMethod(LinkMovementMethod.getInstance());
        translationTV.setText(translation);

        lyricsSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                translationSV.scrollTo(0, lyricsSV.getScrollY());
            }
        });

        translationSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                lyricsSV.scrollTo(0, translationSV.getScrollY());
            }
        });

        closeButton.setOnClickListener(this);
        lyricsButton.setOnClickListener(this);
        translationButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.closeButton: {
                dismiss();
                break;
            }
            case R.id.lyricsButton: {
                if (lyricsSV.getVisibility() == View.GONE) {
                    lyricsSV.setVisibility(View.VISIBLE);
                }
                else {
                    if (translationSV.getVisibility() == View.VISIBLE) {
                        translationSV.setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.translationButton: {
                if (translationSV.getVisibility() == View.GONE) {
                    translationSV.setVisibility(View.VISIBLE);
                }
                else {
                    if (lyricsSV.getVisibility() == View.VISIBLE) {
                        lyricsSV.setVisibility(View.GONE);
                        bottomMenu.setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.addToDictionaryButton: {
                addWord();
                break;
            }
        }
    }

    class DictionaryTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... strings) {
            YandexDictionary dictionary = null;
            List<String> words = new ArrayList<>();
            try {
                String url = strings[0];
                URL dictionary_url = new URL(url);
                InputStream stream = (InputStream) dictionary_url.getContent();
                Gson gson = new Gson();
                dictionary = gson.fromJson(new InputStreamReader(stream), YandexDictionary.class);
            } catch (IOException e) {
                Log.d("mytag", e.getLocalizedMessage());
            }
            if (dictionary.def.length != 0) {
                clickedWord = dictionary.def[0].text;
                for (int i = 0; i < dictionary.def.length; i++) {
                    for (int j = 0; j < dictionary.def[i].tr.length; j++) {
                        words.add(dictionary.def[i].tr[j].text);
                    }
                }
            }
            return words;
        }

        @Override
        protected void onPostExecute(List<String> words){
            super.onPostExecute(words);
            String res = "";
            boolean firstWord = true;
            for (int i = 0; i < words.size(); i++) {
                if (firstWord) {
                    res += words.get(i);
                    firstWord = false;
                }
                else {
                    res += (", " + words.get(i));
                }
            }
            wordsTV.setText(res);
        }
    }

    private void addWord() {
        WordViewModel wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        boolean firstWord = false, secondWord = false; // Первое слово - английское, второе - русское
        List<WordTuple> word = wordViewModel.getWordBySpelling(clickedWord); // Находим слово по написанию
        EngWord engWord;
        long engWordId;
        if (word.size() == 0 || word.get(0) == null) { // Если таких нет
            engWord = new EngWord(clickedWord); // То это новое слово
            engWordId = wordViewModel.insert(engWord);
            firstWord = true;
        }
        else {
            engWordId = word.get(0).wordId; // Старое слово, получаем его id
        }
        for (int i = 0; i < translations.size(); i++) { // Цикл по переводам
            word = wordViewModel.getWordBySpelling(translations.get(i)); // Находим слово по написанию
            RusWord rusWord;
            long rusWordId;
            if (word.size() == 0 || word.get(0) == null) { // Если таких нет
                rusWord = new RusWord(translations.get(i)); // То это новое слово
                rusWordId = wordViewModel.insert(rusWord);
                secondWord = true;
            }
            else {
                rusWordId = word.get(0).wordId; // Старое слово, получаем его id
            }
            if (firstWord || secondWord) { // Если хотя бы одно из слов новое, то добавляем в общий словарь
                Translation translation = new Translation();
                translation.setEngWordId(engWordId);
                translation.setRusWordId(rusWordId);
                wordViewModel.insert(translation);
            }
            List<Dictionary> dictionary = wordViewModel.getSongDictionaryByWordId(songId, engWordId);
            if (dictionary.size() == 0) {
                Dictionary newDictionary = new Dictionary();
                newDictionary.setSongId(this.songId);
                newDictionary.setEngWordId(engWordId);
                wordViewModel.insert(newDictionary);
            }
        }
    }
}
