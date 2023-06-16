package com.example.glossong;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.fragment.CardsInformationFragment;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.TaskSettings;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClickWordsActivity extends AppCompatActivity {
    LinearLayout llEngWords, llRusWords;
    TextView first, second;

    WordViewModel wordViewModel;

    List<EngToRusWord> words;
    int wordsClicked = 0;
    Long amountOfWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_words);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setNavigationIcon(R.drawable.back_square);
        toolbar.setNavigationContentDescription("Вернуться");

        llEngWords = findViewById(R.id.engWords);
        llRusWords = findViewById(R.id.rusWords);
        Button startAgainButton = findViewById(R.id.startAgainButton);

        amountOfWords = TaskSettings.amountOfWords;

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> _words) {
                words = _words;
                new SetWordsTask(getApplicationContext(), words).execute();
            }
        });

        startAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsClicked = 0;
                llEngWords.removeAllViews();
                llRusWords.removeAllViews();
                new ClickWordsActivity.SetWordsTask(getApplicationContext(), words).execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task, menu);

        MenuItem informationButton = menu.findItem(R.id.information);
        informationButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                DialogFragment dialog = CardsInformationFragment.newInstance(getResources().getString(R.string.click_information));
                dialog.show(getSupportFragmentManager(), "cardsInformationFragment");
                return true;
            }
        });
        return true;
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
            Collections.shuffle(words);
            for (int i = 0; i < amountOfWords; i++) {
                String word = words.get(i).word.getSpelling();
                Collections.shuffle(words.get(i).translations);
                String translation = words.get(i).translations.get(0).getSpelling();
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
                params.setMargins(20, 10, 10, 0);
                engWord.setLayoutParams(params);
                params.setMargins(10, 10, 20, 0);
                rusWord.setLayoutParams(params);
                engWord.setTextSize(25);
                rusWord.setTextSize(25);
                engWord.setPadding(30, 30, 30, 30);
                rusWord.setPadding(30, 30, 30, 30);
                engWord.setBackgroundResource(R.drawable.rounded_corner);
                rusWord.setBackgroundResource(R.drawable.rounded_corner);
                engWord.setTextColor(getColor(R.color.jet));
                rusWord.setTextColor(getColor(R.color.jet));
                engWord.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.proximanova_bold));
                rusWord.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.proximanova_bold));

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
        switch(wordsClicked) {
            case 0: {
                v.setBackgroundResource(R.drawable.selected);
                wordsClicked++;
                first = (TextView) v;
                break;
            }
            case 1: {
                v.setBackgroundResource(R.drawable.selected);
                LinearLayout secondParent = (LinearLayout) v.getParent();
                LinearLayout firstParent = (LinearLayout) first.getParent();
                if (firstParent.getId() != secondParent.getId()) {
                    second = (TextView) v;
                    wordsClicked++;
                    new WordMatchTask(first, second).execute(1);
                }
                else {
                    if (!first.equals(v)) {
                        first.setBackgroundResource(R.drawable.rounded_corner);
                        first = (TextView) v;
                    }
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
                first.setVisibility(View.GONE);
                second.setVisibility(View.GONE);
            }
            else{
                first.setBackgroundResource(R.drawable.rounded_corner);
                second.setBackgroundResource(R.drawable.rounded_corner);
            }
            wordsClicked = 0;
        }
    }
}