package com.example.glossong.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.glossong.ClickWordsActivity;
import com.example.glossong.MatchWordsActivity;
import com.example.glossong.R;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.List;

public class ChooseTaskDialog extends DialogFragment implements View.OnClickListener {
    Long dictionarySize;
    public static ChooseTaskDialog newInstance() {
        return new ChooseTaskDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WordViewModel wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        wordViewModel.getEngWords().observe(this, new Observer<List<EngToRusWord>>() {
            @Override
            public void onChanged(List<EngToRusWord> words) {
                dictionarySize = Long.valueOf(words.size());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_task_dialog, container, false);

        Button matchWordsButton = view.findViewById(R.id.matchWordsButton);
        Button clickWordsButton = view.findViewById(R.id.clickWordsButton);

        matchWordsButton.setOnClickListener(this);
        clickWordsButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (this.dictionarySize < 2) {
            FragmentManager manager = getActivity().getSupportFragmentManager();
            MyHintDialog dialog = new MyHintDialog();
            dialog.show(manager, "toTaskHint");
        }
        else {
            int id = v.getId();
            switch (id) {
                case R.id.matchWordsButton: {
                    Intent intent = new Intent(getContext(), MatchWordsActivity.class);
                    startActivity(intent);
                    break;
                }
                case R.id.clickWordsButton: {
                    Intent intent = new Intent(getContext(), ClickWordsActivity.class);
                    startActivity(intent);
                    break;
                }
            }
        }
    }
}
