package com.example.glossong.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
    WordViewModel wordViewModel;

    EngToRusWord engToRusWord;

    public static WordDialog newInstance(Long engWordId) {
        WordDialog myWordDialog = new WordDialog();
        Bundle args = new Bundle();
        args.putLong("engWordId", engWordId);
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

        ImageButton closeButton = view.findViewById(R.id.closeButton);
        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        TextView engWordTV = view.findViewById(R.id.engWord);
        RecyclerView translationList = view.findViewById(R.id.translationList);
        translationList.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        Long engWordId = getArguments().getLong("engWordId");
        WordDialog wordDialog = this;

        WordViewModel wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngToRusWordsByEngId(engWordId).observe(this, new Observer<EngToRusWord>() {
            @Override
            public void onChanged(EngToRusWord _engToRusWord) {
                engToRusWord = _engToRusWord;
                engWordTV.setText(engToRusWord.word.getSpelling());

                TranslationsAdapter adapter = new TranslationsAdapter(getContext(), engToRusWord, wordDialog, getFragmentManager());
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
        return view;
    }

    @Override
    public void onClick(View v) {
        MyAlertDialog dialog = new MyAlertDialog();
        dialog.setItems(engToRusWord, this);
        dialog.show(getFragmentManager(), "deleteWordAlert");
    }
}
