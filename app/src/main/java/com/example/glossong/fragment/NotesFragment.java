package com.example.glossong.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.glossong.NoteListAdapter;
import com.example.glossong.R;
import com.example.glossong.model.NoteWithSongAndArtist;
import com.example.glossong.viewmodel.NoteViewModel;

import java.util.List;

public class NotesFragment extends Fragment {
    private NoteViewModel noteViewModel;
    private RecyclerView noteList;

    public NotesFragment() {
        // Required empty public constructor
    }

    public static NotesFragment newInstance() {
        NotesFragment fragment = new NotesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        noteList = view.findViewById(R.id.notesList);
        noteList.setLayoutManager(new LinearLayoutManager(getContext()));

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteViewModel.getNotes().observe(getViewLifecycleOwner(), new Observer<List<NoteWithSongAndArtist>>() {
            @Override
            public void onChanged(List<NoteWithSongAndArtist> notes) {
                NoteListAdapter adapter = new NoteListAdapter(getContext(), notes);
                noteList.setAdapter(adapter);
            }
        });
        return view;
    }
}