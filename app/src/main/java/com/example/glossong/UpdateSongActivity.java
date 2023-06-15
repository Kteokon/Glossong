package com.example.glossong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.glossong.fragment.AddWordDialog;
import com.example.glossong.fragment.ChooseTaskDialog;
import com.example.glossong.fragment.ChooseTextDialog;
import com.example.glossong.fragment.DeleteSongDialog;
import com.example.glossong.fragment.DictionaryFragment;
import com.example.glossong.fragment.LyricsFragment;
import com.example.glossong.fragment.PlayerFragment;
import com.example.glossong.model.Artist;
import com.example.glossong.model.ArtistAndSongs;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.Note;
import com.example.glossong.model.Song;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.TranslatorResponse;
import com.example.glossong.model.YandexTranslation;
import com.example.glossong.viewmodel.ArtistViewModel;
import com.example.glossong.viewmodel.WordViewModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UpdateSongActivity extends AppCompatActivity {
    public static final String ARTIST_ID = "com.example.musiclearning.ARTIST_ID";
    public static final String ARTIST_NAME = "com.example.musiclearning.ARTIST_NAME";
    public static final String SONG_NAME = "com.example.musiclearning.SONG_NAME";
    public static final String SONG_LYRICS = "com.example.musiclearning.SONG_LYRICS";
    public static final String SONG_TRANSLATION = "com.example.musiclearning.SONG_TRANSLATION";

    ArtistViewModel artistViewModel;
    ArtistAndSongs artist;

    EditText songNameET, artistNameET;
    public static EditText lyricsET;
    public static EditText translationET;
    String artistName, songName, songLyrics, songTranslation;
    Button lyricsButton, translationButton;

    boolean deleteWords = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_song);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges("save");
            }
        });
        toolbar.setNavigationIcon(R.drawable.save_changes);
        toolbar.setNavigationContentDescription("Сохранить изменения");

        songNameET = findViewById(R.id.songName);
        artistNameET = findViewById(R.id.artistName);
        lyricsET = findViewById(R.id.songLyrics);
        translationET = findViewById(R.id.songTranslation);
        lyricsButton = findViewById(R.id.lyricsButton);
        translationButton = findViewById(R.id.translationButton);

        Intent intent = getIntent();
        artistName = intent.getStringExtra(ARTIST_NAME);
        songName = intent.getStringExtra(SONG_NAME);
        songLyrics = intent.getStringExtra(SONG_LYRICS);
        songTranslation = intent.getStringExtra(SONG_TRANSLATION);

        artistNameET.setText(artistName);
        songNameET.setText(songName);
        if (!songLyrics.equals("")) {
            lyricsET.setText(songLyrics);
        }
        if (!songTranslation.equals("")) {
            translationET.setText(songTranslation);
        }

        artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistViewModel.getArtistByName(artistName).observe(this, new Observer<ArtistAndSongs>() {
            @Override
            public void onChanged(ArtistAndSongs _artist) {
                artist = _artist;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_update_song, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.addLyricsButton: {
                DialogFragment dialog = ChooseTextDialog.newInstance();
                dialog.show(getSupportFragmentManager(), "chooseTextDialog");
                break;
            }
            case R.id.deleteButton: {
                DeleteSongDialog dialog = new DeleteSongDialog();
                dialog.show(getSupportFragmentManager(), "deleteSongAlert");
                break;
            }
        }
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editButton: {
                v.setVisibility(View.GONE);
                if (lyricsET.getVisibility() == View.VISIBLE) {
//                    lyricsET.setFocusableInTouchMode(true);
                }
                if (translationET.getVisibility() == View.VISIBLE) {
//                    translationET.setFocusableInTouchMode(true);
                }
            }
            case R.id.lyricsButton: {
                if (translationET.getVisibility() == View.VISIBLE) {
                    translationET.setVisibility(View.GONE);
                    lyricsET.setVisibility(View.VISIBLE);
//                    translationET.setFocusableInTouchMode(false);
                }
                break;
            }
            case R.id.translationButton: {
                if (lyricsET.getVisibility() == View.VISIBLE) {
                    lyricsET.setVisibility(View.GONE);
                    translationET.setVisibility(View.VISIBLE);
//                    lyricsET.setFocusableInTouchMode(false);
                }
                break;
            }
        }
    }

    public void cancelChanges(View v) {
        saveChanges("cancel");
    }

    public void saveChanges(String button) {
        String newName = songNameET.getText().toString();
        String newArtist = artistNameET.getText().toString();
        String newLyrics = lyricsET.getText().toString();
        String newTranslation = translationET.getText().toString();

        boolean isNameEmpty = false, isArtistEmpty = false;

        String checkSongName = newName.replaceAll(" ", "");
        String checkArtistName = newArtist.replaceAll(" ", "");
        String checkLyrics = newLyrics.replaceAll(" ", "");
        String checkTranslation = newTranslation.replaceAll(" ", "");

        if (checkSongName.equals("")) {
            isNameEmpty = true;
            Toast.makeText(this, "Введите название песни", Toast.LENGTH_LONG).show();
        }
        if (checkArtistName.equals("")) {
            isArtistEmpty = true;
            Toast.makeText(this, "Введите имя автора", Toast.LENGTH_LONG).show();
        }
        if (checkLyrics.equals("")) {
            newLyrics = "Текст песни не загружен";
        }
        if (checkTranslation.equals("")) {
            newTranslation = "Перевод текста песни не загружен";
        }

        if (!(isNameEmpty || isArtistEmpty)) {
            Intent intent = new Intent();

            if (button.equals("save")) {
                intent.putExtra("button", button);
            }
            else {
                if (button.equals("delete")){
//                    MyMediaPlayer.nowPlayingSong.setVisibility(View.GONE);
                    intent.putExtra("deleteWords", deleteWords);
                    intent.putExtra("button", button);
                }
                else {
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
            }

            Long artistId = null;

            try {
                artistId = artistViewModel.getArtistIdByName(newArtist);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (artistId == null) {  // Введёного исполнителя нет в бд
                if (artist.songs.size() == 1 && artist.artist.getId() != 1) { // Если у прошлого исполнителя больше нет произведений и он не неизвестен, то обновить его имя на новое введённое
                    artistId = artist.artist.getId();
                    artist.artist.setName(newArtist);
                    artistViewModel.update(artist.artist);
                }
                else {
                    Artist artist = new Artist(newArtist);
                    artistId = artistViewModel.insert(artist);
                }
            }

            intent.putExtra(UpdateSongActivity.ARTIST_ID, artistId);
            intent.putExtra(UpdateSongActivity.ARTIST_NAME, newArtist);
            intent.putExtra(UpdateSongActivity.SONG_NAME, newName);
            intent.putExtra(UpdateSongActivity.SONG_LYRICS, newLyrics);
            intent.putExtra(UpdateSongActivity.SONG_TRANSLATION, newTranslation);

            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void setDeleteWords(boolean deleteWords) {
        this.deleteWords = deleteWords;
    }
}