package com.example.glossong;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.glossong.dao.WordDAO;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngToRusWord;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusToEngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Translation;
import com.example.glossong.model.WordInSongs;
import com.example.glossong.tuple.WordTuple;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class WordRepository {
    WordDAO wordDAO;
    LiveData<List<EngToRusWord>> engWords;

    public WordRepository(Application application) {
        MyRoomDB myRoomDB = MyRoomDB.get(application);
        this.wordDAO = myRoomDB.wordDAO();
        this.engWords = wordDAO.findEngToRusWords();
    }
    // region Insert
    public long insert(EngWord word) {
        InsertEngWordTask task = new InsertEngWordTask(wordDAO);
        long wordId = -1;
        try {
            wordId = task.execute(word).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordId;
    }

    public long insert(RusWord word) {
        InsertRusWordTask task = new InsertRusWordTask(wordDAO);
        long wordId = -1;
        try {
            wordId = task.execute(word).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordId;
    }

    public void insert(Translation translation) {
        new InsertTranslationTask(wordDAO).execute(translation);
    }

    public void insert(Dictionary dictionary) {
        new InsertDictionaryTask(wordDAO).execute(dictionary);
    }
    // endregion Insert
    // region Delete
    public int delete(EngWord word) {
        DeleteEngWordTask task = new DeleteEngWordTask(wordDAO);
        int wordId = -1;
        try {
            wordId = task.execute(word).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordId;
    }

    public int delete(RusWord word) {
        DeleteRusWordTask task = new DeleteRusWordTask(wordDAO);
        int wordId = -1;
        try {
            wordId = task.execute(word).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return wordId;
    }

    public void delete(Translation translation) {
        new DeleteTranslationTask(wordDAO).execute(translation);
    }

    public void delete(Dictionary dictionary) {
        new DeleteDictionaryTask(wordDAO).execute(dictionary);
    }
    //endregion Delete

    public LiveData<List<EngToRusWord>> getAllEngWords() {
        return this.engWords;
    }

    public List<WordTuple> getWordBySpelling(String spelling) {
        FindWordBySpellingTask task = new FindWordBySpellingTask(wordDAO);
        List<WordTuple> word = null;
        try {
            word = task.execute(spelling).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return word;
    }

    public EngToRusWord getEngToRusWordsBySpelling(String spelling) {
        FindEngToRusWordsBySpellingTask task = new FindEngToRusWordsBySpellingTask(wordDAO);
        EngToRusWord word = null;
        try {
            word = task.execute(spelling).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return word;
    }

    public RusToEngWord getRusToEngWordsBySpelling(String spelling) {
        FindRusToEngWordsBySpellingTask task = new FindRusToEngWordsBySpellingTask(wordDAO);
        RusToEngWord word = null;
        try {
            word = task.execute(spelling).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return word;
    }

    public WordInSongs getWordInSongs(Long wordId) {
        FindWordInSongsTask task = new FindWordInSongsTask(wordDAO);
        WordInSongs word = null;
        try {
            word = task.execute(wordId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return word;
    }

    public List<Dictionary> getSongDictionaryByWordId(Long songId, Long engWordId) {
        FindSongDictionaryByWordIdTask task = new FindSongDictionaryByWordIdTask(wordDAO, songId);
        List<Dictionary> dictionary = null;
        try {
            dictionary = task.execute(engWordId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    public LiveData<List<EngToRusWord>> getSongDictionary(Long songId) {
        FindSongDictionaryTask task = new FindSongDictionaryTask(wordDAO);
        LiveData<List<EngToRusWord>> dictionary = null;
        try {
            dictionary = task.execute(songId).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dictionary;
    }

    // region AsyncTasks
    public static class InsertEngWordTask extends AsyncTask<EngWord, Void, Long> {
        private WordDAO wordsDAO;

        private InsertEngWordTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Long doInBackground(EngWord... words) {
            long wordId = wordsDAO.insert(words[0]);
            return wordId;
        }
    }

    public static class InsertRusWordTask extends AsyncTask<RusWord, Void, Long> {
        private WordDAO wordsDAO;

        private InsertRusWordTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Long doInBackground(RusWord... words) {
            long wordId = wordsDAO.insert(words[0]);
            return wordId;
        }
    }

    public static class InsertTranslationTask extends AsyncTask<Translation, Void, Void> {
        private WordDAO wordsDAO;

        private InsertTranslationTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Void doInBackground(Translation... translations) {
            wordsDAO.insert(translations[0]);
            return null;
        }
    }

    public static class InsertDictionaryTask extends AsyncTask<Dictionary, Void, Void> {
        private WordDAO wordsDAO;

        private InsertDictionaryTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Void doInBackground(Dictionary... dictionaries) {
            wordsDAO.insert(dictionaries[0]);
            return null;
        }
    }

    public static class DeleteEngWordTask extends AsyncTask<EngWord, Void, Integer> {
        private WordDAO wordsDAO;

        private DeleteEngWordTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Integer doInBackground(EngWord... words) {
            int wordId = wordsDAO.delete(words[0]);
            return wordId;
        }
    }

    public static class DeleteRusWordTask extends AsyncTask<RusWord, Void, Integer> {
        private WordDAO wordsDAO;

        private DeleteRusWordTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Integer doInBackground(RusWord... words) {
            int wordId = wordsDAO.delete(words[0]);
            return wordId;
        }
    }

    public static class DeleteTranslationTask extends AsyncTask<Translation, Void, Void> {
        private WordDAO wordsDAO;

        private DeleteTranslationTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Void doInBackground(Translation... translations) {
            wordsDAO.delete(translations[0]);
            return null;
        }
    }

    public static class DeleteDictionaryTask extends AsyncTask<Dictionary, Void, Void> {
        private WordDAO wordsDAO;

        private DeleteDictionaryTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected Void doInBackground(Dictionary... dictionaries) {
            wordsDAO.delete(dictionaries[0]);
            return null;
        }
    }

    public static class FindWordBySpellingTask extends AsyncTask<String, Void, List<WordTuple>> {
        private WordDAO wordsDAO;

        private FindWordBySpellingTask(WordDAO _wordsDAO) {
            wordsDAO = _wordsDAO;
        }

        protected List<WordTuple> doInBackground(String... strings) {
            List<WordTuple> word = wordsDAO.findWordBySpelling(strings[0]);
            return word;
        }
    }

    public static class FindEngToRusWordsBySpellingTask extends AsyncTask<String, Void, EngToRusWord> {
        private WordDAO wordsDAO;

        private FindEngToRusWordsBySpellingTask(WordDAO _wordsDAO) {
            wordsDAO = _wordsDAO;
        }

        @Override
        protected EngToRusWord doInBackground(String... strings) {
            EngToRusWord word = wordsDAO.findEngToRusWordsBySpelling(strings[0]);
            return word;
        }
    }

    public static class FindRusToEngWordsBySpellingTask extends AsyncTask<String, Void, RusToEngWord> {
        private WordDAO wordsDAO;

        private FindRusToEngWordsBySpellingTask(WordDAO _wordsDAO) {
            wordsDAO = _wordsDAO;
        }

        @Override
        protected RusToEngWord doInBackground(String... strings) {
            RusToEngWord word = wordsDAO.findRusToEngWordsBySpelling(strings[0]);
            return word;
        }
    }

    public static class FindWordInSongsTask extends AsyncTask<Long, Void, WordInSongs> {
        private WordDAO wordsDAO;

        private FindWordInSongsTask(WordDAO _wordsDAO) {
            wordsDAO = _wordsDAO;
        }

        @Override
        protected WordInSongs doInBackground(Long... longs) {
            WordInSongs word = wordsDAO.findWordInSongs(longs[0]);
            return word;
        }
    }

    public static class FindSongDictionaryTask extends AsyncTask<Long, Void, LiveData<List<EngToRusWord>>> {
        private WordDAO wordsDAO;

        private FindSongDictionaryTask(WordDAO wordsDAO) {
            this.wordsDAO = wordsDAO;
        }

        @Override
        protected LiveData<List<EngToRusWord>> doInBackground(Long... longs) {
            Long songId = longs[0];
            LiveData<List<EngToRusWord>> dictionary = wordsDAO.findSongDictionary(songId);
            return dictionary;
        }
    }

    public static class FindSongDictionaryByWordIdTask extends AsyncTask<Long, Void, List<Dictionary>> {
        private WordDAO wordsDAO;
        private Long songId;

        private FindSongDictionaryByWordIdTask(WordDAO wordsDAO, Long songId) {
            this.wordsDAO = wordsDAO;
            this.songId = songId;
        }

        @Override
        protected List<Dictionary> doInBackground(Long... longs) {
            Long engWordId = longs[0];
            List<Dictionary> dictionary = wordsDAO.findSongDictionaryByWordId(songId, engWordId);
            return dictionary;
        }
    }
    //endregion
}
