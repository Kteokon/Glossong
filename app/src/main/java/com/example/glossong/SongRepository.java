package com.example.glossong;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.glossong.dao.SongDAO;
import com.example.glossong.model.SongAndArtist;
import com.example.glossong.model.Song;

import java.util.List;

public class SongRepository {
    private SongDAO songDAO;
    private LiveData<List<SongAndArtist>> songs;

    public SongRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.songDAO = myRoomDB.songDAO();
        this.songs = songDAO.getSongsWithArtists();
    }

    public void insert(Song song) {
        new InsertSongTask(songDAO).execute(song);
    }

    public void update(Song song) {
        new UpdateSongTask(songDAO).execute(song);
    }

    public void delete(Song song) {
        new DeleteSongTask(songDAO).execute(song);
    }

    public LiveData<List<SongAndArtist>> getSongs() {
        return songs;
    }

    public static class InsertSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private InsertSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.insert(songs[0]);
            return null;
        }
    }

    public static class UpdateSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private UpdateSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.update(songs[0]);
            return null;
        }
    }

    public static class DeleteSongTask extends AsyncTask<Song, Void, Void> {
        private SongDAO songDAO;

        private DeleteSongTask(SongDAO songDAO) {
            this.songDAO = songDAO;
        }

        @Override
        protected Void doInBackground(Song... songs) {
            songDAO.delete(songs[0]);
            return null;
        }
    }
}
