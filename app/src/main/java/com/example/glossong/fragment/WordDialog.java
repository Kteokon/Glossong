package com.example.glossong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.glossong.R;
import com.example.glossong.SongListAdapter;
import com.example.glossong.TranslationsAdapter;
import com.example.glossong.listener.ItemClickListener;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Translation;
import com.example.glossong.model.Word;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;
import com.example.glossong.viewmodel.WordViewModel;

import java.io.Serializable;
import java.util.List;

public class WordDialog extends DialogFragment implements View.OnClickListener {
    ViewModelStoreOwner viewModelStoreOwner;

    EngToRusWord engToRusWord;
    Long songId;

    public static WordDialog newInstance(Long engWordId, Long songId) {
        WordDialog myWordDialog = new WordDialog();
        Bundle args = new Bundle();
        args.putLong("engWordId", engWordId);
        args.putLong("songId", songId);
        myWordDialog.setArguments(args);
        return myWordDialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.word_dialog, container, false);

        viewModelStoreOwner = this;

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        TextView engWordTV = view.findViewById(R.id.engWord);
        ImageButton expandButton = view.findViewById(R.id.expand);
        RelativeLayout addTranslationLayout = view.findViewById(R.id.addTranslationLayout);
        EditText translationET = view.findViewById(R.id.translation);
        ImageButton addTranslationButton = view.findViewById(R.id.addTranslation);
        ImageButton closeTranslationButton = view.findViewById(R.id.closeTranslation);
        RecyclerView translationList = view.findViewById(R.id.translationList);
        translationList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        Long engWordId = getArguments().getLong("engWordId");
        songId = getArguments().getLong("songId");
        WordDialog wordDialog = this;

        WordViewModel wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngToRusWordsByEngId(engWordId).observe(this, new Observer<EngToRusWord>() {
            @Override
            public void onChanged(EngToRusWord _engToRusWord) {
                engToRusWord = _engToRusWord;
                engWordTV.setText(engToRusWord.word.getSpelling());

                TranslationsAdapter adapter = new TranslationsAdapter(getContext(), viewModelStoreOwner, engToRusWord, songId, wordDialog, getChildFragmentManager());
                translationList.setAdapter(adapter);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        deleteButton.setOnClickListener(this);

        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandButton.setVisibility(View.GONE);
                addTranslationLayout.setVisibility(View.VISIBLE);
            }
        });

        addTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userTranslation = String.valueOf(translationET.getText());
                String checkTranslation = userTranslation.replace(" ", "");
                if (checkTranslation.equals("")) {
                    Toast.makeText(getContext(), "Введите перевод", Toast.LENGTH_LONG).show();
                }
                else {
                    if (!checkTranslation.matches("^[а-яА-ЯёЁ0-9.,?!:;'\"@#№$%^&*()<>{}~`=+/-]+$")) {
                        Toast.makeText(getContext(), "Перевод должен быть на русском", Toast.LENGTH_LONG).show();
                    }
                    else {
                        List<WordTuple> word = wordViewModel.getWordBySpelling(userTranslation);
                        Long rusWordId = null;
                        if (word.size() == 0 || word.get(0) == null) {
                            RusWord rusWord = new RusWord(userTranslation);
                            rusWordId = wordViewModel.insert(rusWord);
                        }
                        else {
                            rusWordId = word.get(0).wordId;
                        }

                        Translation translation = new Translation();
                        translation.setEngWordId(engWordId);
                        translation.setRusWordId(rusWordId);
                        if (wordViewModel.getTranslation(translation) == null) { // Если связи между словами нет
                            wordViewModel.insert(translation);
                            translationET.setText("");
                        }
                        else {
                            Toast.makeText(getContext(), "Данный перевод уже есть", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });

        closeTranslationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandButton.setVisibility(View.VISIBLE);
                addTranslationLayout.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        MyAlertDialog dialog = new MyAlertDialog();
        dialog.setItems(viewModelStoreOwner, engToRusWord, songId, this);
        dialog.show(getChildFragmentManager(), "deleteWordAlert");
    }
}
