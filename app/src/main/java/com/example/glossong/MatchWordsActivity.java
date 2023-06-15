package com.example.glossong;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.listener.MyDragListener;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.TaskSettings;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MatchWordsActivity extends AppCompatActivity {
    LinearLayout llMain;
    Button checkButton;
    ScrollView scrollView;

    WordViewModel wordViewModel;

    List<EngToRusWord> words;
    Long amountOfWords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_words);

        llMain = findViewById(R.id.linearLayoutMain);
        checkButton = findViewById(R.id.checkButton);
        scrollView = findViewById(R.id.scrollView);

        amountOfWords = TaskSettings.amountOfWords;

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> _words) {
                words = _words;
                new SetWordsTask(getApplicationContext(), words).execute();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < llMain.getChildCount(); i++) {
                    LinearLayout line = (LinearLayout) llMain.getChildAt(i);
                    String rightAnswer = (String) line.getTag();
                    LinearLayout rightPart = (LinearLayout) line.getChildAt(1);
                    TextView textView = (TextView) rightPart.getChildAt(0);
                    textView.setOnLongClickListener(null);
                    String userAnswer = (String) textView.getTag();

                    if (rightAnswer.equals(userAnswer)) {
                        textView.setBackgroundResource(R.drawable.right);
                    }
                    else {
                        textView.setBackgroundResource(R.drawable.wrong);
                    }
                }
                checkButton.setVisibility(View.GONE);
                Button exitButton = findViewById(R.id.exitButton);
                Button restartButton = findViewById(R.id.restartButton);
                exitButton.setVisibility(View.VISIBLE);
                restartButton.setVisibility(View.VISIBLE);
                exitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                restartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkButton.setVisibility(View.VISIBLE);
                        exitButton.setVisibility(View.GONE);
                        restartButton.setVisibility(View.GONE);
                        llMain.removeAllViews();
                        new SetWordsTask(getApplicationContext(), words).execute();
                    }
                });
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
            Set<String> words = wordsAndTranslation.keySet(); // список слов на английском
            List<LinearLayout> lines = new ArrayList<>();
            List<LinearLayout> rightParts = new ArrayList<>();
            List<TextView> translations = new ArrayList<>();
            Iterator iterator = words.iterator();
            while (iterator.hasNext()) {
                LinearLayout line = new LinearLayout(context);
                TextView leftPart = new TextView(context);
                LinearLayout rightPart = new LinearLayout(context);
                TextView textView = new TextView(context);

                String word = (String) iterator.next();

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );
                rightPart.setLayoutParams(params);
                params.setMargins(20, 10, 10, 0);
                leftPart.setLayoutParams(params);
                leftPart.setPadding(30, 30, 30, 30);
                rightPart.setOrientation(LinearLayout.HORIZONTAL);

                leftPart.setTextSize(25);
                textView.setTextSize(25);
                leftPart.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.proximanova_bold));
                textView.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.proximanova_bold));
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode &
                                Configuration.UI_MODE_NIGHT_MASK;
                if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                    leftPart.setTextColor(getColor(R.color.floral_white));
                }
                else {
                    leftPart.setTextColor(getColor(R.color.jet));
                }
                textView.setTextColor(getColor(R.color.jet));
                line.setLayoutParams(params);
                line.setOrientation(LinearLayout.HORIZONTAL);
                params.setMargins(10, 10, 20, 0);
                textView.setLayoutParams(params);
                textView.setBackgroundResource(R.drawable.rounded_corner);
                textView.setPadding(30, 30, 30, 30);

                leftPart.setText(word);
                line.setTag(word);
                textView.setTag(word);
                textView.setText(wordsAndTranslation.get(word));

                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.d("mytag", "On long click");
                        String clipText = v.getTag().toString();
                        ClipData.Item item = new ClipData.Item(clipText);
                        String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                        ClipData data = new ClipData(clipText, mimeType, item);

                        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(data, dragShadowBuilder, v, 0);

                        v.setVisibility(View.INVISIBLE);
                        return true;
                    }
                });

                line.addView(leftPart);
                lines.add(line);
                translations.add(textView);
                rightPart.setOnDragListener(new MyDragListener());
                rightParts.add(rightPart);
            }

            for (int i = 0; i < lines.size(); i++) {
                int randomWord = (int) (Math.random() * translations.size());

                TextView textView = translations.remove(randomWord);
                Log.d("mytag", textView.getText().toString());

                rightParts.get(i).addView(textView);
                rightParts.get(i).setOnDragListener(new MyDragListener());
                lines.get(i).addView(rightParts.get(i));
                llMain.addView(lines.get(i));
            }
        }
    }
}