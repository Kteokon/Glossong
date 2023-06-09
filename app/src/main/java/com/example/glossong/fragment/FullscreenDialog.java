package com.example.glossong.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
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
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.Functions;
import com.example.glossong.R;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Translation;
import com.example.glossong.model.YandexDictionary;
import com.example.glossong.tuple.WordTuple;
import com.example.glossong.viewmodel.WordViewModel;
import com.google.android.material.color.MaterialColors;
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
    View separateLine;

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
        separateLine = view.findViewById(R.id.separateLine);
        Button lyricsButton = view.findViewById(R.id.lyricsButton);
        Button translationButton = view.findViewById(R.id.translationButton);
        ImageButton closeButton = view.findViewById(R.id.closeButton);
        bottomMenu = view.findViewById(R.id.bottomMenu);
        ImageButton addButton = view.findViewById(R.id.addToDictionaryButton);

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
                                DictionaryTask task = new DictionaryTask();
                                clickedWord = word;
                                String set_server_url = getString(R.string.yandex_dictionary_server_url);
                                String theKey = getString(R.string.dictionaryKey);
                                String lang = getString(R.string.lang);
                                String what = set_server_url + "?key=" + theKey + "&lang=" + lang + "&text=" + word;
                                if (new Functions().NetworkIsConnected(getContext())) {
                                    try {
                                        translations = task.execute(what).get();
                                        bottomMenu.setVisibility(View.VISIBLE);
                                    } catch (ExecutionException e) {
                                        Log.d("mytag", e.getLocalizedMessage());
                                    } catch (InterruptedException e) {
                                        Log.d("mytag", e.getLocalizedMessage());
                                    }
                                }
                                else {
                                    bottomMenu.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Нет доступа к интернету", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void updateDrawState(@NonNull TextPaint ds) {
                                super.updateDrawState(ds);
                                int color = MaterialColors.getColor(getContext(), androidx.appcompat.R.attr.theme, Color.BLACK);
                                ds.setColor(color);
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
                translationSV.scrollTo(0, scrollY);

//                double scrollLyricsHeight = lyricsTV.getHeight() - lyricsSV.getHeight();
//                double scrollTranslationHeight = translationTV.getHeight() - translationSV.getHeight();
//
//                double x = (scrollY / scrollLyricsHeight) * 100;
//                Log.d("mytag", "" + scrollLyricsHeight  + " " + scrollTranslationHeight + " " + scrollY + " " + x);
//                translationSV.scrollTo(0, (int) ((x * scrollTranslationHeight) / 100));
            }
        });

        translationSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                lyricsSV.scrollTo(0, scrollY);

//                double scrollTranslationHeight = translationTV.getHeight() - translationSV.getHeight();
//                double scrollLyricsHeight = lyricsTV.getHeight() - lyricsSV.getHeight();
//                double x = (scrollY / scrollTranslationHeight) * 100;
//                lyricsSV.scrollTo(0, (int) ((x * scrollLyricsHeight) / 100));
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
            case R.id.lyricsButton: { // Нажали на ru
                if (lyricsSV.getVisibility() == View.GONE) { // Текст не виден
                    lyricsSV.setVisibility(View.VISIBLE);
                    separateLine.setVisibility(View.VISIBLE);
                }
                else {
                    if (translationSV.getVisibility() == View.VISIBLE) { // Текст виден, перевод виден
                        translationSV.setVisibility(View.GONE);
                        separateLine.setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.translationButton: {
                if (translationSV.getVisibility() == View.GONE) {
                    translationSV.setVisibility(View.VISIBLE);
                    separateLine.setVisibility(View.VISIBLE);
                }
                else {
                    if (lyricsSV.getVisibility() == View.VISIBLE) {
                        lyricsSV.setVisibility(View.GONE);
                        bottomMenu.setVisibility(View.GONE);
                        separateLine.setVisibility(View.GONE);
                    }
                }
                break;
            }
            case R.id.addToDictionaryButton: {
                new Functions().addWord(this, clickedWord, translations, songId);
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
}
