package com.example.glossong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.glossong.fragment.CardsInformationFragment;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.TaskSettings;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SwapCardsActivity extends AppCompatActivity {
    private WordViewModel wordViewModel;
    private RecyclerView translationList;

    CardView card;
    TextView engWordTV;
    Button seeTranslations, startAgainButton;

    List<EngToRusWord> words = new ArrayList<>();
    List<EngToRusWord> cardWords = new ArrayList<>();
    EngToRusWord displayed;
    CardTranslationsAdapter adapter;
    Long amountOfWords;
    float currentX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_cards);

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

        card = findViewById(R.id.card);
        engWordTV = findViewById(R.id.engWord);
        seeTranslations = findViewById(R.id.seeTranslations);
        startAgainButton = findViewById(R.id.startAgainButton);
        translationList = findViewById(R.id.translationList);
        translationList.setLayoutManager(new LinearLayoutManager(this));
        amountOfWords = TaskSettings.amountOfWords;

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> _words) {
                words = _words;
                start();
            }
        });

        seeTranslations.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (translationList.getVisibility() == View.GONE) {
                    translationList.setVisibility(View.VISIBLE);
                    seeTranslations.setText("Скрыть переводы");
                }
                return true;
            }
        });

        seeTranslations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translationList.getVisibility() == View.VISIBLE) {
                    translationList.setVisibility(View.GONE);
                    seeTranslations.setText("Посмотреть переводы");
                }
            }
        });

        startAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        card.setOnTouchListener(new View.OnTouchListener() {
            @Override
            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                float cardWidth = card.getWidth();
                float cardStart = (((float) displayMetrics.widthPixels) / 2) - (cardWidth / 2);
                float cardEnd = cardStart + cardWidth;
                switch(event.getAction()) {
                    case MotionEvent.ACTION_MOVE: {
                        float newX = event.getRawX();
                        if (newX < cardStart + (cardWidth / 2)) {
                            card.animate().x(Math.min(cardStart, newX - (cardWidth / 2)))
                                    .setDuration(0)
                                    .start();
                        }
                        else {
                            card.animate().x(Math.min(cardEnd, newX - (cardWidth / 2)))
                                .setDuration(0)
                                .start();
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        currentX = card.getX();
                        float hideX = cardStart;
                        String swappedTo = "";
                        if (currentX < 0) {
                            hideX = 0 - 2 * cardWidth;
                            swappedTo = "left";
                        }
                        else {
                            if (currentX + cardWidth > displayMetrics.widthPixels) {
                                hideX = displayMetrics.widthPixels + 2 * cardWidth;
                                swappedTo = "right";
                            }
                        }
                        String finalSwappedTo = swappedTo;
                        card.animate().x(hideX).setDuration(150)
                            .setListener(new AnimatorListenerAdapter() {
                                @SuppressLint("ClickableViewAccessibility")
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (!finalSwappedTo.equals("")) {
                                        int next = cardWords.indexOf(displayed);
                                        if (finalSwappedTo.equals("right")) {
                                            cardWords.remove(displayed);
                                        }
                                        else { next++; }
                                        if (next >= cardWords.size()) { next = 0; }
                                        int finalNext = next;
                                        card.animate().x(cardStart).setDuration(1)
                                                .setListener(new AnimatorListenerAdapter() {
                                                    @Override
                                                    public void onAnimationEnd(Animator animation) {
                                                        super.onAnimationEnd(animation);

                                                        if (cardWords.size() != 0) {
                                                            displayed = cardWords.get(finalNext);
                                                            EngWord engWord = displayed.word;
                                                            List<RusWord> translations = displayed.translations;

                                                            engWordTV.setText(engWord.getSpelling());
                                                            adapter.setTranslations(translations);
                                                            translationList.setAdapter(adapter);
                                                        }
                                                        else {
                                                            card.setVisibility(View.GONE);
                                                            startAgainButton.setVisibility(View.VISIBLE);
                                                        }
                                                        currentX = 0;
                                                    }
                                                })
                                                .start();
                                        seeTranslations.setText("Посмотреть переводы");
                                        translationList.setVisibility(View.GONE);
                                    }
                                }
                            })
                            .start();
                        break;
                    }
                }
                return true;
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
                DialogFragment dialog = CardsInformationFragment.newInstance(getResources().getString(R.string.cards_information));
                dialog.show(getSupportFragmentManager(), "cardsInformationFragment");
                return true;
            }
        });
        return true;
    }

    private void start() {
        Collections.shuffle(words);
        cardWords = new ArrayList<>();

        for (int i = 0; i < amountOfWords; i++) {
            cardWords.add(words.get(i));
        }
        displayed = cardWords.get(0);

        EngWord engWord = displayed.word;
        List<RusWord> translations = displayed.translations;

        engWordTV.setText(engWord.getSpelling());
        if (adapter == null) {
            adapter = new CardTranslationsAdapter(getApplicationContext(), translations);
        }
        else {
            adapter.setTranslations(translations);
            card.setVisibility(View.VISIBLE);
            startAgainButton.setVisibility(View.GONE);
        }
        translationList.setAdapter(adapter);
    }
}