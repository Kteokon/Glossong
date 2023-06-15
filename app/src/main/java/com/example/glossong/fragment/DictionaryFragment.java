package com.example.glossong.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.glossong.DictionaryAdapter;
import com.example.glossong.Functions;
import com.example.glossong.R;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Translation;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.viewmodel.WordViewModel;

import java.util.List;

public class DictionaryFragment extends Fragment {
    ViewModelStoreOwner viewModelStoreOwner;

    private WordViewModel wordViewModel;
    private RecyclerView wordList;

    private Long songId = -1L;

    public DictionaryFragment() {
        // Required empty public constructor
    }

    public static DictionaryFragment newInstance(Long songId) {
        DictionaryFragment fragment = new DictionaryFragment();
        Bundle args = new Bundle();
        args.putLong("songId", songId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            songId = getArguments().getLong("songId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dictionary, container, false);
        viewModelStoreOwner = this;

        wordList = view.findViewById(R.id.wordList);
        wordList.setLayoutManager(new LinearLayoutManager(getContext()));

        wordViewModel = new ViewModelProvider(this).get(WordViewModel.class);
        if (songId == -1L) {
            wordViewModel.getEngWords().observe(getViewLifecycleOwner(), new Observer<List<EngToRusWord>>() {
                @Override
                public void onChanged(List<EngToRusWord> words) {
                    DictionaryAdapter adapter = new DictionaryAdapter(getContext(), words, songId, getChildFragmentManager());
                    wordList.setAdapter(adapter);
                }
            });
        }
        else {
            wordViewModel.getSongDictionary(songId).observe(getViewLifecycleOwner(), new Observer<List<EngToRusWord>>() {
                @Override
                public void onChanged(List<EngToRusWord> words) {
                    DictionaryAdapter adapter = new DictionaryAdapter(getContext(), words, songId, getChildFragmentManager());
                    wordList.setAdapter(adapter);
                }
            });
        }
        return view;
    }

    public void delete(EngToRusWord engToRusWord) {
        Functions.deleteWord(viewModelStoreOwner, engToRusWord, this.songId);
    }
}