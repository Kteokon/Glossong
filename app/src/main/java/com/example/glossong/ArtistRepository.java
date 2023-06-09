package com.example.glossong;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.glossong.dao.ArtistDAO;
import com.example.glossong.model.Artist;
import com.example.glossong.model.ArtistAndSongs;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ArtistRepository {
    private ArtistDAO artistDAO;

    public ArtistRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.artistDAO = myRoomDB.artistDAO();
    }

    public Long insert(Artist artist) {
        InsertArtistTask task = new InsertArtistTask(artistDAO);
        Long id = 1L;
        try {
            id = task.execute(artist).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public void update(Artist artist) {
        new ArtistRepository.UpdateArtistTask(artistDAO).execute(artist);
    }

    public void delete(Artist artist) {
        new ArtistRepository.DeleteArtistTask(artistDAO).execute(artist);
    }

    public Long getArtistIdByName(String name){
        GetArtistIdByNameTask task = new GetArtistIdByNameTask(artistDAO);
        Long id = null;
        try {
            id = task.execute(name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id;
    }

    public LiveData<ArtistAndSongs> getArtistByName(String name){
        GetArtistByNameTask task = new GetArtistByNameTask(artistDAO);
        LiveData<ArtistAndSongs> artist = null;
        try {
            artist = task.execute(name).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return artist;
    }

    public static class InsertArtistTask extends AsyncTask<Artist, Void, Long> {
        private ArtistDAO artistDAO;

        private InsertArtistTask(ArtistDAO artistDAO) {
            this.artistDAO = artistDAO;
        }

        @Override
        protected Long doInBackground(Artist... artists) {
            Long id = artistDAO.insert(artists[0]);
            return id;
        }
    }

    public static class UpdateArtistTask extends AsyncTask<Artist, Void, Void> {
        private ArtistDAO artistDAO;

        private UpdateArtistTask(ArtistDAO artistDAO) {
            this.artistDAO = artistDAO;
        }

        @Override
        protected Void doInBackground(Artist... artists) {
            artistDAO.update(artists[0]);
            return null;
        }
    }

    public static class DeleteArtistTask extends AsyncTask<Artist, Void, Void> {
        private ArtistDAO artistDAO;

        private DeleteArtistTask(ArtistDAO artistDAO) {
            this.artistDAO = artistDAO;
        }

        @Override
        protected Void doInBackground(Artist... artists) {
            artistDAO.delete(artists[0]);
            return null;
        }
    }

    public static class GetArtistIdByNameTask extends AsyncTask<String, Void, Long> {
        private ArtistDAO artistDAO;

        private GetArtistIdByNameTask(ArtistDAO artistDAO) {
            this.artistDAO = artistDAO;
        }

        @Override
        protected Long doInBackground(String... strings) {
            Long id = artistDAO.findArtistIdByName(strings[0]);
            return id;
        }
    }

    public static class GetArtistByNameTask extends AsyncTask<String, Void, LiveData<ArtistAndSongs>> {
        private ArtistDAO artistDAO;

        private GetArtistByNameTask(ArtistDAO artistDAO) {
            this.artistDAO = artistDAO;
        }

        @Override
        protected LiveData<ArtistAndSongs> doInBackground(String... strings) {
            LiveData<ArtistAndSongs> artist = artistDAO.findArtistByName(strings[0]);
            return artist;
        }
    }
}
