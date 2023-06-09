package com.example.glossong;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.TaskSettings;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClickWordsActivity extends AppCompatActivity {
    LinearLayout llEngWords, llRusWords;
    TextView first, second;

    WordViewModel wordViewModel;

    int emptyColor;

    List<EngToRusWord> words;
    int wordsClicked = 0;
    Long amountOfWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_words);

        llEngWords = findViewById(R.id.engWords);
        llRusWords = findViewById(R.id.rusWords);
        Button exitButton = findViewById(R.id.exitButton);
        Button restartButton = findViewById(R.id.restartButton);
        emptyColor = getResources().getColor(R.color.nothing);

        amountOfWords = TaskSettings.amountOfWords;

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> _words) {
                words = _words;
                new SetWordsTask(getApplicationContext(), words).execute();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsClicked = 0;
                llEngWords.removeAllViews();
                llRusWords.removeAllViews();
                new ClickWordsActivity.SetWordsTask(getApplicationContext(), words).execute();
            }
        });
    }

    class SetWordsTask extends AsyncTask<Void, Void, Map<String, String>> {
        Context context;
        List<EngToRusWord> words;

        public SetWordsTask(Context _context, List<EngToRusWord> _words) {
            context = _context;
            words = _words;
        }

        @Override
        protected Map<String, String> doInBackground(Void... voids) {
            Map<String, String> wordsAndTranslation = new HashMap<>();
            while (wordsAndTranslation.size() < amountOfWords) {
                int randomWord = (int) (Math.random() * words.size());
                int randomTranslation = (int) (Math.random() * words.get(randomWord).translations.size());
                String word = words.get(randomWord).word.getSpelling();
                String translation = words.get(randomWord).translations.get(randomTranslation).getSpelling();
                wordsAndTranslation.put(word, translation);
            }
            return wordsAndTranslation;
        }

        @Override
        protected void onPostExecute(Map<String, String> wordsAndTranslation) {
            super.onPostExecute(wordsAndTranslation);
            Set<String> words = wordsAndTranslation.keySet();
            List<TextView> translations = new ArrayList<>();
            Iterator iterator = words.iterator();
            while (iterator.hasNext()) {
                TextView engWord = new TextView(context);
                TextView rusWord = new TextView(context);

                String word = (String) iterator.next();
                engWord.setText(word);
                engWord.setTag(word);
                rusWord.setText(wordsAndTranslation.get(word));
                rusWord.setTag(word);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                engWord.setLayoutParams(params);
                rusWord.setLayoutParams(params);
                engWord.setTextSize(25);
                rusWord.setTextSize(25);

                engWord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWordClick(v);
                    }
                });
                llEngWords.addView(engWord);
                translations.add(rusWord);
            }
            int translationSize = translations.size();
            for (int i = 0; i < translationSize; i++) {
                int randomWord = (int) (Math.random() * translations.size());
                TextView word = translations.remove(randomWord);
                word.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onWordClick(v);
                    }
                });
                llRusWords.addView(word);
            }
        }
    }

    public void onWordClick(View v) {
        String tag = v.getTag().toString();
        int selectedColor = getResources().getColor(R.color.selected);
        LinearLayout parent = (LinearLayout) v.getParent();
        switch(wordsClicked) {
            case 0: {
                v.setBackgroundColor(selectedColor);
                wordsClicked++;
                first = (TextView) v;
                break;
            }
            case 1: {
                v.setBackgroundColor(selectedColor);
                LinearLayout secondParent = (LinearLayout) v.getParent();
                LinearLayout firstParent = (LinearLayout) first.getParent();
                if (firstParent.getId() != secondParent.getId()) {
                    second = (TextView) v;
                    wordsClicked++;
                    new WordMatchTask(first, second).execute(1);
                }
                else {
                    first.setBackgroundColor(emptyColor);
                    first = (TextView) v;
                }
                break;
            }
        }
    }

    class WordMatchTask extends AsyncTask<Integer, Void, Void> {
        TextView first, second;

        public WordMatchTask(TextView _first, TextView _second) {
            super();
            this.first = _first;
            this.second = _second;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            if (first.getTag().equals(second.getTag())){
                first.setVisibility(View.INVISIBLE);
                second.setVisibility(View.INVISIBLE);
            }
            else{
                first.setBackgroundColor(emptyColor);
                second.setBackgroundColor(emptyColor);
            }
            wordsClicked = 0;
        }
    }
}