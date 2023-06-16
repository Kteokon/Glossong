package com.example.glossong.fragment;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.glossong.Functions;
import com.example.glossong.R;
import com.example.glossong.model.Song;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.YandexDictionary;
import com.example.glossong.viewmodel.SongViewModel;
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

public class LyricsFragment extends Fragment implements View.OnClickListener {
    ScrollView lyricsSV, translationSV;
    TextView lyricsTV, translationTV, wordsTV;
    RelativeLayout bottomMenu;
    View separateLine;

    private int songId = -1;
    int touchedTV = -1;
    String clickedWord = "";
    List<String> translations;

    public LyricsFragment() {
        // Required empty public constructor
    }

    public static LyricsFragment newInstance(int songId) {
        LyricsFragment fragment = new LyricsFragment();
        Bundle args = new Bundle();
        args.putInt("songId", songId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId = getArguments().getInt("songId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lyrics, container, false);

        lyricsSV = view.findViewById(R.id.svlyrics);
        translationSV = view.findViewById(R.id.svtranslation);
        lyricsTV = view.findViewById(R.id.lyrics);
        translationTV = view.findViewById(R.id.translation);
        wordsTV = view.findViewById(R.id.words);
        separateLine = view.findViewById(R.id.separateLine);
        Button lyricsButton = view.findViewById(R.id.lyricsButton);
        Button translationButton = view.findViewById(R.id.translationButton);
        bottomMenu = view.findViewById(R.id.bottomMenu);
        ImageButton addButton = view.findViewById(R.id.addToDictionaryButton);

        SongViewModel songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> songAndArtists) {
                Song song = songAndArtists.get(songId).song;

                String lyrics = song.getLyrics();
                String translation = song.getTranslation();
                SpannableString ss = new SpannableString(lyrics);
                String[] words = lyrics.split("\\s+");
                String newLyrics = lyrics;
                for (int i = 0; i < words.length; i++) {
                    String word = words[i];
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
                            int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                                    Configuration.UI_MODE_NIGHT_MASK;
                            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                                ds.setColor(getResources().getColor(R.color.floral_white, getContext().getTheme()));
                            }
                            else {
                                ds.setColor(getResources().getColor(R.color.jet, getContext().getTheme()));
                            }
                            ds.setUnderlineText(false);
                        }
                    };
                    int startWord = newLyrics.indexOf(word);
                    int endWord = startWord + word.length();
                    ss.setSpan(clickableSpan, startWord, endWord, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    for (int j = startWord; j <= endWord; j++) {
                        if (j != newLyrics.length()) {
                            newLyrics = newLyrics.substring(0, j) + '_' + newLyrics.substring(j + 1);
                        }
                        else {
                            newLyrics = newLyrics.substring(j) + '_';
                        }
                    }
                }

                Log.d("mytag", lyrics);
                lyricsTV.setText(ss);
                lyricsTV.setMovementMethod(LinkMovementMethod.getInstance());
                translationTV.setText(translation);
            }
        });

        lyricsSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchedTV = v.getId();
                return false;
            }
        });

        translationSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchedTV = v.getId();
                return false;
            }
        });

        lyricsSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (touchedTV == v.getId()) {
                    double scrollLyricsHeight = lyricsTV.getHeight() - lyricsSV.getHeight();
                    double scrollTranslationHeight = translationTV.getHeight() - translationSV.getHeight();
                    double x = (scrollY / scrollLyricsHeight) * 100;
                    Log.d("mytag", "" + lyricsTV.getHeight() + " " + lyricsSV.getHeight() + " " + scrollY);
                    translationSV.scrollTo(0, (int) ((x * scrollTranslationHeight) / 100));
                }
            }
        });

        translationSV.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (touchedTV == v.getId()) {
                    double scrollTranslationHeight = translationTV.getHeight() - translationSV.getHeight();
                    double scrollLyricsHeight = lyricsTV.getHeight() - lyricsSV.getHeight();
                    double x = (scrollY / scrollTranslationHeight) * 100;
                    lyricsSV.scrollTo(0, (int) ((x * scrollLyricsHeight) / 100));
                }
            }
        });

        lyricsButton.setOnClickListener(this);
        translationButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.lyricsButton: {
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
                new Functions().addWord(this, clickedWord, translations, (long) songId);
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
                try {
                    clickedWord = dictionary.def[0].text;
                    for (int i = 0; i < dictionary.def.length; i++) {
                        for (int j = 0; j < dictionary.def[i].tr.length; j++) {
                            words.add(dictionary.def[i].tr[j].text);
                        }
                    }
                } catch (NullPointerException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bottomMenu.setVisibility(View.GONE);
                        }
                    });
                }
            }
            else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomMenu.setVisibility(View.GONE);
                    }
                });
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