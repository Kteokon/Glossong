package com.example.glossong.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.glossong.Functions;
import com.example.glossong.R;
import com.example.glossong.UpdateSongActivity;
import com.example.glossong.model.TranslatorResponse;
import com.example.glossong.model.YandexTranslation;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChooseTextDialog extends DialogFragment implements View.OnClickListener{
    public static final int LYRICS = 1;
    public static final int LYRICS_AND_TRANSLATE = 2;
    public static final int TRANSLATION = 3;

    Context context;

    public static ChooseTextDialog newInstance() {
        return new ChooseTextDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_text_dialog, container, false);

        context = getContext();

        Button lyricsButton = view.findViewById(R.id.lyricsButton);
        Button lyricsAndTranslateButton = view.findViewById(R.id.lyricsAndTranslateButton);
        Button translationButton = view.findViewById(R.id.translationButton);

        lyricsButton.setOnClickListener(this);
        lyricsAndTranslateButton.setOnClickListener(this);
        translationButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"text/plain", "application/lrc"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);

        switch (v.getId()) {
            case R.id.lyricsButton: {
                Log.d("mytag", "was chosen lyrics");
                startActivityForResult(intent, LYRICS);
                break;
            }
            case R.id.lyricsAndTranslateButton: {
                Log.d("mytag", "was chosen lyrics and translate");
                startActivityForResult(intent, LYRICS_AND_TRANSLATE);
                break;
            }
            case R.id.translationButton: {
                Log.d("mytag", "was chosen translation");
                startActivityForResult(intent, TRANSLATION);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;

            if(resultData != null) {
                uri = resultData.getData();
                File f = new File(uri.getPath());
                String text = "";
                try {
                    InputStream in = getActivity().getApplicationContext().getContentResolver().openInputStream(uri);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    for (String line; (line = reader.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    text = total.toString();
                } catch (Exception e) {
                    Log.d("Error", "No file " + e.getLocalizedMessage());
                }

                String checkText = text.replaceAll(" ", "");
                if (!checkText.equals("")) {
                    if (requestCode == TRANSLATION) {
                        UpdateSongActivity.translationET.setText(text);
                    }
                    else {
                        UpdateSongActivity.lyricsET.setText(text);
                        if (requestCode == LYRICS_AND_TRANSLATE) {
                            if (new Functions().NetworkIsConnected(getContext())) {
                                TranslatorTask task = new TranslatorTask(getContext());
                                task.execute(text);
                            }
                            else {
                                Toast.makeText(getContext(), "Нет доступа к интернету для перевода файла", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(getContext(), "В указанном файле нет текста", Toast.LENGTH_SHORT).show();
                }
            }
        }
        this.dismiss();
    }
}

class TranslatorTask extends AsyncTask<String, Void, String> {
    Context context;
    String folderId;
    String targetLanguageCode;
    String set_server_url;

    public TranslatorTask(Context context) {
        this.context = context;
        this.folderId = context.getString(R.string.folderId);
        this.targetLanguageCode = context.getString(R.string.targetLanguageCode);
        this.set_server_url = context.getString(R.string.yandex_translator_server_url);
    }

    @Override
    protected String doInBackground(String... strings) {
        TranslatorResponse response = null;
        String res = "";
        try {
            String textToTranslate = strings[0];
            URL url = new URL(set_server_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Api-Key AQVNwN2UTjKub9Iw5BTNBPyI8j-OqGUJhlMEAYEL");
            urlConnection.setDoOutput(true);

            OutputStream stream = urlConnection.getOutputStream();
            String postData = "{\n" + "\"folderId\": \"" + folderId + "\",\r\n" +
                    "\"texts\": [\"" + textToTranslate + "\"],\r\n" +
                    "\"targetLanguageCode\": \"" + targetLanguageCode + "\"}";
            stream.write(postData.getBytes());

            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            response = gson.fromJson(reader, TranslatorResponse.class);
            urlConnection.disconnect();
            YandexTranslation[] texts = response.translations;
            for (int i = 0; i < texts.length; i++) {
                res += texts[i].text + " ";
            }
        } catch (IOException e) {
            Log.d("Error", "Error: " + e.getMessage());
            Toast.makeText(context, "Ошибка доступа к переводчику", Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    @Override
    protected void onPostExecute(String res){
        super.onPostExecute(res);
        UpdateSongActivity.translationET.setText(res);
        Toast.makeText(context, "Текст переведён", Toast.LENGTH_SHORT).show();
    }
}