package com.example.glossong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.glossong.model.Artist;
import com.example.glossong.model.TranslatorResponse;
import com.example.glossong.model.YandexTranslation;
import com.example.glossong.viewmodel.ArtistViewModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UpdateSongActivity extends AppCompatActivity {
    public static final String ARTIST_ID = "com.example.musiclearning.ARTIST_ID";
    public static final String ARTIST_NAME = "com.example.musiclearning.ARTIST_NAME";
    public static final String SONG_NAME = "com.example.musiclearning.SONG_NAME";
    public static final String SONG_LYRICS = "com.example.musiclearning.SONG_LYRICS";
    public static final String SONG_TRANSLATION = "com.example.musiclearning.SONG_TRANSLATION";
    public static final int ADD_SONG_LYRICS = 3;

    EditText songNameET, artistNameET;
    String songLyrics;
    String songTranslation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_song);

        songNameET = findViewById(R.id.songName);
        artistNameET = findViewById(R.id.artistName);

        Intent intent = getIntent();
        songNameET.setText(intent.getStringExtra(SONG_NAME));
        artistNameET.setText(intent.getStringExtra(ARTIST_NAME));
        songLyrics = intent.getStringExtra(SONG_LYRICS);
        songTranslation = intent.getStringExtra(SONG_TRANSLATION);
    }

    public void onClick(View v) throws ExecutionException, InterruptedException {
        Intent intent = new Intent();

        if (v.getId() == R.id.saveButton) {
            intent.putExtra("button", "save");
        }
        else {
            if (v.getId() == R.id.deleteButton){
                intent.putExtra("button", "delete");
            }
            else {
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }

        String newName = songNameET.getText().toString();
        String newArtist = artistNameET.getText().toString();
        String newLyrics = songLyrics;
        String newTranslation = songTranslation;

        Long artistId;
        ArtistViewModel artistViewModel = new ViewModelProvider(this).get(ArtistViewModel.class);
        artistId = artistViewModel.getArtistByName(newArtist);
        if (artistId == null) {
            Artist artist = new Artist(newArtist);
            artistId = artistViewModel.insert(artist);
        }

        intent.putExtra(UpdateSongActivity.ARTIST_ID, artistId);
        intent.putExtra(UpdateSongActivity.ARTIST_NAME, newArtist);
        intent.putExtra(UpdateSongActivity.SONG_NAME, newName);
        intent.putExtra(UpdateSongActivity.SONG_LYRICS, newLyrics);
        intent.putExtra(UpdateSongActivity.SONG_TRANSLATION, newTranslation);

        setResult(RESULT_OK, intent);
        finish();
    }

    public void addSongLyrics(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"text/plain", "application/lrc"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        startActivityForResult(intent, ADD_SONG_LYRICS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == ADD_SONG_LYRICS && resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                File f = new File(uri.getPath());
                try {
                    InputStream in = getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = reader.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    songLyrics = total.toString();
                } catch (Exception e) {
                    Log.d("Error", "No file " + e.getLocalizedMessage());
                }

                if (!(songLyrics.equals(null) || songLyrics.equals("") || songLyrics.equals("\n"))) {
                    TranslatorTask task = new TranslatorTask();
                    task.execute("hello");
                }
            }
        }
    }

    class TranslatorTask extends AsyncTask<String, Void, String> {
        String theKey = getString(R.string.theKey);
        String folderId = getString(R.string.folderId);
        String targetLanguageCode = getString(R.string.targetLanguageCode);
        String set_server_url = getString(R.string.yandex_translator_server_url);

        @Override
        protected String doInBackground(String... strings) {
            TranslatorResponse response = null;
            String res = "";
            try {
                String textToTranslate = strings[0];
                URL url = new URL(set_server_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Api-Key " + theKey);
                urlConnection.setDoOutput(true);

                OutputStream stream = urlConnection.getOutputStream();
                String postData = "{\n" + "\"folderId\": \"" + folderId + "\",\r\n" +
                        "\"texts\": ['" + textToTranslate + "'],\r\n" +
                        "\"targetLanguageCode\": \"" + targetLanguageCode + "\"}";
                stream.write(postData.getBytes());

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                Gson gson = new Gson();
                response = gson.fromJson(reader, TranslatorResponse.class);
                urlConnection.disconnect();
                YandexTranslation[] texts = response.getTranslations();
                for (int i = 0; i < texts.length; i++) {
                    res += texts[i].getText() + " ";
                }
            } catch (IOException e) {
                Log.d("Error", "Error: " + e.getLocalizedMessage());
            }
            return res;
        }

        @Override
        protected void onPostExecute(String res){
            super.onPostExecute(res);
            songTranslation = res;
        }
    }
}