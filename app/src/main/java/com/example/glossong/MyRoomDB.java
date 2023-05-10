package com.example.glossong;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.glossong.dao.ArtistDAO;
import com.example.glossong.dao.NoteDAO;
import com.example.glossong.dao.SongDAO;
import com.example.glossong.dao.WordDAO;
import com.example.glossong.model.Artist;
import com.example.glossong.model.Dictionary;
import com.example.glossong.model.EngWord;
import com.example.glossong.model.RusWord;
import com.example.glossong.model.Song;
import com.example.glossong.model.Note;
import com.example.glossong.model.Translation;

@Database(entities = {Artist.class,
        Dictionary.class,
        EngWord.class,
        Note.class,
        RusWord.class,
        Song.class,
        Translation.class}, version = 1)
public abstract class MyRoomDB extends RoomDatabase {
    abstract SongDAO songDAO();
    abstract ArtistDAO artistDAO();
    abstract NoteDAO noteDAO();
    abstract WordDAO wordDAO();

    private static final String DB_NAME = "glossong.db";
    private static volatile MyRoomDB INSTANCE = null;

    static MyRoomDB create(Context ctxt, boolean memoryOnly) {
        Builder<MyRoomDB> b;
        if (memoryOnly) {
            b = Room.inMemoryDatabaseBuilder(ctxt.getApplicationContext(),
                    MyRoomDB.class);
        }
        else {
            b = Room.databaseBuilder(ctxt.getApplicationContext(), MyRoomDB.class,
                    DB_NAME);
        }
        return(b.build());
    }

    synchronized static MyRoomDB get(Context ctxt) {
        if (INSTANCE == null) {
            INSTANCE = create(ctxt, false);
        }
        return(INSTANCE);
    }
}
