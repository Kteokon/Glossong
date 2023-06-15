package com.example.glossong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.glossong.R;
import com.example.glossong.SongListAdapter;
import com.example.glossong.model.MyMediaPlayer;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.viewmodel.SongViewModel;

import java.util.List;

public class AllSongsFragment extends Fragment {
    private SongViewModel songViewModel;
    private RecyclerView songList;

    public AllSongsFragment() {
        // Required empty public constructor
    }

    public static AllSongsFragment newInstance() {
        AllSongsFragment fragment = new AllSongsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_songs, container, false);

        songList = view.findViewById(R.id.songList);
        songList.setLayoutManager(new LinearLayoutManager(getContext()));

        songViewModel = new ViewModelProvider(this).get(SongViewModel.class);
        songViewModel.getSongs().observe(getViewLifecycleOwner(), new Observer<List<SongAndArtist>>() {
            @Override
            public void onChanged(List<SongAndArtist> _songs) {
                SongListAdapter adapter = new SongListAdapter(getContext(), _songs);
                songList.setAdapter(adapter);

                if (MyMediaPlayer.nowPlaying != -1) {
                    MyMediaPlayer.songs = _songs;
                }
            }
        });
        return view;
    }
}