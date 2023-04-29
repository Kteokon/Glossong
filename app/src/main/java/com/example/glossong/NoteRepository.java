package com.example.glossong;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.glossong.dao.NoteDAO;
import com.example.glossong.dao.SongDAO;
import com.example.glossong.model.Note;
import com.example.glossong.model.Song;
import com.example.glossong.tuple.NoteTuple;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class NoteRepository {
    NoteDAO noteDAO;

    public NoteRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.noteDAO = myRoomDB.noteDAO();
    }

    public void insert(Note note) {
        new NoteRepository.InsertNoteTask(noteDAO).execute(note);
    }

    public void update(Note note) {
        new NoteRepository.UpdateNoteTask(noteDAO).execute(note);
    }

    public void delete(Note note) {
        new NoteRepository.DeleteNoteTask(noteDAO).execute(note);
    }

    public LiveData<Note> getNoteBySongId(Long id) {
        return this.noteDAO.findBySongId(id);
    }

    public static class InsertNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private InsertNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.insert(notes[0]);
            return null;
        }
    }

    public static class UpdateNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private UpdateNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.update(notes[0]);
            return null;
        }
    }

    public static class DeleteNoteTask extends AsyncTask<Note, Void, Void> {
        private NoteDAO noteDAO;

        private DeleteNoteTask(NoteDAO noteDAO) {
            this.noteDAO = noteDAO;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDAO.delete(notes[0]);
            return null;
        }
    }
}
