package com.example.glossong;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.glossong.dao.NoteDAO;
import com.example.glossong.model.Note;
import com.example.glossong.model.NoteAndSong;
import com.example.glossong.model.NoteWithSongAndArtist;

import java.util.List;

public class NoteRepository {
    NoteDAO noteDAO;
    LiveData<List<NoteWithSongAndArtist>> notes;

    public NoteRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.noteDAO = myRoomDB.noteDAO();
        this.notes = noteDAO.selectAllNotesWithSong();
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

    public LiveData<List<NoteWithSongAndArtist>> getNotes() {
        return this.notes;
    }

    public LiveData<Note> getNoteBySongId(Long id) {
        return this.noteDAO.findNoteBySongId(id);
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
