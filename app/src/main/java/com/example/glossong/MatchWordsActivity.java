package com.example.glossong;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.listener.MyDragListener;
import com.example.glossong.model.EngToRusWord;
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

    WordViewModel wordViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_words);

        llMain = findViewById(R.id.linearLayoutMain);
        checkButton = findViewById(R.id.checkButton);

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> words) {
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
                    String userAnswer = (String) textView.getTag();
                    int rightColor = getResources().getColor(R.color.green), wrongColor = getResources().getColor(R.color.red);

                    Log.d("mytag", "right answer:" + rightAnswer + "\nuser answer: " + userAnswer);
                    if (rightAnswer.equals(userAnswer)) {
                        line.setBackgroundColor(rightColor);
                        textView.setBackgroundColor(rightColor);
                    }
                    else {
                        line.setBackgroundColor(wrongColor);
                        textView.setBackgroundColor(wrongColor);
                    }
                }
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
            while (wordsAndTranslation.size() < words.size()) {
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
                leftPart.setLayoutParams(params);
                rightPart.setLayoutParams(params);
                rightPart.setOrientation(LinearLayout.HORIZONTAL);

                leftPart.setTextSize(25);
                textView.setTextSize(25);
                params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                line.setLayoutParams(params);
                line.setOrientation(LinearLayout.HORIZONTAL);
                textView.setLayoutParams(params);

//                int color = getResources().getColor(R.color.purple_200);
//                leftPart.setBackgroundColor(color);
//                color = getResources().getColor(R.color.teal_200);
//                rightPart.setBackgroundColor(color);

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